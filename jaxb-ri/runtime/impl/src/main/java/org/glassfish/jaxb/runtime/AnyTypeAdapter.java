/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * {@link XmlAdapter} useful for mapping interfaces.
 *
 * See <a href="https://javaee.github.io/jaxb-v2/doc/user-guide/ch03.html#annotating-your-classes-mapping-interfaces">The JAXB user's guide</a>
 * for more about this adapter class.
 *
 * @author Kohsuke Kawaguchi
 * @since JAXB 2.1
 */
public final class AnyTypeAdapter extends XmlAdapter<Object,Object> {
    /**
     * Noop. Just returns the object given as the argument.
     */
    public Object unmarshal(Object v) {
        return v;
    }

    /**
     * Noop. Just returns the object given as the argument.
     */
    public Object marshal(Object v) {
        return v;
    }
}
