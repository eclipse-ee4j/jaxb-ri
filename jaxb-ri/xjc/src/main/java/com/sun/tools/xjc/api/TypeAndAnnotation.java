/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JType;

/**
 * Java type and associated JAXB annotations.
 *
 * @author Kohsuke Kawaguchi
 */
public interface TypeAndAnnotation {
    /**
     * Returns the Java type.
     *
     * <p>
     * {@link JType} is a representation of a Java type in a codeModel.
     * If you just need the fully-qualified class name, call {@link JType#fullName()}.
     *
     * @return
     *      never be null.
     */
    JType getTypeClass();

    /**
     * Annotates the given program element by additional JAXB annotations that need to be there
     * at the point of reference.
     */
    void annotate( JAnnotatable programElement );

    /**
     * Two {@link TypeAndAnnotation} are equal if they
     * has the same type and annotations.
     */
    boolean equals(Object o);
}
