/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.output;

import org.glassfish.jaxb.core.marshaller.NoEscapeHandler;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Base64Data;
import org.jvnet.staxex.XMLStreamWriterEx;

import javax.xml.stream.XMLStreamException;

/**
 * {@link XmlOutput} for {@link XMLStreamWriterEx}.
 *
 * @author Paul Sandoz.
 */
public final class StAXExStreamWriterOutput extends XMLStreamWriterOutput {
    private final XMLStreamWriterEx out;

    public StAXExStreamWriterOutput(XMLStreamWriterEx out) {
        super(out, NoEscapeHandler.theInstance);
        this.out = out;
    }

    public void text(Pcdata value, boolean needsSeparatingWhitespace) throws XMLStreamException {
        if(needsSeparatingWhitespace) {
            out.writeCharacters(" ");
        }

        if (!(value instanceof Base64Data)) {
            out.writeCharacters(value.toString());
        } else {
            Base64Data v = (Base64Data)value;
            out.writeBinary(v.getDataHandler());
        }
    }
}
