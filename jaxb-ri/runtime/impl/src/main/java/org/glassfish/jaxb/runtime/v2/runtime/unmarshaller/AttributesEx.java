/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.xml.sax.Attributes;

/**
 * {@link Attributes} extension that allows attribute values
 * to be exposed as {@link CharSequence}.
 *
 * <p>
 * All namespace URIs and local names are assumed to be interned.
 *
 * @author Kohsuke Kawaguchi
 */
public interface AttributesEx extends Attributes {
    /**
     * The same as {@link #getValue(int)}
     */
    CharSequence getData(int idx);

    /**
     * The same as {@link #getValue(String,String)}
     */
    CharSequence getData(String nsUri,String localName);
}
