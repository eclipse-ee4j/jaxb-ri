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

import java.util.ArrayList;
import java.util.List;


/**
 * For statement
 */

public class JForLoop implements JStatement {
    
    private List<Object> inits = new ArrayList<Object>();
    private JExpression test = null;
    private List<JExpression> updates = new ArrayList<JExpression>();
    private JBlock body = null;
    
    public JVar init(int mods, JType type, String var, JExpression e) {
        JVar v = new JVar(JMods.forVar(mods), type, var, e);
        inits.add(v);
        return v;
    }
    
    public JVar init(JType type, String var, JExpression e) {
        return init(JMod.NONE, type, var, e);
    }
    
    public void init(JVar v, JExpression e) {
        inits.add(JExpr.assign(v, e));
    }
    
    public void test(JExpression e) {
        this.test = e;
    }
    
    public void update(JExpression e) {
        updates.add(e);
    }
    
    public JBlock body() {
        if (body == null) body = new JBlock();
        return body;
    }
    
    public void state(JFormatter f) {
        f.p("for (");
        boolean first = true;
        for (Object o : inits) {
            if (!first) f.p(',');
            if (o instanceof JVar)
                f.b((JVar) o);
            else
                f.g((JExpression) o);
            first = false;
        }
        f.p(';').g(test).p(';').g(updates).p(')');
        if (body != null)
            f.g(body).nl();
        else
            f.p(';').nl();
    }
    
}
