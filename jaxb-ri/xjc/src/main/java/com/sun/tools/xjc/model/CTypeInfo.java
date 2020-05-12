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

import com.sun.codemodel.JClass;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import org.glassfish.jaxb.core.v2.model.core.TypeInfo;

/**
 * {@link TypeInfo} at the compile-time.
 * Either {@link CClassInfo}, {@link CBuiltinLeafInfo}, or {@link CElementInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface CTypeInfo extends TypeInfo<NType,NClass>, CCustomizable {

    /**
     * Returns the {@link JClass} that represents the class being bound,
     * under the given {@link Outline}.
     *
     * @see NType#toType(Outline, com.sun.tools.xjc.outline.Aspect)
     */
    JType toType(Outline o, Aspect aspect);
}
