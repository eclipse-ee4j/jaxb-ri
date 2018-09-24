/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.core;

import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.bind.v2.model.impl.ModelBuilderI;
import com.sun.xml.bind.v2.model.nav.Navigator;

/**
 * Reference to a type in a model.
 *
 * TODO: isn't there a similarity between this and TypeUse in XJC?
 *
 * @author Kohsuke Kawaguchi
 */
public final class Ref<T,C> {
    /**
     * The type being referenced.
     * <p>
     * If the type is adapted, this field is the same as the adapter's default type.
     */
    public final T type;
    /**
     * If the reference has an adapter, non-null.
     */
    public final Adapter<T,C> adapter;
    /**
     * If the {@link #type} is an array and it is a value list,
     * true.
     */
    public final boolean valueList;

    public Ref(T type) {
        this(type,null,false);
    }

    public Ref(T type, Adapter<T, C> adapter, boolean valueList) {
        this.adapter = adapter;
        if(adapter!=null)
            type=adapter.defaultType;
        this.type = type;
        this.valueList = valueList;
    }

    public Ref(ModelBuilderI<T,C,?,?> builder, T type, XmlJavaTypeAdapter xjta, XmlList xl ) {
        this(builder.getReader(),builder.getNavigator(),type,xjta,xl);
    }

    public Ref(AnnotationReader<T,C,?,?> reader,
               Navigator<T,C,?,?> nav,
               T type, XmlJavaTypeAdapter xjta, XmlList xl ) {
        Adapter<T,C> adapter=null;
        if(xjta!=null) {
            adapter = new Adapter<T,C>(xjta,reader,nav);
            type = adapter.defaultType;
        }

        this.type = type;
        this.adapter = adapter;
        this.valueList = xl!=null;
    }
}
