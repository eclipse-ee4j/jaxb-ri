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
import java.util.Collections;

/**
 * A special {@link JClass} that represents an unknown class (except its name.)
 *
 * @author Kohsuke Kawaguchi
 * @see JCodeModel#directClass(String) 
 */
final class JDirectClass extends JClass {

    private final String fullName;

    public JDirectClass(JCodeModel _owner,String fullName) {
        super(_owner);
        this.fullName = fullName;
    }

    @Override
    public String name() {
        int i = fullName.lastIndexOf('.');
        if(i>=0)    return fullName.substring(i+1);
        return fullName;
    }

    @Override
    public String fullName() {
        return fullName;
    }

    @Override
    public JPackage _package() {
        int i = fullName.lastIndexOf('.');
        if(i>=0)    return owner()._package(fullName.substring(0,i));
        else        return owner().rootPackage();
    }

    @Override
    public JClass _extends() {
        return owner().ref(Object.class);
    }

    @Override
    public Iterator<JClass> _implements() {
        return Collections.<JClass>emptyList().iterator();
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
        return this;
    }
}
