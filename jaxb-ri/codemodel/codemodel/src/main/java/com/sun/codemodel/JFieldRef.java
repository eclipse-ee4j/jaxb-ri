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
 * Field Reference
 */

public class JFieldRef extends JExpressionImpl implements JAssignmentTarget {
    /**
     * Object expression upon which this field will be accessed, or
     * null for the implicit 'this'.
     */
    private JGenerable object;

    /**
     * Name of the field to be accessed. Either this or {@link #var} is set.
     */
    private String name;

    /**
     * Variable to be accessed.
     */
    private JVar var;

    /**
     * Indicates if an explicit this should be generated
     */
    private boolean explicitThis;

    /**
     * Field reference constructor given an object expression and field name
     *
     * @param object
     *        JExpression for the object upon which
     *        the named field will be accessed,
     *
     * @param name
     *        Name of field to access
     */
    JFieldRef(JExpression object, String name) {
        this(object, name, false);
    }

    JFieldRef(JExpression object, JVar v) {
        this(object, v, false);
    }

    /**
     * Static field reference.
     */
    JFieldRef(JType type, String name) {
        this(type, name, false);
    }

    JFieldRef(JType type, JVar v) {
        this(type, v, false);
    }

    JFieldRef(JGenerable object, String name, boolean explicitThis) {
        this.explicitThis = explicitThis;
        this.object = object;
        if (name.indexOf('.') >= 0)
            throw new IllegalArgumentException("Field name contains '.': " + name);
        this.name = name;
    }

    JFieldRef(JGenerable object, JVar var, boolean explicitThis) {
        this.explicitThis = explicitThis;
        this.object = object;
        this.var = var;
    }

    @Override
    public void generate(JFormatter f) {
        String name = this.name;
        if(name==null)  name=var.name();

        if (object != null) {
            f.g(object).p('.').p(name);
        } else {
            if (explicitThis) {
                f.p("this.").p(name);
            } else {
                f.id(name);
            }
        }
    }

    @Override
    public JExpression assign(JExpression rhs) {
        return JExpr.assign(this, rhs);
    }
    @Override
    public JExpression assignPlus(JExpression rhs) {
        return JExpr.assignPlus(this, rhs);
    }
}
