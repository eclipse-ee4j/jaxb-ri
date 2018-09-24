/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.ct;

/**
 * Three-state flag for a complex type.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public enum ComplexTypeBindingMode {

    /**
     * Neither FALLBACK nor NOMOREEXTENSION.
     */
    NORMAL,

    /**
     * If a complex type has falled back to the general list content and
     * it is not NOMOREEXTENSION.
     */
    FALLBACK_CONTENT,

    /**
     * If a complex type has falled back to the rest content and
     * it is not NOMOREEXTENSION.
     */
    FALLBACK_REST,

    /**
     * If a complex type has fallen to the dummy property in order
     * to override previously inherited content.
     */
    FALLBACK_EXTENSION

//
//    /**
//     * If a complex type is derived by restriction from a complex type
//     * other than the ur-type. Once this flag is turned on, no more
//     * derivation by extension is allowed.
//     */
//    static final ComplexTypeBindingMode NOMOREEXTENSION = new ComplexTypeBindingMode("noMoreExtension");
}
