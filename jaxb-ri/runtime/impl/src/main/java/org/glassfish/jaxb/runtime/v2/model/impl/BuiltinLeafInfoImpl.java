/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.core.v2.model.core.BuiltinLeafInfo;
import org.glassfish.jaxb.core.v2.model.core.Element;
import org.glassfish.jaxb.core.v2.model.core.LeafInfo;
import org.glassfish.jaxb.core.v2.model.nav.Navigator;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;

/**
 * JAXB spec designates a few Java classes to be mapped to XML types
 * in a way that ignores restrictions placed on user-defined beans.
 *
 * @author Kohsuke Kawaguchi
 */
public class BuiltinLeafInfoImpl<TypeT,ClassDeclT> extends LeafInfoImpl<TypeT,ClassDeclT> implements BuiltinLeafInfo<TypeT,ClassDeclT> {

    private final QName[] typeNames;

    protected BuiltinLeafInfoImpl(TypeT type, QName... typeNames) {
        super(type, typeNames.length>0?typeNames[0]:null);
        this.typeNames = typeNames;
    }

    /**
     * Returns all the type names recognized by this bean info.
     *
     * @return
     *      do not modify the returned array.
     */
    public final QName[] getTypeNames() {
        return typeNames;
    }

    /**
     * @deprecated always return false at this level.
     */
    @Override
    @Deprecated
    public final boolean isElement() {
        return false;
    }

    /**
     * @deprecated always return null at this level.
     */
    @Override
    @Deprecated
    public final QName getElementName() {
        return null;
    }

    /**
     * @deprecated always return null at this level.
     */
    @Override
    @Deprecated
    public final Element<TypeT,ClassDeclT> asElement() {
        return null;
    }

    /**
     * Creates all the s as specified in the spec.
     *
     * {@link LeafInfo}s are all defined by the spec.
     */
    public static <TypeT,ClassDeclT>
    Map<TypeT,BuiltinLeafInfoImpl<TypeT,ClassDeclT>> createLeaves( Navigator<TypeT,ClassDeclT,?,?> nav ) {
        Map<TypeT,BuiltinLeafInfoImpl<TypeT,ClassDeclT>> leaves = new HashMap<>();

        for( RuntimeBuiltinLeafInfoImpl<?> leaf : RuntimeBuiltinLeafInfoImpl.builtinBeanInfos ) {
            TypeT t = nav.ref(leaf.getClazz());
            leaves.put( t, new BuiltinLeafInfoImpl<>(t,leaf.getTypeNames()) );
        }

        return leaves;
    }
}
