/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.output;

import java.io.IOException;

/**
 * Text data in XML.
 *
 * <p>
 * This class is used inside the marshaller/unmarshaller to
 * send/receive text data.
 *
 * <p>
 * On top of {@link CharSequence}, this class has an
 * ability to write itself to the {@link XmlOutput}. This allows
 * the implementation to choose the most efficient way possible
 * when writing to XML (for example, it can skip the escaping
 * of buffer copying.)
 *
 * TODO: visitor pattern support?
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Pcdata implements CharSequence {

    /**
     * Default constructor.
     */
    protected Pcdata() {}

    /**
     * Writes itself to {@link UTF8XmlOutput}.
     *
     * <p>
     * This is the most performance critical path for the marshaller,
     * so it warrants its own method.
     */
    public abstract void writeTo(UTF8XmlOutput output) throws IOException;

    /**
     * Writes itself to the character array.
     *
     * <p>
     * This method is used by most other {@link XmlOutput}.
     * The default implementation involves in one extra char[] copying.
     *
     * <p>
     * The caller must provide a big enough buffer that can hold
     * enough characters returned by the {@link #length()} method.
     */
    public void writeTo(char[] buf, int start) {
        toString().getChars(0,length(),buf,start);
    }

    @Override
    public abstract String toString();
}
