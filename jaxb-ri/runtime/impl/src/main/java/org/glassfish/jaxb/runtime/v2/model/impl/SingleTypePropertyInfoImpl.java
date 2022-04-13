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

import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.model.core.NonElement;
import org.glassfish.jaxb.core.v2.model.core.PropertyInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeNonElementRef;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimePropertyInfo;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import jakarta.xml.bind.annotation.XmlList;

import java.util.Collections;
import java.util.List;

/**
 * {@link PropertyInfoImpl} that can only have one type.
 *
 * Specifically, {@link AttributePropertyInfoImpl} and {@link ValuePropertyInfoImpl}.
 *
 * @author Kohsuke Kawaguchi
 */
abstract class SingleTypePropertyInfoImpl<T,C,F,M>
    extends PropertyInfoImpl<T,C,F,M> {

    /**
     * Computed lazily.
     *
     * @see #getTarget()
     */
    private NonElement<T,C> type;

    public SingleTypePropertyInfoImpl(ClassInfoImpl<T,C,F,M> classInfo, PropertySeed<T,C,F,M> seed) {
        super(classInfo, seed);
        if(this instanceof RuntimePropertyInfo) {
            Accessor rawAcc = ((RuntimeClassInfoImpl.RuntimePropertySeed)seed).getAccessor();
            if(getAdapter()!=null && !isCollection())
                // adapter for a single-value property is handled by accessor.
                // adapter for a collection property is handled by lister.
                rawAcc = rawAcc.adapt(((RuntimePropertyInfo)this).getAdapter());
            this.acc = rawAcc;
        } else
            this.acc = null;
    }

    @Override
    public List<? extends NonElement<T,C>> ref() {
        return Collections.singletonList(getTarget());
    }

    public NonElement<T,C> getTarget() {
        if(type==null) {
            assert parent.builder!=null : "this method must be called during the build stage";
            type = parent.builder.getTypeInfo(getIndividualType(),this);
        }
        return type;
    }

    public PropertyInfo<T,C> getSource() {
        return this;
    }

    @Override
    public void link() {
        super.link();

        if (!(NonElement.ANYTYPE_NAME.equals(type.getTypeName()) || type.isSimpleType() || id()==ID.IDREF)) {
                parent.builder.reportError(new IllegalAnnotationException(
                Messages.SIMPLE_TYPE_IS_REQUIRED.format(),
                seed
            ));
        }

        if(!isCollection() && seed.hasAnnotation(XmlList.class)) {
            parent.builder.reportError(new IllegalAnnotationException(
                Messages.XMLLIST_ON_SINGLE_PROPERTY.format(), this
            ));
        }
    }

//
//
// technically these code belong to runtime implementation, but moving the code up here
// allows this to be shared between RuntimeValuePropertyInfoImpl and RuntimeAttributePropertyInfoImpl
//
//

    private final Accessor acc;
    /**
     * Lazily created.
     */
    private Transducer xducer;

    public Accessor getAccessor() {
        return acc;
    }


    public Transducer getTransducer() {
        if(xducer==null) {
            xducer = RuntimeModelBuilder.createTransducer((RuntimeNonElementRef)this);
            if(xducer==null) {
                // this situation is checked by by the link method.
                // avoid repeating the same error by silently recovering
                xducer = RuntimeBuiltinLeafInfoImpl.STRING;
            }
        }
        return xducer;
    }
}
