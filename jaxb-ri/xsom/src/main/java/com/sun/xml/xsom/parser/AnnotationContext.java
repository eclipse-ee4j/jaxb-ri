/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.parser;

/**
 * Enumeration used to represent the type of the schema component
 * that is being parsed when the AnnotationParser is called.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final public class AnnotationContext {
    
    /** Display name of the context. */
    private final String name;
    
    private AnnotationContext( String _name ) {
        this.name = _name;
    }
    
    public String toString() { return name; }
    
    
    
    public static final AnnotationContext SCHEMA
        = new AnnotationContext("schema");
    public static final AnnotationContext NOTATION
        = new AnnotationContext("notation");
    public static final AnnotationContext ELEMENT_DECL
        = new AnnotationContext("element");
    public static final AnnotationContext IDENTITY_CONSTRAINT
        = new AnnotationContext("identityConstraint");
    public static final AnnotationContext XPATH
        = new AnnotationContext("xpath");
    public static final AnnotationContext MODELGROUP_DECL
        = new AnnotationContext("modelGroupDecl");
    public static final AnnotationContext SIMPLETYPE_DECL
        = new AnnotationContext("simpleTypeDecl");
    public static final AnnotationContext COMPLEXTYPE_DECL
        = new AnnotationContext("complexTypeDecl");
    public static final AnnotationContext PARTICLE
        = new AnnotationContext("particle");
    public static final AnnotationContext MODELGROUP
        = new AnnotationContext("modelGroup");
    public static final AnnotationContext ATTRIBUTE_USE
        = new AnnotationContext("attributeUse");
    public static final AnnotationContext WILDCARD
        = new AnnotationContext("wildcard");
    public static final AnnotationContext ATTRIBUTE_GROUP
        = new AnnotationContext("attributeGroup");
    public static final AnnotationContext ATTRIBUTE_DECL
        = new AnnotationContext("attributeDecl");
}
