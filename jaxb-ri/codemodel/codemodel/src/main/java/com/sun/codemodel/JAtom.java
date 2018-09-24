/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;


/**
 * JAtoms: Simple code components that merely generate themselves.
 */
final class JAtom extends JExpressionImpl {
    
    private final String what;
    
    JAtom(String what) {
        this.what = what;
    }
    
    public void generate(JFormatter f) {
        f.p(what);
    }
}
