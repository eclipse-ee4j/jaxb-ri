/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.outline;

import java.util.Set;

import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.generator.bean.ObjectFactoryGenerator;

/**
 * Outline object that provides per-package information.
 * 
 * This interface is accessible from {@link Outline}.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface PackageOutline {
    
    /**
     * The exposed package this context is representing.
     *
     * <p>
     * An exposed package is a package visible to users, a package
     * supposed to be used by client applications. Sometime
     * we have another parallel package that's not visible to users.
     */
    JPackage _package();

    /**
     * Generated ObjectFactory from package.
     * 
     * This method allows a caller to obtain a reference to such
     * ObjectFactory from its package.
     *
     * Must not be null.
     */
    JDefinedClass objectFactory();

    /**
     * Generates an ObjectFactory class for this package.
     */
    ObjectFactoryGenerator objectFactoryGenerator();

    /**
     * Gets {@link ClassOutline}s whose {@link ClassOutline#_package()}
     * points to this object.
     *
     * @return can be empty but never null.
     */
    Set<? extends ClassOutline> getClasses();

    /**
     * The namespace URI most commonly used in classes in this package.
     * This should be used as the namespace URI for {@link XmlSchema#namespace()}.
     *
     * <p>
     * Null if no default
     */
    public String getMostUsedNamespaceURI();

    /**
     * The element form default for this package.
     * <p>
     * The value is computed by examining what would yield the smallest generated code.
     */
    public XmlNsForm getElementFormDefault();

    /**
     * The attribute form default for this package.
     * <p>
     * The value is computed by examining what would yield the smallest generated code.
     */
    public XmlNsForm getAttributeFormDefault();
}
