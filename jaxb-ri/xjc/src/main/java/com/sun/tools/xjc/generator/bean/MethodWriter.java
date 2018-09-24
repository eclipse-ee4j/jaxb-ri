/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * Use is subject to the license terms.
 */
package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.outline.ClassOutline;

/**
 * The back-end may or may not generate the content interface
 * separately from the implementation class. If so, a method
 * needs to be declared on both the interface and the implementation class.
 * <p>
 * This class hides those details and allow callers to declare
 * methods just once.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class MethodWriter {
    protected final JCodeModel codeModel;
    
    protected MethodWriter(ClassOutline context) {
        this.codeModel = context.parent().getCodeModel();
    }

    /**
     * Declares a method in both the interface and the implementation.
     * 
     * @return
     *      JMethod object that represents a newly declared method
     *      on the implementation class.
     */
    public abstract JMethod declareMethod( JType returnType, String methodName );
    
    public final JMethod declareMethod( Class returnType, String methodName ) {
        return declareMethod( codeModel.ref(returnType), methodName );
    }
    
    /**
     * To generate javadoc for the previously declared method, use this method
     * to obtain a {@link JDocComment} object. This may return a value
     * different from declareMethod().javadoc().
     */
    public abstract JDocComment javadoc();

            
    /**
     * Adds a parameter to the previously declared method.
     * 
     * @return
     *      JVar object that represents a newly added parameter
     *      on the implementation class.
     */
    public abstract JVar addParameter( JType type, String name );
    
    public final JVar addParameter( Class type, String name ) {
        return addParameter( codeModel.ref(type), name );
    }
}
