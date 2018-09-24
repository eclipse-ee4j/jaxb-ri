/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.visitor.XSVisitor;

/**
 * @author Kohsuke Kawaguchi
 */
abstract class ColorBinder extends BindingComponent implements XSVisitor {
    protected final BGMBuilder builder = Ring.get(BGMBuilder.class);
    protected final ClassSelector selector = getClassSelector();

    protected final CClassInfo getCurrentBean() {
        return selector.getCurrentBean();
    }
    protected final XSComponent getCurrentRoot() {
        return selector.getCurrentRoot();
    }


    protected final void createSimpleTypeProperty(XSSimpleType type,String propName) {
        BIProperty prop = BIProperty.getCustomization(type);

        SimpleTypeBuilder stb = Ring.get(SimpleTypeBuilder.class);
        // since we are building the simple type here, use buildDef
        CPropertyInfo p = prop.createValueProperty(propName,false,type,stb.buildDef(type),BGMBuilder.getName(type));
        getCurrentBean().addProperty(p);
    }





    public final void annotation(XSAnnotation xsAnnotation) {
        throw new IllegalStateException();
    }

    public final void schema(XSSchema xsSchema) {
        throw new IllegalStateException();
    }

    public final void facet(XSFacet xsFacet) {
        throw new IllegalStateException();
    }

    public final void notation(XSNotation xsNotation) {
        throw new IllegalStateException();
    }

    public final void identityConstraint(XSIdentityConstraint xsIdentityConstraint) {
        throw new IllegalStateException();
    }

    public final void xpath(XSXPath xsxPath) {
        throw new IllegalStateException();
    }
}
