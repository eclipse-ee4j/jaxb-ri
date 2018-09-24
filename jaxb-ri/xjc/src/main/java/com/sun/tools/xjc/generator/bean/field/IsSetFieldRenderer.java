/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean.field;

import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.FieldOutline;

/**
 * FieldRenderer that wraps another field generator
 * and produces isSetXXX unsetXXX methods.
 * 
 * <p>
 * This follows the decorator design pattern so that
 * the caller of FieldRenderer can forget about details
 * of the method generation.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class IsSetFieldRenderer implements FieldRenderer {
    private final FieldRenderer core;
    private final boolean generateUnSetMethod;
    private final boolean generateIsSetMethod;
    
    public IsSetFieldRenderer( 
        FieldRenderer core,
        boolean generateUnSetMethod, boolean generateIsSetMethod ) {
        
        this.core = core;
        this.generateUnSetMethod = generateUnSetMethod;
        this.generateIsSetMethod = generateIsSetMethod;
    }

    public FieldOutline generate(ClassOutlineImpl context, CPropertyInfo prop) {
        return new IsSetField(context,prop,
            core.generate(context, prop),
            generateUnSetMethod,generateIsSetMethod);
    }
}
