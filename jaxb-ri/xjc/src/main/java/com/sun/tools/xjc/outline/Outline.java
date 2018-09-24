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

import java.util.Collection;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.util.CodeModelClassFactory;

/**
 * Root of the outline. Captures which code is generated for which model component.
 *
 * <p>
 * This object also provides access to various utilities, such as
 * error reporting etc, for the convenience of code that builds the outline.
 *
 * @author Kohsuke Kawaguchi
 */
public interface Outline {
    /**
     * This outline is for this model.
     */
    Model getModel();

    /**
     * Short for {@code getModel().codeModel}.
     */
    JCodeModel getCodeModel();

    /** Gets the object that wraps the generated field for a given {@link CPropertyInfo}. */
    FieldOutline getField( CPropertyInfo fu );
    
    /**
     * Gets per-package context information.
     * 
     * This method works for every visible package
     * (those packages which are supposed to be used by client applications.)
     * 
     * @return
     *      If this grammar doesn't produce anything in the specified
     *      package, return null.
     */
    PackageOutline getPackageContext( JPackage _Package );

    /**
     * Returns all the {@link ClassOutline}s known to this object.
     */
    Collection<? extends ClassOutline> getClasses();

    /**
     * Obtains per-class context information.
     */
    ClassOutline getClazz( CClassInfo clazz );

    /**
     * If the {@link CElementInfo} generates a class,
     * returns such a class. Otherwise return null.
     */
    ElementOutline getElement(CElementInfo ei);

    EnumOutline getEnum(CEnumLeafInfo eli);

    /**
     * Gets all the {@link EnumOutline}s.
     */
    Collection<EnumOutline> getEnums();

    /** Gets all package-wise contexts at once. */
    Iterable<? extends PackageOutline> getAllPackageContexts();
   
    /**
     * Gets a reference to
     * <code>new CodeModelClassFactory(getErrorHandler())</code>.
     */
    CodeModelClassFactory getClassFactory();
    
    /**
     * Any error during the back-end proccessing should be
     * sent to this object.
     */
    ErrorReceiver getErrorReceiver();

    JClassContainer getContainer(CClassInfoParent parent, Aspect aspect );

    /**
     * Resolves a type reference to the actual (possibly generated) type.
     *
     * Short for {@code resolve(ref.getType(),aspect)}.
     */
    JType resolve(CTypeRef ref,Aspect aspect);

    /**
     * Copies the specified class into the user's package and returns
     * a reference to it.
     */
    JClass addRuntime(Class clazz);
}
