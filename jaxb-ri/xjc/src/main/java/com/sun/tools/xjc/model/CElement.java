/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import org.glassfish.jaxb.core.v2.model.core.Element;

/**
 * Either {@link CElementInfo}, {@link CClassInfo}, or {@link CClassRef}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface CElement extends CTypeInfo, Element<NType,NClass> {
    /**
     * Marks this element as an abstract element.
     */
    void setAbstract();

    /**
     * Returns true iff this element is an abstract element.
     */
    boolean isAbstract();
}
