/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.util;

import com.sun.xml.xsom.XSType;

/**
 * Perform a transitive closure operation on a type to determine if it
 * belongs to this set. 
 * 
 * The contains method returns true if the TypeSet contains an instance
 * of the specified XSType or any of the base types of the XSType.
 * 
 * @author <a href="mailto:Ryan.Shoemaker@Sun.COM">Ryan Shoemaker</a>, Sun Microsystems, Inc.
 */
public class TypeClosure extends TypeSet {

    private final TypeSet typeSet;
    
    public TypeClosure(TypeSet typeSet) {
        this.typeSet = typeSet;
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.xsom.util.TypeSet#contains(com.sun.xml.xsom.XSDeclaration)
     * 
     * transitive closure variation on the contains method.
     */
    public boolean contains(XSType type) {
        if( typeSet.contains(type) ) {
            return true;
        } else {
            XSType baseType = type.getBaseType();
            if( baseType == null ) {
                return false;
            } else {
                // climb the super type hierarchy
                return contains(baseType);
            }
        }
    }

}
