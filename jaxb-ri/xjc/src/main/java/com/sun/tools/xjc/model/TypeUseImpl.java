/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import jakarta.activation.MimeType;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JStringLiteral;
import com.sun.tools.xjc.outline.Outline;
import org.glassfish.jaxb.core.v2.ClassFactory;
import org.glassfish.jaxb.core.v2.model.core.ID;
import com.sun.xml.xsom.XmlString;


/**
 * General-purpose {@link TypeUse} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
final class TypeUseImpl implements TypeUse {
    private final CNonElement coreType;
    private final boolean collection;
    private final CAdapter adapter;
    private final ID id;
    private final MimeType expectedMimeType;


    public TypeUseImpl(CNonElement itemType, boolean collection, ID id, MimeType expectedMimeType, CAdapter adapter) {
        this.coreType = itemType;
        this.collection = collection;
        this.id = id;
        this.expectedMimeType = expectedMimeType;
        this.adapter = adapter;
    }

    public boolean isCollection() {
        return collection;
    }

    public CNonElement getInfo() {
        return coreType;
    }

    public CAdapter getAdapterUse() {
        return adapter;
    }

    public ID idUse() {
        return id;
    }

    public MimeType getExpectedMimeType() {
        return expectedMimeType;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TypeUseImpl)) return false;

        final TypeUseImpl that = (TypeUseImpl) o;

        if (collection != that.collection) return false;
        if (this.id != that.id ) return false;
        if (adapter != null ? !adapter.equals(that.adapter) : that.adapter != null) return false;
        if (coreType != null ? !coreType.equals(that.coreType) : that.coreType != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (coreType != null ? coreType.hashCode() : 0);
        result = 29 * result + (collection ? 1 : 0);
        result = 29 * result + (adapter != null ? adapter.hashCode() : 0);
        return result;
    }


    public JExpression createConstant(Outline outline, XmlString lexical) {
        if(isCollection())  return null;

        if(adapter==null)     return coreType.createConstant(outline, lexical);

        // [RESULT] new Adapter().unmarshal(CONSTANT);
        JExpression cons = coreType.createConstant(outline, lexical);
        Class<? extends XmlAdapter> atype = adapter.getAdapterIfKnown();

        // try to run the adapter now rather than later.
        if(cons instanceof JStringLiteral && atype!=null) {
            JStringLiteral scons = (JStringLiteral) cons;
            XmlAdapter a = ClassFactory.create(atype);
            try {
                Object value = a.unmarshal(scons.str);
                if(value instanceof String) {
                    return JExpr.lit((String)value);
                }
            } catch (Exception e) {
                // assume that we can't eagerly bind this
            }
        }

        return JExpr._new(adapter.getAdapterClass(outline)).invoke("unmarshal").arg(cons);
    }
}
