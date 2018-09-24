/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model;

import com.sun.codemodel.JType;

/**
 * Implemented by {@link Leaf}s that map to PCDATA in XML.
 *
 * @author Kohsuke Kawaguchi
 */
public interface Text {
    /**
     * Obtains the Java class of this {@link Text}.
     */
    JType getDatatype(NodeSet nset);
}
