/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.runtime;

import org.glassfish.jaxb.core.annotation.XmlLocation;
import org.glassfish.jaxb.core.v2.model.core.ClassInfo;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public interface RuntimeClassInfo extends ClassInfo<Type,Class>, RuntimeNonElement {
    RuntimeClassInfo getBaseClass();

    // refined to return RuntimePropertyInfo
    List<? extends RuntimePropertyInfo> getProperties();
    RuntimePropertyInfo getProperty(String name);

    Method getFactoryMethod();
    
    /**
     * If {@link #hasAttributeWildcard()} is true,
     * returns the accessor to access the property.
     *
     * @return
     *      unoptimized accessor.
     *      non-null iff {@link #hasAttributeWildcard()}==true.
     *
     * @see Accessor#optimize(JAXBContextImpl)
     */
    <BeanT> Accessor<BeanT,Map<QName,String>> getAttributeWildcard();

    /**
     * If this JAXB bean has a property annotated with {@link XmlLocation},
     * this method returns it.
     *
     * @return may be null.
     */
    <BeanT> Accessor<BeanT,Locator> getLocatorField();
}
