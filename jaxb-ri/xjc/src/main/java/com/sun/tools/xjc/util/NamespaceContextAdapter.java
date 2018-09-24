/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.util;

import java.util.Collections;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import com.sun.xml.xsom.XmlString;

import com.sun.tools.rngdatatype.ValidationContext;

/**
 * Take a {@link ValidationContext} and make it look like a {@link NamespaceContext}.
 *
 * @author Kohsuke Kawaguchi
 */
public final class NamespaceContextAdapter implements NamespaceContext {
    private XmlString xstr;

    public NamespaceContextAdapter(XmlString xstr) {
        this.xstr = xstr;
    }

    public String getNamespaceURI(String prefix) {
        return xstr.resolvePrefix(prefix);
    }

    public String getPrefix(String namespaceURI) {
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return Collections.EMPTY_LIST.iterator();
    }
}
