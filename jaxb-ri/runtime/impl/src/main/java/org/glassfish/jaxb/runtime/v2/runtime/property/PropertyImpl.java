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
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimePropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
abstract class PropertyImpl<BeanT> implements Property<BeanT> {
    /**
     * Name of this field.
     */
    protected final String fieldName;
    private RuntimePropertyInfo propertyInfo = null;
    private boolean hiddenByOverride = false;

    public PropertyImpl(JAXBContextImpl context, RuntimePropertyInfo prop) {
        fieldName = prop.getName();
        if (context.retainPropertyInfo) {
            propertyInfo = prop;
        }
    }

    public RuntimePropertyInfo getInfo() {
        return propertyInfo;
    }

    public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
    }

    public void serializeURIs(BeanT o, XMLSerializer w) throws SAXException, AccessorException {
    }

    public boolean hasSerializeURIAction() {
        return false;
    }

    public Accessor getElementPropertyAccessor(String nsUri, String localName) {
        // default implementation. should be overrided
        return null;
    }

    public void wrapUp() {/*noop*/}
    
    public boolean isHiddenByOverride() {
        return hiddenByOverride;
    }

    public void setHiddenByOverride(boolean hidden) {
        this.hiddenByOverride = hidden;
    }
    
    public String getFieldName() {
        return fieldName;
    }
}
