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
 * Declarations that can have type variables.
 * 
 * Something that can be made into a generic.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface JGenerifiable {
    /**
     * Adds a new type variable to this declaration.
     */
    JTypeVar generify( String name );
    
    /**
     * Adds a new type variable to this declaration with a bound.
     */
    JTypeVar generify( String name, Class<?> bound );
    
    /**
     * Adds a new type variable to this declaration with a bound.
     */
    JTypeVar generify( String name, JClass bound );
    
    /**
     * Iterates all the type parameters of this class/interface.
     */
    JTypeVar[] typeParams();
}
