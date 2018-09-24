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
 * Anonymous class quick hack.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class JAnonymousClass extends JDefinedClass {

    /**
     * Base interface/class from which this anonymous class is built.
     */
    private final JClass base;
    
    JAnonymousClass( JClass _base) {
        super(_base.owner(), 0, null);
        this.base = _base;
    }
    
    @Override
    public String fullName() {
    	return base.fullName();
    }
    
    @Override
    public void generate(JFormatter f) { 
        f.t(base);
    }
    
}
