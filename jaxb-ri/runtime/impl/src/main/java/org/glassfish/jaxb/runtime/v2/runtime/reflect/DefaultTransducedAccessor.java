/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * {@link TransducedAccessor} that prints to {@link String}.
 *
 * <p>
 * The print method that works for {@link String} determines the dispatching
 * of the {@link #writeText(XMLSerializer,Object,String)} and
 * {@link #writeLeafElement(XMLSerializer, Name, Object, String)} methods,
 * so those are implemented here. 
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class DefaultTransducedAccessor<T> extends TransducedAccessor<T> {

    public abstract String print(T o) throws AccessorException, SAXException;

    public void writeLeafElement(XMLSerializer w, Name tagName, T o, String fieldName) throws SAXException, AccessorException, IOException, XMLStreamException {
        w.leafElement(tagName,print(o),fieldName);
    }

    public void writeText(XMLSerializer w, T o, String fieldName) throws AccessorException, SAXException, IOException, XMLStreamException {
        w.text(print(o),fieldName);
    }
}
