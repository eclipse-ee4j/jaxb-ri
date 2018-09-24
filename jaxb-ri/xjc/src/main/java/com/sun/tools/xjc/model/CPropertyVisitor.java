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
 * Ideally it should be defined on the runtime core model, but the runtime is on diet.
 * Hence it's defined here.
 *
 * @see CPropertyInfo#accept(CPropertyVisitor)
 *
 * @author Kohsuke Kawaguchi
 */
public interface CPropertyVisitor<V> {
    V onElement( CElementPropertyInfo p );
    V onAttribute( CAttributePropertyInfo p );
    V onValue( CValuePropertyInfo p );
    V onReference( CReferencePropertyInfo p );
}
