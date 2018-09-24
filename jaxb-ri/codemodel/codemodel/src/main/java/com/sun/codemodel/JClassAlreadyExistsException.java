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
 * Indicates that the class is already created.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JClassAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	private final JDefinedClass existing;
    
    public JClassAlreadyExistsException( JDefinedClass _existing ) {
        this.existing = _existing;
    }
    
    /**
     * Gets a reference to the existing {@link JDefinedClass}.
     * 
     * @return
     *      This method always return non-null valid object.
     */
    public JDefinedClass getExistingClass() {
        return existing;
    }
}
