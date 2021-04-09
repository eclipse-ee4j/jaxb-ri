/*
<<<<<<< HEAD:jaxb-ri/runtime/impl/src/main/java/org/glassfish/jaxb/runtime/v2/runtime/FilterTransducer.java
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
=======
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
>>>>>>> parent of 263e92e (Revert optimization removal (#1352)):jaxb-ri/runtime/impl/src/main/java/com/sun/xml/bind/v2/runtime/FilterTransducer.java
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

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * {@link Transducer} that delegates to another {@link Transducer}.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class FilterTransducer<T> implements Transducer<T> {
    protected final Transducer<T> core;

    protected FilterTransducer(Transducer<T> core) {
        this.core = core;
    }

    public boolean useNamespace() {
        return core.useNamespace();
    }

    public void declareNamespace(T o, XMLSerializer w) throws AccessorException {
        core.declareNamespace(o, w);
    }

    public @NotNull CharSequence print(@NotNull T o) throws AccessorException {
        return core.print(o);
    }

    public T parse(CharSequence lexical) throws AccessorException, SAXException {
        return core.parse(lexical);
    }

    public void writeText(XMLSerializer w, T o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        core.writeText(w, o, fieldName);
    }

    public void writeLeafElement(XMLSerializer w, Name tagName, T o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        core.writeLeafElement(w,tagName,o,fieldName);
    }

    public QName getTypeName(T instance) {
        return null;
    }
}
