/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.outline;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * Representation of a field of {@link ClassOutline}.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface FieldOutline {

    /**
     * Gets the enclosing {@link ClassOutline}.
     */
    ClassOutline parent();

    /** Gets the corresponding model object. */
    CPropertyInfo getPropertyInfo();
    
    /**
     * Gets the type of the "raw value".
     * 
     * <p>
     * This type can represent the entire value of this field.
     * For fields that can carry multiple values, this is an array.
     *
     * <p>
     * This type allows the client of the outline to generate code
     * to set/get values from a property.
     */
    JType getRawType();
    
    /**
     * Creates a new {@link FieldAccessor} of this field
     * for the specified object.
     * 
     * @param targetObject
     *      Evaluates to an object, and the field on this object
     *      will be accessed.
     */
    FieldAccessor create( JExpression targetObject );
}
