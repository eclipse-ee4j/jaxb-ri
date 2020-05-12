/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.ArrayInfo;
import org.glassfish.jaxb.core.v2.model.core.NonElement;
import org.glassfish.jaxb.core.v2.model.util.ArrayInfoUtil;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.core.v2.runtime.Location;

import javax.xml.namespace.QName;

/**
 *
 * <p>
 * Public because XJC needs to access it
 *
 * @author Kohsuke Kawaguchi
 */
public class ArrayInfoImpl<TypeT,ClassDeclT,FieldT,MethodT>
    extends TypeInfoImpl<TypeT,ClassDeclT,FieldT,MethodT>
    implements ArrayInfo<TypeT,ClassDeclT>, Location {

    private final NonElement<TypeT,ClassDeclT> itemType;

    private final QName typeName;

    /**
     * The representation of T[] in the underlying reflection library.
     */
    private final TypeT arrayType;

    public ArrayInfoImpl(ModelBuilder<TypeT,ClassDeclT,FieldT,MethodT> builder,
                         Locatable upstream, TypeT arrayType) {
        super(builder, upstream);
        this.arrayType = arrayType;
        TypeT componentType = nav().getComponentType(arrayType);
        this.itemType = builder.getTypeInfo(componentType, this);

        QName n = itemType.getTypeName();
        if(n==null) {
            builder.reportError(new IllegalAnnotationException(Messages.ANONYMOUS_ARRAY_ITEM.format(
                nav().getTypeName(componentType)),this));
            n = new QName("#dummy"); // for error recovery
        }
        this.typeName = ArrayInfoUtil.calcArrayTypeName(n);
    }

    public NonElement<TypeT, ClassDeclT> getItemType() {
        return itemType;
    }

    public QName getTypeName() {
        return typeName;
    }

    public boolean isSimpleType() {
        return false;
    }

    public TypeT getType() {
        return arrayType;
    }

    /**
     * Leaf-type cannot be referenced from IDREF.
     *
     * @deprecated
     *      why are you calling a method whose return value is always known?
     */
    public final boolean canBeReferencedByIDREF() {
        return false;
    }

    public Location getLocation() {
        return this;
    }
    public String toString() {
        return nav().getTypeName(arrayType);
    }
}
