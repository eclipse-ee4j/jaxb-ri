/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import java.util.HashSet;
import java.util.Set;

import com.sun.tools.xjc.reader.gbind.Element;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;

/**
 * @author Kohsuke Kawaguchi
 */
abstract class GElement extends Element {
    /**
     * All the {@link XSParticle}s (whose term is {@link XSElementDecl})
     * that are coereced into a single {@link Element}.
     */
    final Set<XSParticle> particles = new HashSet<XSParticle>();

    /**
     * Gets the seed (raw XML name) to be used to generate a property name.
     */
    abstract String getPropertyNameSeed();
}
