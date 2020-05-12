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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.activation.MimeType;
import javax.xml.namespace.QName;

import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.reader.RawTypeSet;
import org.glassfish.jaxb.core.v2.model.core.ElementPropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * {@link ElementPropertyInfo} for the compiler.
 *
 * @author Kohsuke Kawaguchi
 */
public final class CElementPropertyInfo extends CPropertyInfo implements ElementPropertyInfo<NType,NClass> {

    /**
     * True if this property can never be absent legally.
     */
    private final boolean required;

    private final MimeType expectedMimeType;
    /**
     *
     * <p>
     * Currently, this is set inside {@link RawTypeSet} in a very ugly way.
     */
    private CAdapter adapter;

    private final boolean isValueList;

    private ID id;


    /**
     * List of referenced types.
     */
    private final List<CTypeRef> types = new ArrayList<CTypeRef>();

    private final List<CNonElement> ref = new AbstractList<CNonElement>() {
        public CNonElement get(int index) {
            return getTypes().get(index).getTarget();
        }
        public int size() {
            return getTypes().size();
        }
    };

    // TODO: shouldn't they get id and expectedMimeType from TypeUses of CTypeRef?
    public CElementPropertyInfo(String name, CollectionMode collection, ID id, MimeType expectedMimeType, XSComponent source,
                                CCustomizations customizations, Locator locator, boolean required) {
        super(name, collection.col, source, customizations, locator);
        this.required = required;
        this.id = id;
        this.expectedMimeType = expectedMimeType;
        this.isValueList = collection.val;
    }

    public ID id() {
        return id;
    }

    public List<CTypeRef> getTypes() {
        return types;
    }

    public List<CNonElement> ref() {
        return ref;
    }

    public QName getSchemaType() {
        if(types.size()!=1)
            // if more than one kind is here, can't produce @XmlSchemaType.
            // TODO: is it allowed to have one generated if types
            return null;

        CTypeRef t = types.get(0);
        if(needsExplicitTypeName(t.getTarget(),t.typeName))
            return t.typeName;
        else
            return null;
    }

    /**
     * XJC never uses the wrapper element. Always return null.
     */
    @Deprecated
    public QName getXmlName() {
        return null;
    }

    public boolean isCollectionRequired() {
        // in XJC, we never recognize a nillable collection pattern, so this is always false.
        return false;
    }

    public boolean isCollectionNillable() {
        // in XJC, we never recognize a nillable collection pattern, so this is always false.
        return false;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isValueList() {
        return isValueList;
    }

    public boolean isUnboxable() {
        if(!isCollection() && !required)
            // if the property can be legally absent, it's not unboxable
            return false;
        // we need to have null to represent the absence of value. not unboxable.
        for (CTypeRef t : getTypes()) {
            if(t.isNillable())
                return false;
        }
        return super.isUnboxable();
    }

    @Override
    public boolean isOptionalPrimitive() {
        // we need to have null to represent the absence of value. not unboxable.
        for (CTypeRef t : getTypes()) {
            if(t.isNillable())
                return false;
        }
        return !isCollection() && !required && super.isUnboxable();
    }

    public <V> V accept(CPropertyVisitor<V> visitor) {
        return visitor.onElement(this);
    }

    @Override
    public <R, P> R accept(CPropertyVisitor2<R, P> visitor, P p) {
        return visitor.visit(this, p);
    }

    public CAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CAdapter a) {
        assert adapter==null;
        adapter = a;
    }

    public final PropertyKind kind() {
        return PropertyKind.ELEMENT;
    }

    public MimeType getExpectedMimeType() {
        return expectedMimeType;
    }

    public static enum CollectionMode {
        NOT_REPEATED(false,false),
        REPEATED_ELEMENT(true,false),
        REPEATED_VALUE(true,true);

        private final boolean col,val;

        CollectionMode(boolean col,boolean val) {
            this.col = col;
            this.val = val;
        }

        public boolean isRepeated() { return col; }
    }

    @Override
    public QName collectElementNames(Map<QName, CPropertyInfo> table) {
        for (CTypeRef t : types) {
            QName n = t.getTagName();
            if(table.containsKey(n))    return n;
            table.put(n, this);
        }
        return null;
    }
}
