/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api.impl.s2j;

import jakarta.xml.bind.annotation.XmlAttachmentRef;
import jakarta.xml.bind.annotation.XmlList;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.api.TypeAndAnnotation;
import com.sun.tools.xjc.generator.annotation.spec.XmlJavaTypeAdapterWriter;
import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.nav.NType;
import static com.sun.tools.xjc.outline.Aspect.EXPOSED;
import com.sun.tools.xjc.outline.Outline;
import org.glassfish.jaxb.core.v2.runtime.SwaRefAdapterMarker;

/**
 * {@link TypeAndAnnotation} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
final class TypeAndAnnotationImpl implements TypeAndAnnotation {
    private final TypeUse typeUse;
    private final Outline outline;

    public TypeAndAnnotationImpl(Outline outline, TypeUse typeUse) {
        this.typeUse = typeUse;
        this.outline = outline;
    }

    public JType getTypeClass() {
        CAdapter a = typeUse.getAdapterUse();
        NType nt;
        if(a!=null)
            nt = a.customType;
        else
            nt = typeUse.getInfo().getType();

        JType jt = nt.toType(outline,EXPOSED);

        JPrimitiveType prim = jt.boxify().getPrimitiveType();
        if(!typeUse.isCollection() && prim!=null)
            jt = prim;

        if(typeUse.isCollection())
            jt = jt.array();

        return jt;
    }

    public void annotate(JAnnotatable programElement) {
        if(typeUse.getAdapterUse()==null && !typeUse.isCollection())
            return; // nothing

        CAdapter adapterUse = typeUse.getAdapterUse();
        if(adapterUse!=null) {
            // ugly, ugly hack
            if(adapterUse.getAdapterIfKnown() == SwaRefAdapterMarker.class) {
                programElement.annotate(XmlAttachmentRef.class);
            } else {
                // [RESULT]
                // @XmlJavaTypeAdapter( Foo.class )
                programElement.annotate2(XmlJavaTypeAdapterWriter.class).value(
                    adapterUse.adapterType.toType(outline,EXPOSED));
            }
        }
        if(typeUse.isCollection())
            programElement.annotate(XmlList.class);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        // TODO: support annotations
        builder.append(getTypeClass());
        return builder.toString();
    }

    public boolean equals(Object o) {
        if (!(o instanceof TypeAndAnnotationImpl)) return false;
        TypeAndAnnotationImpl that = (TypeAndAnnotationImpl) o;
        return this.typeUse==that.typeUse;
    }

    public int hashCode() {
        return typeUse.hashCode();
    }
}
