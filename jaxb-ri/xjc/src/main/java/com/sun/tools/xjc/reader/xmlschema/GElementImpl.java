/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import javax.xml.namespace.QName;

import com.sun.tools.xjc.reader.gbind.Element;
import com.sun.xml.xsom.XSElementDecl;

/**
 * {@link Element} that wraps {@link XSElementDecl}.
 *
 * @author Kohsuke Kawaguchi
 */
final class GElementImpl extends GElement {
    public final QName tagName;

    /**
     * The representative {@link XSElementDecl}.
     *
     * Even though multiple {@link XSElementDecl}s maybe represented by
     * a single {@link GElementImpl} (especially when they are local),
     * the schema spec requires that they share the same type and other
     * characteristic.
     *
     * (To be really precise, you may have different default values,
     * nillability, all that, so if that becomes a real issue we have
     * to reconsider this design.)
     */
    public final XSElementDecl decl;

    public GElementImpl(QName tagName, XSElementDecl decl) {
        this.tagName = tagName;
        this.decl = decl;
    }

    public String toString() {
        return tagName.toString();
    }

    String getPropertyNameSeed() {
        return tagName.getLocalPart();
    }
}
