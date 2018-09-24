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

import java.util.ArrayList;
import java.util.List;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.model.CCustomizable;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.istack.NotNull;

/**
 * Outline object that provides per-{@link CEnumLeafInfo} information
 * for filling in methods/fields for a bean.
 *
 * This object can be obtained from {@link Outline}
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class EnumOutline implements CustomizableOutline {

    /**
     * This {@link EnumOutline} holds information about this {@link CEnumLeafInfo}.
     */
    public final CEnumLeafInfo target;

    /**
     * The generated enum class.
     */
    public final JDefinedClass clazz;

    /**
     * Constants.
     */
    public final List<EnumConstantOutline> constants = new ArrayList<EnumConstantOutline>();

    /**
     * {@link PackageOutline} that contains this class.
     */
    public @NotNull
    PackageOutline _package() {
        return parent().getPackageContext(clazz._package());
    }

    /**
     * A {@link Outline} that encloses all the class outlines.
     */
    public abstract @NotNull Outline parent();

    protected EnumOutline(CEnumLeafInfo target, JDefinedClass clazz) {
        this.target = target;
        this.clazz = clazz;
    }

    @Override
    public JDefinedClass getImplClass() {
        return clazz;
    }

    @Override
    public CCustomizable getTarget() {
        return target;
    }
}
