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

import com.sun.tools.xjc.model.CElement;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;

/**
 * {@link ClassBinder} that marks abstract components as abstract.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
class Abstractifier extends ClassBinderFilter {
    public Abstractifier(ClassBinder core) {
        super(core);
    }

    public CElement complexType(XSComplexType xs) {
        CElement ci = super.complexType(xs);
        if(ci!=null && xs.isAbstract())
            ci.setAbstract();
        return ci;
    }

    public CElement elementDecl(XSElementDecl xs) {
        CElement ci = super.elementDecl(xs);
        if(ci!=null && xs.isAbstract())
            ci.setAbstract();
        return ci;
    }
}
