/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

/**
 * String literal.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JStringLiteral extends JExpressionImpl {

    public final String str;

    JStringLiteral(String what) {
        this.str = what;
    
    }
   
    
    @Override
    public void generate(JFormatter f) {
        f.p(JExpr.quotify('"', str));
    }

    @Override
    public String toString() {
        return str;
    }

}
