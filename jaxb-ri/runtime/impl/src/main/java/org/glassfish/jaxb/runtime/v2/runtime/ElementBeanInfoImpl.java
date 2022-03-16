/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeElementInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimePropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.property.Property;
import org.glassfish.jaxb.runtime.v2.runtime.property.PropertyFactory;
import org.glassfish.jaxb.runtime.v2.runtime.property.UnmarshallerChain;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.*;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * {@link JaxBeanInfo} implementation for {@link RuntimeElementInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ElementBeanInfoImpl extends JaxBeanInfo<JAXBElement> {

    private Loader loader;

    private final Property property;

    // used to create new instances of JAXBElement.
    private final QName tagName;
    public final Class expectedType;
    private final Class scope;

    /**
     * If non-null, use this to create an instance.
     * It takes one value.
     */
    private final Constructor<? extends JAXBElement> constructor;

    ElementBeanInfoImpl(JAXBContextImpl grammar, RuntimeElementInfo rei) {
        super(grammar,rei,(Class<JAXBElement>)rei.getType(),true,false,true);

        this.property = PropertyFactory.create(grammar,rei.getProperty());

        tagName = rei.getElementName();
        expectedType = (Class) Utils.REFLECTION_NAVIGATOR.erasure(rei.getContentInMemoryType());
        scope = rei.getScope()==null ? JAXBElement.GlobalScope.class : rei.getScope().getClazz();

        Class type = (Class) Utils.REFLECTION_NAVIGATOR.erasure(rei.getType());
        if(type==JAXBElement.class)
            constructor = null;
        else {
            try {
                constructor = type.getConstructor(expectedType);
            } catch (NoSuchMethodException e) {
                NoSuchMethodError x = new NoSuchMethodError("Failed to find the constructor for " + type + " with " + expectedType);
                x.initCause(e);
                throw x;
            }
        }
    }

    /**
     * The constructor for the sole instanceof {@link JaxBeanInfo} for
     * handling user-created {@link JAXBElement}.
     *
     * Such {@link JaxBeanInfo} is used only for marshalling.
     *
     * This is a hack.
     */
    protected ElementBeanInfoImpl(final JAXBContextImpl grammar) {
        super(grammar,null,JAXBElement.class,true,false,true);
        tagName = null;
        expectedType = null;
        scope = null;
        constructor = null;

        this.property = new Property<JAXBElement>() {
            @Override
            public void reset(JAXBElement o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void serializeBody(JAXBElement e, XMLSerializer target, Object outerPeer) throws SAXException, IOException, XMLStreamException {
                Class scope = e.getScope();
                if(e.isGlobalScope())   scope = null;
                QName n = e.getName();
                ElementBeanInfoImpl bi = grammar.getElement(scope,n);
                if(bi==null) {
                    // infer what to do from the type
                    JaxBeanInfo tbi;
                    try {
                        tbi = grammar.getBeanInfo(e.getDeclaredType(),true);
                    } catch (JAXBException x) {
                        // if e.getDeclaredType() isn't known to this JAXBContext
                        target.reportError(null,x);
                        return;
                    }
                    Object value = e.getValue();
                    target.startElement(n.getNamespaceURI(),n.getLocalPart(),n.getPrefix(),null);
                    if(value==null) {
                        target.writeXsiNilTrue();
                    } else {
                        target.childAsXsiType(value,"value",tbi, false);
                    }
                    target.endElement();
                } else {
                    try {
                        bi.property.serializeBody(e,target,e);
                    } catch (AccessorException x) {
                        target.reportError(null,x);
                    }
                }
            }

            @Override
            public void serializeURIs(JAXBElement o, XMLSerializer target) {
            }

            @Override
            public boolean hasSerializeURIAction() {
                return false;
            }

            @Override
            public String getIdValue(JAXBElement o) {
                return null;
            }

            @Override
            public PropertyKind getKind() {
                return PropertyKind.ELEMENT;
            }

            @Override
            public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
            }

            @Override
            public Accessor getElementPropertyAccessor(String nsUri, String localName) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void wrapUp() {
            }

            @Override
            public RuntimePropertyInfo getInfo() {
                return property.getInfo();
            }

            @Override
            public boolean isHiddenByOverride() {
                return false;
            }
            
            @Override
            public void setHiddenByOverride(boolean hidden) {
                throw new UnsupportedOperationException("Not supported on jaxbelements.");
            }

            @Override
            public String getFieldName() {
                return null;
            }

        };
    }

    /**
     * Use the previous {@link UnmarshallingContext.State}'s target to store
     * {@link JAXBElement} object to be unmarshalled. This allows the property {@link Loader}
     * to correctly find the parent object.
     * This is a hack.
     */
    private final class IntercepterLoader extends Loader implements Intercepter {
        private final Loader core;

        public IntercepterLoader(Loader core) {
            this.core = core;
        }

        @Override
        public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
            state.setLoader(core);
            state.setIntercepter(this);

            // TODO: make sure there aren't too many duplicate of this code
            // create the object to unmarshal
            Object child;
            UnmarshallingContext context = state.getContext();

            // let's see if we can reuse the existing peer object
            child = context.getOuterPeer();

            if(child!=null && jaxbType!=child.getClass())
                child = null;   // unexpected type.

            if(child!=null)
                reset((JAXBElement)child,context);

            if(child==null)
                child = context.createInstance(ElementBeanInfoImpl.this);

            fireBeforeUnmarshal(ElementBeanInfoImpl.this, child, state);

            context.recordOuterPeer(child);
            UnmarshallingContext.State p = state.getPrev();
            p.setBackup(p.getTarget());
            p.setTarget(child);

            core.startElement(state,ea);
        }

        @Override
        public Object intercept(UnmarshallingContext.State state, Object o) throws SAXException {
            JAXBElement e = (JAXBElement)state.getTarget();
            state.setTarget(state.getBackup());
            state.setBackup(null);

            if (state.isNil()) {
                e.setNil(true);
                state.setNil(false);
            }

            if(o!=null)
                // if the value is a leaf type, it's often already set to the element
                // through Accessor.
                e.setValue(o);

            fireAfterUnmarshal(ElementBeanInfoImpl.this, e, state);

            return e;
        }
    }

    @Override
    public String getElementNamespaceURI(JAXBElement e) {
        return e.getName().getNamespaceURI();
    }

    @Override
    public String getElementLocalName(JAXBElement e) {
        return e.getName().getLocalPart();
    }

    @Override
    public Loader getLoader(JAXBContextImpl context, boolean typeSubstitutionCapable) {
        if(loader==null) {
            // this has to be done lazily to avoid cyclic reference issue
            UnmarshallerChain c = new UnmarshallerChain(context);
            QNameMap<ChildLoader> result = new QNameMap<>();
            property.buildChildElementUnmarshallers(c,result);
            if(result.size()==1)
                // for ElementBeanInfoImpl created from RuntimeElementInfo
                this.loader = new IntercepterLoader(result.getOne().getValue().loader);
            else
                // for special ElementBeanInfoImpl only used for marshalling
                this.loader = Discarder.INSTANCE;
        }
        return loader;
    }

    @Override
    public JAXBElement createInstance(UnmarshallingContext context) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return createInstanceFromValue(null);
    }

    public JAXBElement createInstanceFromValue(Object o) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        if(constructor==null)
            return new JAXBElement(tagName,expectedType,scope,o);
        else
            return constructor.newInstance(o);
    }

    @Override
    public boolean reset(JAXBElement e, UnmarshallingContext context) {
        e.setValue(null);
        return true;
    }

    @Override
    public String getId(JAXBElement e, XMLSerializer target) {
        // TODO: is this OK? Should we be returning the ID value of the type property?
        /*
            There's one case where we JAXBElement needs to be designated as ID,
            and that is when there's a global element whose type is ID.
        */
        Object o = e.getValue();
        if(o instanceof String)
            return (String)o;
        else
            return null;
    }

    @Override
    public void serializeBody(JAXBElement element, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        try {
            property.serializeBody(element,target,null);
        } catch (AccessorException x) {
            target.reportError(null,x);
        }
    }

    @Override
    public void serializeRoot(JAXBElement e, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        serializeBody(e,target);
    }

    @Override
    public void serializeAttributes(JAXBElement e, XMLSerializer target) {
        // noop
    }

    @Override
    public void serializeURIs(JAXBElement e, XMLSerializer target) {
        // noop
    }

    @Override
    public Transducer<JAXBElement> getTransducer() {
        return null;
    }

    @Override
    public void wrapUp() {
        super.wrapUp();
        property.wrapUp();
    }

    @Override
    public void link(JAXBContextImpl grammar) {
        super.link(grammar);
        getLoader(grammar,true);    // make sure to build them, if we hadn't done so
    }
}
