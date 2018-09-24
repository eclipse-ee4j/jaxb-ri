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

import javax.xml.namespace.QName;

import com.sun.codemodel.JType;

/**
 * Represents a property of a wrapper-style element.
 * 
 * <p>
 * Carrys information about one property of a wrapper-style
 * element. This interface is solely intended for the use by
 * the JAX-RPC and otherwise the use is discouraged.
 * 
 * <p>
 * REVISIT: use CodeModel.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * @see Mapping
 */
public interface Property {
    /**
     * The name of the property.
     * 
     * <p>
     * This method returns a valid identifier suitable for
     * the use as a variable name.
     * 
     * @return
     *      always non-null. Camel-style name like "foo" or "barAndZot".
     *      Note that it may contain non-ASCII characters (CJK, etc.)
     *      The caller is responsible for proper escaping if it
     *      wants to print this as a variable name.
     */
    String name();
    
    /**
     * The Java type of the property.
     * 
     * @return
     *      always non-null.
     *      {@link JType} is a representation of a Java type in a codeModel.
     *      If you just need the fully-qualified class name, call {@link JType#fullName()}.
     */
    JType type();
    
    /**
     * Name of the XML element that corresponds to the property.
     * 
     * <p>
     * Each child of a wrapper style element corresponds with an
     * element, and this method returns that name.
     * 
     * @return
     *      always non-null valid {@link QName}.
     */
    QName elementName();

    QName rawName();
    
}
