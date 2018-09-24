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
 * Catch block for a try/catch/finally statement
 */

public class JCatchBlock implements JGenerable {

    JClass exception;
    private JVar var = null;
    private JBlock body = new JBlock();

    JCatchBlock(JClass exception) {
        this.exception = exception;
    }

    public JVar param(String name) {
        if (var != null) throw new IllegalStateException();
        var = new JVar(JMods.forVar(JMod.NONE), exception, name, null);
        return var;
    }

    public JBlock body() {
        return body;
    }

    public void generate(JFormatter f) {
        if (var == null)
            var = new JVar(JMods.forVar(JMod.NONE),
                    exception, "_x", null);
        f.p("catch (").b(var).p(')').g(body);
    }

}
