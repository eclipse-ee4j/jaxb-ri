/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.property;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.core.v2.ClassFactory;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeMapPropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.*;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;

/**
 * @author Kohsuke Kawaguchi
 */
final class SingleMapNodeProperty<BeanT,ValueT extends Map> extends PropertyImpl<BeanT> {

    private final Accessor<BeanT,ValueT> acc;
    /**
     * The tag name that surrounds the whole property.
     */
    private final Name tagName;
    /**
     * The tag name that corresponds to the 'entry' element.
     */
    private final Name entryTag;
    private final Name keyTag;
    private final Name valueTag;

    private final boolean nillable;

    private JaxBeanInfo keyBeanInfo;
    private JaxBeanInfo valueBeanInfo;

    /**
     * The implementation class for this property.
     * If the property is null, we create an instance of this class.
     */
    private final Class<? extends ValueT> mapImplClass;

    public SingleMapNodeProperty(JAXBContextImpl context, RuntimeMapPropertyInfo prop) {
        super(context, prop);
        acc = prop.getAccessor().optimize(context);
        this.tagName = context.nameBuilder.createElementName(prop.getXmlName());
        this.entryTag = context.nameBuilder.createElementName("","entry");
        this.keyTag = context.nameBuilder.createElementName("","key");
        this.valueTag = context.nameBuilder.createElementName("","value");
        this.nillable = prop.isCollectionNillable();
        this.keyBeanInfo = context.getOrCreate(prop.getKeyType());
        this.valueBeanInfo = context.getOrCreate(prop.getValueType());

        // infer the implementation class
        //noinspection unchecked
        Class<ValueT> sig = (Class<ValueT>) Utils.REFLECTION_NAVIGATOR.erasure(prop.getRawType());
        mapImplClass = ClassFactory.inferImplClass(sig,knownImplClasses);
        // TODO: error check for mapImplClass==null
        // what is the error reporting path for this part of the code?
    }

    private static final Class[] knownImplClasses = {
        HashMap.class, TreeMap.class, LinkedHashMap.class
    };

    public void reset(BeanT bean) throws AccessorException {
        acc.set(bean,null);
    }


    /**
     * A Map property can never be ID.
     */
    public String getIdValue(BeanT bean) {
        return null;
    }

    public PropertyKind getKind() {
        return PropertyKind.MAP;
    }

    public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
        keyLoader = keyBeanInfo.getLoader(chain.context,true);
        valueLoader = valueBeanInfo.getLoader(chain.context,true);
        handlers.put(tagName,new ChildLoader(itemsLoader,null));
    }

    private Loader keyLoader;
    private Loader valueLoader;

    /**
     * Handles {@code <items>} and {@code </items>}.
     *
     * The target will be set to a {@link Map}.
     */
    private final Loader itemsLoader = new Loader(false) {

        private ThreadLocal<Stack<BeanT>> target = new ThreadLocal<Stack<BeanT>>();
        private ThreadLocal<Stack<ValueT>> map = new ThreadLocal<Stack<ValueT>>();

        @Override
        public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
            // create or obtain the Map object
            try {
                BeanT target = (BeanT) state.getPrev().getTarget();
                ValueT mapValue = acc.get(target);
                if(mapValue == null)
                    mapValue = ClassFactory.create(mapImplClass);
                else
                    mapValue.clear();

                Stack.push(this.target, target);
                Stack.push(map, mapValue);
                state.setTarget(mapValue);
            } catch (AccessorException e) {
                // recover from error by setting a dummy Map that receives and discards the values
                handleGenericException(e,true);
                state.setTarget(new HashMap());
            }
        }

        @Override
        public void leaveElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
            super.leaveElement(state, ea);
            try {
                acc.set(Stack.pop(target), Stack.pop(map));
            } catch (AccessorException ex) {
                handleGenericException(ex,true);
            }
        }

        @Override
        public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
            if(ea.matches(entryTag)) {
                state.setLoader(entryLoader);
            } else {
                super.childElement(state,ea);
            }
        }

        @Override
        public Collection<QName> getExpectedChildElements() {
            return Collections.singleton(entryTag.toQName());
        }
    };

    /**
     * Handles {@code <entry>} and {@code </entry>}.
     *
     * The target will be set to a {@link Map}.
     */
    private final Loader entryLoader = new Loader(false) {
        @Override
        public void startElement(UnmarshallingContext.State state, TagName ea) {
            state.setTarget(new Object[2]);  // this is inefficient
        }

        @Override
        public void leaveElement(UnmarshallingContext.State state, TagName ea) {
            Object[] keyValue = (Object[])state.getTarget();
            Map map = (Map) state.getPrev().getTarget();
            map.put(keyValue[0],keyValue[1]);
        }

        @Override
        public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
            if(ea.matches(keyTag)) {
                state.setLoader(keyLoader);
                state.setReceiver(keyReceiver);
                return;
            }
            if(ea.matches(valueTag)) {
                state.setLoader(valueLoader);
                state.setReceiver(valueReceiver);
                return;
            }
            super.childElement(state,ea);
        }

        @Override
        public Collection<QName> getExpectedChildElements() {
            return Arrays.asList(keyTag.toQName(),valueTag.toQName());
        }
    };

    private static final class ReceiverImpl implements Receiver {
        private final int index;
        public ReceiverImpl(int index) {
            this.index = index;
        }
        public void receive(UnmarshallingContext.State state, Object o) {
            ((Object[])state.getTarget())[index] = o;
        }
    }

    private static final Receiver keyReceiver = new ReceiverImpl(0);
    private static final Receiver valueReceiver = new ReceiverImpl(1);

    @Override
    public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        ValueT v = acc.get(o);
        if(v!=null) {
            bareStartTag(w,tagName,v);
            for( Map.Entry e : (Set<Map.Entry>)v.entrySet() ) {
                bareStartTag(w,entryTag,null);

                Object key = e.getKey();
                if(key!=null) {
                    w.startElement(keyTag,key);
                    w.childAsXsiType(key,fieldName,keyBeanInfo, false);
                    w.endElement();
                }

                Object value = e.getValue();
                if(value!=null) {
                    w.startElement(valueTag,value);
                    w.childAsXsiType(value,fieldName,valueBeanInfo, false);
                    w.endElement();
                }

                w.endElement();
            }
            w.endElement();
        } else
        if(nillable) {
            w.startElement(tagName,null);
            w.writeXsiNilTrue();
            w.endElement();
        }
    }

    private void bareStartTag(XMLSerializer w, Name tagName, Object peer) throws IOException, XMLStreamException, SAXException {
        w.startElement(tagName,peer);
        w.endNamespaceDecls(peer);
        w.endAttributes();
    }

    @Override
    public Accessor getElementPropertyAccessor(String nsUri, String localName) {
        if(tagName.equals(nsUri,localName))
            return acc;
        return null;
    }

    private static final class Stack<T> {
        private Stack<T> parent;
        private T value;

        private Stack(Stack<T> parent, T value) {
            this.parent = parent;
            this.value = value;
        }

        private Stack(T value) {
            this.value = value;
        }

        private static <T> void push(ThreadLocal<Stack<T>> holder, T value) {
            Stack<T> parent = holder.get();
            if (parent == null)
                holder.set(new Stack<T>(value));
            else
                holder.set(new Stack<T>(parent, value));
        }

        private static <T> T pop(ThreadLocal<Stack<T>> holder) {
            Stack<T> current = holder.get();
            if (current.parent == null)
                holder.remove();
            else
                holder.set(current.parent);
            return current.value;
        }

    }
}
