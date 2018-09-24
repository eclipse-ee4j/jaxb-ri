/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.impl.parser.SchemaDocumentImpl;
import com.sun.xml.xsom.util.NameGetter;
import org.xml.sax.Locator;

abstract class DeclarationImpl extends ComponentImpl implements XSDeclaration
{
    DeclarationImpl( SchemaDocumentImpl owner,
        AnnotationImpl _annon, Locator loc, ForeignAttributesImpl fa,
        String _targetNamespace, String _name,    boolean _anonymous ) {
        
        super(owner,_annon,loc,fa);
        this.targetNamespace = _targetNamespace;
        this.name = _name;
        this.anonymous = _anonymous;
    }
    
    private final String name;
    public String getName() { return name; }
    
    private final String targetNamespace;
    public String getTargetNamespace() { return targetNamespace; }
    
    private final boolean anonymous;
    /** @deprecated */
    public boolean isAnonymous() { return anonymous; }
    
    public final boolean isGlobal() { return !isAnonymous(); }
    public final boolean isLocal() { return isAnonymous(); }
}
