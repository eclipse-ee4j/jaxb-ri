/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.outline.ClassOutline;

/**
 * {@link ClassOutline} enhanced with schema2java specific
 * information.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class ClassOutlineImpl extends ClassOutline {
    private final BeanGenerator _parent;


    public MethodWriter createMethodWriter() {
        return _parent.getModel().strategy.createMethodWriter(this);
    }
    
    /**
     * Gets {@link #_package} as {@link PackageOutlineImpl},
     * since it's guaranteed to be of that type.
     */
    public PackageOutlineImpl _package() {
        return (PackageOutlineImpl)super._package();
    }

    ClassOutlineImpl( BeanGenerator _parent,
        CClassInfo _target, JDefinedClass exposedClass, JDefinedClass _implClass, JClass _implRef ) {
        super(_target,exposedClass,_implRef,_implClass);
        this._parent = _parent;
        _package().classes.add(this);
    }

    public BeanGenerator parent() {
        return _parent;
    }
}
