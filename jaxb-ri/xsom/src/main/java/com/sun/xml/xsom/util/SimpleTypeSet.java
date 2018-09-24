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

import java.util.Set;

import com.sun.xml.xsom.XSType;

/**
 * A very simple TypeSet.
 * 
 * The contains method returns true if the set explicitly contains an
 * instance of the specified XSType.
 * 
 * @author <a href="mailto:Ryan.Shoemaker@Sun.COM">Ryan Shoemaker</a>, Sun Microsystems, Inc.
 */
public class SimpleTypeSet extends TypeSet {

    private final Set typeSet;
    
    public SimpleTypeSet(Set s) {
        typeSet = s;
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.xsom.util.TypeSet#contains(com.sun.xml.xsom.XSDeclaration)
     */
    public boolean contains(XSType type) {
        return typeSet.contains(type);
    }

}
