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

import com.sun.istack.FinalArrayList;
import org.glassfish.jaxb.core.v2.model.core.*;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlList;

import javax.xml.namespace.QName;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

/**
 * Common {@link ElementPropertyInfo} implementation used for both
 * Annotation Processing and runtime.
 * 
 * @author Kohsuke Kawaguchi
 */
class ElementPropertyInfoImpl<TypeT,ClassDeclT,FieldT,MethodT>
    extends ERPropertyInfoImpl<TypeT,ClassDeclT,FieldT,MethodT>
    implements ElementPropertyInfo<TypeT,ClassDeclT>
{
    /**
     * Lazily computed.
     * @see #getTypes()
     */
    private List<TypeRefImpl<TypeT,ClassDeclT>> types;

    private final List<TypeInfo<TypeT,ClassDeclT>> ref = new AbstractList<>() {
        @Override
        public TypeInfo<TypeT, ClassDeclT> get(int index) {
            return getTypes().get(index).getTarget();
        }

        @Override
        public int size() {
            return getTypes().size();
        }
    };

    /**
     * Lazily computed.
     * @see #isRequired()
     */
    private Boolean isRequired;

    /**
     * @see #isValueList()
     */
    private final boolean isValueList;

    ElementPropertyInfoImpl(
        ClassInfoImpl<TypeT,ClassDeclT,FieldT,MethodT> parent,
        PropertySeed<TypeT,ClassDeclT,FieldT,MethodT> propertySeed) {
        super(parent, propertySeed);

        isValueList = seed.hasAnnotation(XmlList.class);

    }

    @Override
    public List<? extends TypeRefImpl<TypeT,ClassDeclT>> getTypes() {
        if(types==null) {
            types = new FinalArrayList<>();
            XmlElement[] ann=null;

            XmlElement xe = seed.readAnnotation(XmlElement.class);
            XmlElements xes = seed.readAnnotation(XmlElements.class);

            if(xe!=null && xes!=null) {
                parent.builder.reportError(new IllegalAnnotationException(
                        Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(
                                nav().getClassName(parent.getClazz())+'#'+seed.getName(),
                                xe.annotationType().getName(), xes.annotationType().getName()),
                        xe, xes ));
            }

            isRequired = true;

            if(xe!=null)
                ann = new XmlElement[]{xe};
            else
            if(xes!=null)
                ann = xes.value();

            if(ann==null) {
                // default
                TypeT t = getIndividualType();
                if(!nav().isPrimitive(t) || isCollection())
                    isRequired = false;
                // nillableness defaults to true if it's collection
                types.add(createTypeRef(calcXmlName((XmlElement)null),t,isCollection(),null));
            } else {
                for( XmlElement item : ann ) {
                    // TODO: handle defaulting in names.
                    QName name = calcXmlName(item);
                    TypeT type = reader().getClassValue(item, "type");
                    if (nav().isSameType(type, nav().ref(XmlElement.DEFAULT.class)))
                        type = getIndividualType();
                    if((!nav().isPrimitive(type) || isCollection()) && !item.required())
                        isRequired = false;
                    types.add(createTypeRef(name, type, item.nillable(), getDefaultValue(item.defaultValue()) ));
                }
            }
            types = Collections.unmodifiableList(types);
            assert !types.contains(null);
        }
        return types;
    }

    private String getDefaultValue(String value) {
        if(value.equals("\u0000"))
            return null;
        else
            return value;
    }

    /**
     * Used by {@link PropertyInfoImpl} to create new instances of {@link TypeRef}
     */
    protected TypeRefImpl<TypeT,ClassDeclT> createTypeRef(QName name,TypeT type,boolean isNillable,String defaultValue) {
        return new TypeRefImpl<>(this,name,type,isNillable,defaultValue);
    }

    @Override
    public boolean isValueList() {
        return isValueList;
    }

    @Override
    public boolean isRequired() {
        if(isRequired==null)
            getTypes(); // compute the value
        return isRequired;
    }

    @Override
    public List<? extends TypeInfo<TypeT,ClassDeclT>> ref() {
        return ref;
    }

    @Override
    public final PropertyKind kind() {
        return PropertyKind.ELEMENT;
    }

    @Override
    protected void link() {
        super.link();
        for (TypeRefImpl<TypeT, ClassDeclT> ref : getTypes() ) {
            ref.link();
        }

        if(isValueList()) {
            // ugly test, because IDREF's are represented as text on the wire,
            // it's OK to be a value list in that case.
            if(id()!= ID.IDREF) {
                // check if all the item types are simple types
                // this can't be done when we compute types because
                // not all TypeInfos are available yet
                for (TypeRefImpl<TypeT,ClassDeclT> ref : types) {
                    if(!ref.getTarget().isSimpleType()) {
                        parent.builder.reportError(new IllegalAnnotationException(
                        Messages.XMLLIST_NEEDS_SIMPLETYPE.format(
                            nav().getTypeName(ref.getTarget().getType())), this ));
                        break;
                    }
                }
            }

            if(!isCollection())
                parent.builder.reportError(new IllegalAnnotationException(
                    Messages.XMLLIST_ON_SINGLE_PROPERTY.format(), this
                ));
        }
    }
}
