/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.core;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.glassfish.jaxb.core.v2.model.annotation.AnnotationReader;
import org.glassfish.jaxb.core.v2.model.nav.Navigator;

/**
 * {@link Adapter} that wraps {@link XmlJavaTypeAdapter}.
 *
 * @author Kohsuke Kawaguchi
 */
public class Adapter<TypeT,ClassDeclT> {
    /**
     * The adapter class. Always non-null.
     *
     * A class that derives from {@link jakarta.xml.bind.annotation.adapters.XmlAdapter}.
     */
    public final ClassDeclT adapterType;

    /**
     * The type that the JAXB can handle natively.
     * The {@code Default} parameter of {@code XmlAdapter<Default,Custom>}.
     *
     * Always non-null.
     */
    public final TypeT defaultType;

    /**
     * The type that is stored in memory.
     * The {@code Custom} parameter of {@code XmlAdapter<Default,Custom>}.
     */
    public final TypeT customType;



    public Adapter(
        XmlJavaTypeAdapter spec,
        AnnotationReader<TypeT,ClassDeclT,?,?> reader,
        Navigator<TypeT,ClassDeclT,?,?> nav) {

        this( nav.asDecl(reader.getClassValue(spec,"value")), nav );
    }

    public Adapter(ClassDeclT adapterType,Navigator<TypeT,ClassDeclT,?,?> nav) {
        this.adapterType = adapterType;
        TypeT baseClass = nav.getBaseClass(nav.use(adapterType), nav.asDecl(XmlAdapter.class));

        // because the parameterization of XmlJavaTypeAdapter requires that the class derives from XmlAdapter.
        assert baseClass!=null;

        if(nav.isParameterizedType(baseClass))
            defaultType = nav.getTypeArgument(baseClass,0);
        else
            defaultType = nav.ref(Object.class);

        if(nav.isParameterizedType(baseClass))
            customType = nav.getTypeArgument(baseClass,1);
        else
            customType = nav.ref(Object.class);
    }
}
