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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Special class object that represents the type of "null".
 * 
 * <p>
 * Use this class with care.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class JNullType extends JClass {

    JNullType(JCodeModel _owner) {
        super(_owner);
    }

    public String name() { return "null"; }
    public String fullName() { return "null"; }

    public JPackage _package() { return owner()._package(""); }

    public JClass _extends() { return null; }

    public Iterator<JClass> _implements() {
        return Collections.<JClass>emptyList().iterator();
    }

    public boolean isInterface() { return false; }
    public boolean isAbstract() { return false; }

    protected JClass substituteParams(JTypeVar[] variables, List<JClass> bindings) {
        return this;
    }
}
