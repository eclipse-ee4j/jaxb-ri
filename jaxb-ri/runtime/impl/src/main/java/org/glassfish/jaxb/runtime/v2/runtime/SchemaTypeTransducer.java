/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import org.glassfish.jaxb.runtime.api.AccessorException;
import jakarta.xml.bind.annotation.XmlSchemaType;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * {@link Transducer} that signals the runtime that this datatype
 * is marshalled to a different XML Schema type.
 *
 * <p>
 * This transducer is used to implement the semantics of {@link XmlSchemaType} annotation.
 *
 *
 * @see XMLSerializer#schemaType
 * @author Kohsuke Kawaguchi
 */
public class SchemaTypeTransducer<V> extends FilterTransducer<V> {
    private final QName schemaType;

    public SchemaTypeTransducer(Transducer<V> core, QName schemaType) {
        super(core);
        this.schemaType = schemaType;
    }

    @Override
    public CharSequence print(V o) throws AccessorException {
        XMLSerializer w = XMLSerializer.getInstance();
        QName old = w.setSchemaType(schemaType);
        try {
            return core.print(o);
        } finally {
            w.setSchemaType(old);
        }
    }

    @Override
    public void writeText(XMLSerializer w, V o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        QName old = w.setSchemaType(schemaType);
        try {
            core.writeText(w,o,fieldName);
        } finally {
            w.setSchemaType(old);
        }
    }

    @Override
    public void writeLeafElement(XMLSerializer w, Name tagName, V o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        QName old = w.setSchemaType(schemaType);
        try {
            core.writeLeafElement(w, tagName, o, fieldName);
        } finally {
            w.setSchemaType(old);
        }
    }
}
