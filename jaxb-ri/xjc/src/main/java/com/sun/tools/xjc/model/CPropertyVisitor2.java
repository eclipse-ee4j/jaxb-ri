/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

/**
 * Visitor for {@link CPropertyInfo}.
 *
 * The number 2 signals number of arguments.
 *
 * @param <R> the return type of this visitor's methods.  Use {@link
 *            Void} for visitors that do not need to return results.
 * @param <P> the type of the additional parameter to this visitor's
 *            methods.  Use {@code Void} for visitors that do not need an
 *            additional parameter.
 *
 * @see CPropertyInfo#accept(CPropertyVisitor2, Object)
 *
 * @author Marcel Valovy
 */
public interface CPropertyVisitor2<R, P> {

    /**
     * Visits a CElementPropertyInfo type.
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(CElementPropertyInfo t, P p);

    /**
     * Visits a CAttributePropertyInfo type.
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(CAttributePropertyInfo t, P p);

    /**
     * Visits a CValuePropertyInfo type.
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(CValuePropertyInfo t, P p);

    /**
     * Visits a CReferencePropertyInfo type.
     * @param t the type to visit
     * @param p a visitor-specified parameter
     * @return  a visitor-specified result
     */
    R visit(CReferencePropertyInfo t, P p);
}
