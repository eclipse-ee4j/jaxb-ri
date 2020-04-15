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

import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeElementPropertyInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.JaxBeanInfo;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * {@link ArrayProperty} that contains node values.
 *
 * @author Kohsuke Kawaguchi
 */
final class ArrayElementNodeProperty<BeanT,ListT,ItemT> extends ArrayElementProperty<BeanT,ListT,ItemT> {

    public ArrayElementNodeProperty(JAXBContextImpl p, RuntimeElementPropertyInfo prop) {
        super(p, prop);
    }

    public void serializeItem(JaxBeanInfo expected, ItemT item, XMLSerializer w) throws SAXException, IOException, XMLStreamException {
        if(item==null) {
            w.writeXsiNilTrue();
        } else {
            w.childAsXsiType(item,fieldName,expected, false);
        }
    }
}
