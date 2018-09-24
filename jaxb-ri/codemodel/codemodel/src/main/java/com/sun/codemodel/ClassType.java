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
 * This helps enable whether the JDefinedClass is a Class or Interface or
 * AnnotationTypeDeclaration or Enum
 *
 * @author
 *     Bhakti Mehta (bhakti.mehta@sun.com)
 */
public final class ClassType {

    /**
     * The keyword used to declare this type.
     */
    final String declarationToken;

    private ClassType(String token) {
        this.declarationToken = token;
    }

    public static final ClassType CLASS = new ClassType("class");
    public static final ClassType INTERFACE = new ClassType("interface");
    public static final ClassType ANNOTATION_TYPE_DECL = new ClassType("@interface");
    public static final ClassType ENUM = new ClassType("enum");
}
