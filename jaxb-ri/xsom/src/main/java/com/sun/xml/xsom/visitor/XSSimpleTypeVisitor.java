/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.visitor;

import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSUnionSimpleType;

/**
 * Visitor that works on {@link com.sun.xml.xsom.XSSimpleType}
 * and its derived interfaces.
 * 
 * @author
 *  Kohsuke Kawaguchi (kohsuke,kawaguchi@sun.com)
 */
public interface XSSimpleTypeVisitor {
    void listSimpleType( XSListSimpleType type );
    void unionSimpleType( XSUnionSimpleType type );
    void restrictionSimpleType( XSRestrictionSimpleType type );
}
