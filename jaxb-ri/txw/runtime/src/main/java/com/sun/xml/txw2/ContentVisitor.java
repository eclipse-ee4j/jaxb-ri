/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2;

/**
 * Visits {@link Content}.
 *
 * @author Kohsuke Kawaguchi
 */
interface ContentVisitor {
    void onStartDocument();

    void onEndDocument();

    void onEndTag();

    void onPcdata(StringBuilder buffer);

    void onCdata(StringBuilder buffer);

    void onStartTag(String nsUri, String localName, Attribute attributes, NamespaceDecl namespaces);

    void onComment(StringBuilder buffer);
}
