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

import org.glassfish.jaxb.core.v2.model.core.Adapter;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.glassfish.jaxb.runtime.v2.model.runtime.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
final class RuntimeElementInfoImpl extends ElementInfoImpl<Type,Class,Field,Method>
    implements RuntimeElementInfo {
    
    public RuntimeElementInfoImpl(RuntimeModelBuilder modelBuilder, RegistryInfoImpl registry, Method method) throws IllegalAnnotationException {
        super(modelBuilder, registry, method);

        Adapter<Type,Class> a = getProperty().getAdapter();

        if(a!=null)
            adapterType = a.adapterType;
        else
            adapterType = null;
    }

    @Override
    protected PropertyImpl createPropertyImpl() {
        return new RuntimePropertyImpl();
    }

    class RuntimePropertyImpl extends PropertyImpl implements RuntimeElementPropertyInfo, RuntimeTypeRef {
        public Accessor getAccessor() {
            if(adapterType==null)
                return Accessor.JAXB_ELEMENT_VALUE;
            else
                return Accessor.JAXB_ELEMENT_VALUE.adapt(
                        (Class)getAdapter().defaultType,(Class)adapterType);
        }

        public Type getRawType() {
            return Collection.class;
        }

        public Type getIndividualType() {
             return getContentType().getType();
        }


        public boolean elementOnlyContent() {
            return false;   // this method doesn't make sense here
        }

        public List<? extends RuntimeTypeRef> getTypes() {
            return Collections.singletonList(this);
        }

        public List<? extends RuntimeNonElement> ref() {
            return (List<? extends RuntimeNonElement>)super.ref();
        }

        public RuntimeNonElement getTarget() {
            return (RuntimeNonElement)super.getTarget();
        }

        public RuntimePropertyInfo getSource() {
            return this;
        }

        public Transducer getTransducer() {
            return RuntimeModelBuilder.createTransducer(this);
        }
    }

    /**
     * The adapter specified by <code>getProperty().getAdapter()</code>.
     */
    private final Class<? extends XmlAdapter> adapterType;

    public RuntimeElementPropertyInfo getProperty() {
        return (RuntimeElementPropertyInfo)super.getProperty();
    }

    public Class<? extends JAXBElement> getType() {
        //noinspection unchecked
        return (Class<? extends JAXBElement>) Utils.REFLECTION_NAVIGATOR.erasure(super.getType());
    }

    public RuntimeClassInfo getScope() {
        return (RuntimeClassInfo)super.getScope();
    }

    public RuntimeNonElement getContentType() {
        return (RuntimeNonElement)super.getContentType();
    }
}
