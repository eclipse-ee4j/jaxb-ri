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

import com.sun.istack.NotNull;
import org.glassfish.jaxb.runtime.api.AccessorException;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * Transducer that signals the runtime that this binary data shall be always inlined.
 *
 * @author Kohsuke Kawaguchi
 */
public class InlineBinaryTransducer<V> extends FilterTransducer<V> {
    public InlineBinaryTransducer(Transducer<V> core) {
        super(core);
    }

    @Override
    public @NotNull CharSequence print(@NotNull V o) throws AccessorException {
        XMLSerializer w = XMLSerializer.getInstance();
        boolean old = w.setInlineBinaryFlag(true);
        try {
            return core.print(o);
        } finally {
            w.setInlineBinaryFlag(old);
        }
    }

    @Override
    public void writeText(XMLSerializer w, V o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        boolean old = w.setInlineBinaryFlag(true);
        try {
            core.writeText(w,o,fieldName);
        } finally {
            w.setInlineBinaryFlag(old);
        }
    }

    @Override
    public void writeLeafElement(XMLSerializer w, Name tagName, V o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        boolean old = w.setInlineBinaryFlag(true);
        try {
            core.writeLeafElement(w, tagName, o, fieldName);
        } finally {
            w.setInlineBinaryFlag(old);
        }
    }
}
