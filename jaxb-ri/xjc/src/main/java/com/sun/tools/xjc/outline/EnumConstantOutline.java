/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.outline;

import com.sun.codemodel.JEnumConstant;
import com.sun.tools.xjc.generator.bean.BeanGenerator;
import com.sun.tools.xjc.model.CEnumConstant;
import com.sun.tools.xjc.model.CEnumLeafInfo;

/**
 * Outline object that provides per-{@link CEnumConstant} information.
 *
 * This object can be obtained from {@link EnumOutline}
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class EnumConstantOutline {
    /**
     * This {@link EnumOutline} holds information about this {@link CEnumLeafInfo}.
     */
    public final CEnumConstant target;

    /**
     * The generated enum constant.
     */
    public final JEnumConstant constRef;

    /**
     * Reserved for {@link BeanGenerator}.
     */
    protected EnumConstantOutline(CEnumConstant target, JEnumConstant constRef) {
        this.target = target;
        this.constRef = constRef;
    }
}
