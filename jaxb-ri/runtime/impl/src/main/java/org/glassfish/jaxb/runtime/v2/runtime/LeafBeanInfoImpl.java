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
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeLeafInfo;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Loader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.TextLoader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallingContext;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.XsiTypeLoader;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * {@link JaxBeanInfo} implementation for immutable leaf classes.
 *
 * <p>
 * Leaf classes are always bound to a text and they are often immutable.
 * The JAXB spec allows this binding for a few special Java classes plus
 * type-safe enums.
 *
 * <p>
 * This implementation obtains necessary information from {@link RuntimeLeafInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
final class LeafBeanInfoImpl<BeanT> extends JaxBeanInfo<BeanT> {

    private final Loader loader;
    private final Loader loaderWithSubst;

    private final Transducer<BeanT> xducer;

    /**
     * Non-null only if the leaf is also an element.
     */
    private final Name tagName;

    public LeafBeanInfoImpl(JAXBContextImpl grammar, RuntimeLeafInfo li) {
        super(grammar,li,li.getClazz(),li.getTypeNames(),li.isElement(),true,false);

        xducer = li.getTransducer();
        loader = new TextLoader(xducer);
        loaderWithSubst = new XsiTypeLoader(this);

        if(isElement())
            tagName = grammar.nameBuilder.createElementName(li.getElementName());
        else
            tagName = null;
    }

    @Override
    public QName getTypeName(BeanT instance) {
        QName tn = xducer.getTypeName(instance);
        if(tn!=null)    return tn;
        // rely on default
        return super.getTypeName(instance);
    }

    @Override
    public String getElementNamespaceURI(BeanT t) {
        return tagName.nsUri;
    }

    @Override
    public String getElementLocalName(BeanT t) {
        return tagName.localName;
    }

    @Override
    public BeanT createInstance(UnmarshallingContext context) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean reset(BeanT bean, UnmarshallingContext context) {
        return false;
    }

    @Override
    public String getId(BeanT bean, XMLSerializer target) {
        return null;
    }

    @Override
    public void serializeBody(BeanT bean, XMLSerializer w) throws SAXException, IOException, XMLStreamException {
        // most of the times leaves are printed as leaf element/attribute property,
        // so this code is only used for example when you have multiple XmlElement on a property
        // and some of them are leaves. Hence this doesn't need to be super-fast.
        try {
            xducer.writeText(w,bean,null);
        } catch (AccessorException e) {
            w.reportError(null,e);
        }
    }

    @Override
    public void serializeAttributes(BeanT bean, XMLSerializer target) {
        // noop
    }

    @Override
    public void serializeRoot(BeanT bean, XMLSerializer target) throws SAXException, IOException, XMLStreamException {
        if(tagName==null) {
            target.reportError(
                new ValidationEventImpl(
                    ValidationEvent.ERROR,
                    Messages.UNABLE_TO_MARSHAL_NON_ELEMENT.format(bean.getClass().getName()),
                    null,
                    null));
        }
        else {
            target.startElement(tagName,bean);
            target.childAsSoleContent(bean,null);
            target.endElement();
        }
    }

    @Override
    public void serializeURIs(BeanT bean, XMLSerializer target) throws SAXException {
        // TODO: maybe we should create another LeafBeanInfoImpl class for
        // context-dependent xducers?
        if(xducer.useNamespace()) {
            try {
                xducer.declareNamespace(bean,target);
            } catch (AccessorException e) {
                target.reportError(null,e);
            }
        }
    }

    @Override
    public Loader getLoader(JAXBContextImpl context, boolean typeSubstitutionCapable) {
        if(typeSubstitutionCapable)
            return loaderWithSubst;
        else
            return loader;
    }

    @Override
    public Transducer<BeanT> getTransducer() {
        return xducer;
    }
}
