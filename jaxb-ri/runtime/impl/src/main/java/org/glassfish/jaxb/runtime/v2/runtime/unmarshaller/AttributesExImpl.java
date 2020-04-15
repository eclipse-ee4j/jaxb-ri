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

import org.glassfish.jaxb.runtime.util.AttributesImpl;

/**
 * {@link AttributesEx} implementation.
 * 
 * TODO: proper implementation that holds CharSequence
 *
 * @author Kohsuke Kawaguchi
 */
public final class AttributesExImpl extends AttributesImpl implements AttributesEx {
    public CharSequence getData(int idx) {
        return getValue(idx);
    }

    public CharSequence getData(String nsUri, String localName) {
        return getValue(nsUri,localName);
    }
}
