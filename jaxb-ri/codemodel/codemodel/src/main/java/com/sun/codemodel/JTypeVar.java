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
 * Type variable used to declare generics.
 * 
 * @see JGenerifiable
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class JTypeVar extends JClass implements JDeclaration {
    
    private final String name;
    
    private JClass bound;

    JTypeVar(JCodeModel owner, String _name) {
        super(owner);
        this.name = _name;
    }
    
    @Override
    public String name() {
        return name;
    }

    @Override
    public String fullName() {
        return name;
    }

    @Override
    public JPackage _package() {
        return null;
    }
    
    /**
     * Adds a bound to this variable.
     * 
     * @return  this
     */
    public JTypeVar bound( JClass c ) {
        if(bound!=null)
            throw new IllegalArgumentException("type variable has an existing class bound "+bound);
        bound = c;
        return this;
    }

    /**
     * @return bound of this variable
     */
    public JClass bound() {
        return bound;
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

    /**
     * Prints out the declaration of the variable.
     */
    @Override
    public void declare(JFormatter f) {
        f.id(name);
        if(bound!=null)
            f.p("extends").g(bound);
    }


    @Override
    protected JClass substituteParams(JTypeVar[] variables, List<JClass> bindings) {
        for(int i=0;i<variables.length;i++)
            if(variables[i]==this)
                return bindings.get(i);
        return this;
    }

    @Override
    public void generate(JFormatter f) {
        f.id(name);
    }
}
