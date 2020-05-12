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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Map;

import jakarta.activation.MimeType;
import jakarta.xml.bind.annotation.W3CDomHandler;
import javax.xml.namespace.QName;

import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.core.v2.model.core.ReferencePropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.WildcardMode;
import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * {@link ReferencePropertyInfo} for the compiler.
 *
 * @author Kohsuke Kawaguchi
 */
public final class CReferencePropertyInfo extends CPropertyInfo implements ReferencePropertyInfo<NType,NClass> {

    /**
     * True if this property can never be absent legally.
     */
    private final boolean required;

    /**
     * List of referenced elements.
     */
    private final Set<CElement> elements = new LinkedHashSet<CElement>();

    private final boolean isMixed;
    private WildcardMode wildcard;
    private boolean dummy;
    private boolean content;
    private boolean isMixedExtendedCust = false;

    public CReferencePropertyInfo(String name, boolean collection, boolean required, boolean isMixed, XSComponent source,
                                  CCustomizations customizations, Locator locator, boolean dummy, boolean content, boolean isMixedExtended) {   // 'dummy' and 'content' here for NHIN fix - a hack in order to be able to handle extended mixed types better
        super(name, (collection||isMixed) && (!dummy), source, customizations, locator);
        this.isMixed = isMixed;
        this.required = required;
        this.dummy = dummy;
        this.content = content;
        this.isMixedExtendedCust = isMixedExtended;
    }

    public Set<? extends CTypeInfo> ref() {
//        if(wildcard==null && !isMixed())
//            return getElements();

        // ugly hack to get the signature right for substitution groups
        // when a class is generated for elements,they don't form a nice type hierarchy,
        // so the Java types of the substitution members need to be taken into account
        // when computing the signature

        final class RefList extends LinkedHashSet<CTypeInfo> {
            RefList() {
                super(elements.size());
                addAll(elements);
            }
            @Override
            public boolean addAll( Collection<? extends CTypeInfo> col ) {
                boolean r = false;
                for (CTypeInfo e : col) {
                    if(e instanceof CElementInfo) {
                        // UGLY. element substitution is implemented in a way that
                        // the derived elements are not assignable to base elements.
                        // so when we compute the signature, we have to take derived types
                        // into account
                        r |= addAll( ((CElementInfo)e).getSubstitutionMembers());
                    }
                    r |= add(e);
                }
                return r;
            }
        }

        RefList r = new RefList();
        if(wildcard!=null) {
            if(wildcard.allowDom)
                r.add(CWildcardTypeInfo.INSTANCE);
            if(wildcard.allowTypedObject)
                // we aren't really adding an AnyType.
                // this is a kind of hack to generate Object as a signature
                r.add(CBuiltinLeafInfo.ANYTYPE);
        }
        if(isMixed())
            r.add(CBuiltinLeafInfo.STRING);

        return r;
    }

    public Set<CElement> getElements() {
        return elements;
    }

    public boolean isMixed() {
        return isMixed;
    }

    public boolean isDummy() {
        return dummy;
    }

    public boolean isContent() {
        return content;
    }

    public boolean isMixedExtendedCust() {
        return isMixedExtendedCust;
    }

    /**
     * We'll never use a wrapper element in XJC. Always return null.
     */
    @Deprecated
    public QName getXmlName() {
        return null;
    }

    /**
     * Reference properties refer to elements, and none of the Java primitive type
     * maps to an element. Thus a reference property is always unboxable.
     */
    @Override
    public boolean isUnboxable() {
        return false;
    }

    // the same as above
    @Override
    public boolean isOptionalPrimitive() {
        return false;
    }

    public <V> V accept(CPropertyVisitor<V> visitor) {
        return visitor.onReference(this);
    }

    @Override
    public <R, P> R accept(CPropertyVisitor2<R, P> visitor, P p) {
        return visitor.visit(this, p);
    }

    public CAdapter getAdapter() {
        return null;
    }

    public final PropertyKind kind() {
        return PropertyKind.REFERENCE;
    }

    /**
     * A reference property can never be ID/IDREF because they always point to
     * other element classes.
     */
    public ID id() {
        return ID.NONE;
    }

    public WildcardMode getWildcard() {
        return wildcard;
    }

    public void setWildcard(WildcardMode mode) {
        this.wildcard = mode;
    }

    public NClass getDOMHandler() {
        // TODO: support other DOM handlers
        if(getWildcard()!=null)
            return NavigatorImpl.create(W3CDomHandler.class);
        else
            return null;
    }

    public MimeType getExpectedMimeType() {
        return null;
    }
    
    public boolean isCollectionNillable() {
        // in XJC, we never recognize a nillable collection pattern, so this is always false.
        return false;
    }

    public boolean isCollectionRequired() {
        // in XJC, we never recognize a nillable collection pattern, so this is always false.
        return false;
    }

    // reference property cannot have a type.
    public QName getSchemaType() {
        return null;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public QName collectElementNames(Map<QName, CPropertyInfo> table) {
        for (CElement e : elements) {
            QName n = e.getElementName();
            if(table.containsKey(n))
                return n;
            table.put(n,this);
        }
        return null;
    }
}
