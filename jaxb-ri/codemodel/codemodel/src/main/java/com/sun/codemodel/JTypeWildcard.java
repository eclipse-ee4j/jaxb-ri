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

import java.util.Iterator;
import java.util.List;

/**
 * Represents a wildcard type like "? extends Foo".
 *
 * <p>
 * Instances of this class can be obtained from {@link JClass#wildcard()}
 *
 * TODO: extend this to cover "? super Integer".
 *
 * <p>
 * Our modeling of types are starting to look really ugly.
 * ideally it should have been done somewhat like APT,
 * but it's too late now.
 *
 * @author Kohsuke Kawaguchi
 */
final class JTypeWildcard extends JClass {

    private final JClass bound;

    JTypeWildcard(JClass bound) {
        super(bound.owner());
        this.bound = bound;
    }

    @Override
    public String name() {
        return "? extends "+bound.name();
    }

    @Override
    public String fullName() {
        return "? extends "+bound.fullName();
    }

    @Override
    public JPackage _package() {
        return null;
    }

    /**
     * Returns the class bound of this variable.
     *
     * <p>
     * If no bound is given, this method returns {@link Object}.
     */
    @Override
    public JClass _extends() {
        if(bound!=null)
            return bound;
        else
            return owner().ref(Object.class);
    }

    /**
     * Returns the interface bounds of this variable.
     */
    @Override
    public Iterator<JClass> _implements() {
        return bound._implements();
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    protected JClass substituteParams(JTypeVar[] variables, List<JClass> bindings) {
        JClass nb = bound.substituteParams(variables,bindings);
        if(nb==bound)
            return this;
        else
            return new JTypeWildcard(nb);
    }

    @Override
    public void generate(JFormatter f) {
        if(bound._extends()==null)
            f.p("?");   // instead of "? extends Object"
        else
            f.p("? extends").g(bound);
    }
}
