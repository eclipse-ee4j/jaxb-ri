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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.Property;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import org.glassfish.jaxb.core.v2.model.core.ClassInfo;
import org.glassfish.jaxb.core.v2.model.core.ReferencePropertyInfo;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSTerm;

/**
 * Partial common implementation between {@link ElementMappingImpl} and {@link BeanMappingImpl}
 *
 * @author Kohsuke Kawaguchi
 */
abstract class AbstractMappingImpl<InfoT extends CElement> implements Mapping {

    protected final JAXBModelImpl parent;
    protected final InfoT clazz;
    /**
     * Lazily computed.
     *
     * @see #getWrapperStyleDrilldown()
     */
    private List<Property> drilldown = null;
    private boolean drilldownComputed = false;

    protected AbstractMappingImpl(JAXBModelImpl parent, InfoT clazz) {
        this.parent = parent;
        this.clazz = clazz;
    }

    public final QName getElement() {
        return clazz.getElementName();
    }

    public final String getClazz() {
        return clazz.getType().fullName();
    }

    public final List<? extends Property> getWrapperStyleDrilldown() {
        if (!drilldownComputed) {
            drilldownComputed = true;
            drilldown = calcDrilldown();
        }
        return drilldown;
    }

    protected abstract List<Property> calcDrilldown();

    /**
     * Derived classes can use this method to implement {@link #calcDrilldown}.
     */
    protected List<Property> buildDrilldown(CClassInfo typeBean) {
        //JAXWS 2.1 spec 2.3.1.2:
        //Wrapper style if the wrapper elements only contain child elements,
        //they must not contain xsd:choice
        if (containingChoice(typeBean)) {
            return null;
        }

        List<Property> result;

        CClassInfo bc = typeBean.getBaseClass();
        if (bc != null) {
            result = buildDrilldown(bc);
            if (result == null) {
                return null;        // aborted
            }
        } else {
            result = new ArrayList<Property>();
        }

        for (CPropertyInfo p : typeBean.getProperties()) {
            if (p instanceof CElementPropertyInfo) {
                CElementPropertyInfo ep = (CElementPropertyInfo) p;
// wrong. A+,B,C is eligible for drill-down.
//                if(ep.isCollection())
//                    // content model like A+,B,C is not eligible
//                    return null;

                List<? extends CTypeRef> ref = ep.getTypes();
                if (ref.size() != 1) {// content model like (A|B),C is not eligible
                    return null;
                }

                result.add(createPropertyImpl(ep, ref.get(0).getTagName()));
            } else if (p instanceof ReferencePropertyInfo) {
                CReferencePropertyInfo rp = (CReferencePropertyInfo) p;

                Collection<CElement> elements = rp.getElements();
                if (elements.size() != 1) {
                    return null;
                }

                CElement ref = elements.iterator().next();
                if (ref instanceof ClassInfo) {
                    result.add(createPropertyImpl(rp, ref.getElementName()));
                } else {
                    CElementInfo eref = (CElementInfo) ref;
                    if (!eref.getSubstitutionMembers().isEmpty()) {
                        return null;    // elements with a substitution group isn't qualified for the wrapper style
                    }
                    // JAX-WS doesn't want to see JAXBElement, so we have to hide it for them.
                    ElementAdapter fr;
                    if (rp.isCollection()) {
                        fr = new ElementCollectionAdapter(parent.outline.getField(rp), eref);
                    } else {
                        fr = new ElementSingleAdapter(parent.outline.getField(rp), eref);
                    }

                    result.add(new PropertyImpl(this,
                            fr, eref.getElementName()));
                }
            } else {// to be eligible for the wrapper style, only elements are allowed.
                    // according to the JAX-RPC spec 2.3.1.2, element refs are disallowed
                return null;
            }

        }

        return result;
    }

    private boolean containingChoice(CClassInfo typeBean) {
        XSComponent component = typeBean.getSchemaComponent();
        if (component instanceof XSComplexType) {
            XSContentType contentType = ((XSComplexType) component).getContentType();
            XSParticle particle = contentType.asParticle();
            if (particle != null) {
                XSTerm term = particle.getTerm();
                XSModelGroup modelGroup = term.asModelGroup();
                if (modelGroup != null) {
                    return (modelGroup.getCompositor() == XSModelGroup.Compositor.CHOICE);
                }
            }
        }

        return false;
    }

    private Property createPropertyImpl(CPropertyInfo p, QName tagName) {
        return new PropertyImpl(this,
                parent.outline.getField(p), tagName);
    }
}
