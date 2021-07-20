/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CDefaultValue;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSWildcard;

/**
 * @author Kohsuke Kawaguchi
 */
public class BindPurple extends ColorBinder {
    public void attGroupDecl(XSAttGroupDecl xsAttGroupDecl) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void attributeDecl(XSAttributeDecl xsAttributeDecl) {
        // TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Attribute use always becomes a property.
     */
    public void attributeUse(XSAttributeUse use) {
        boolean hasFixedValue = use.getFixedValue()!=null;
        BIProperty pc = BIProperty.getCustomization(use);

        // map to a constant property ?
        boolean toConstant = pc.isConstantProperty() && hasFixedValue;
        TypeUse attType = bindAttDecl(use.getDecl());

        CPropertyInfo prop = pc.createAttributeProperty( use, attType );

        if(toConstant) {
            prop.defaultValue = CDefaultValue.create(attType,use.getFixedValue());
            prop.realization = builder.fieldRendererFactory.getConst(prop.realization);
        } else
        if(!attType.isCollection() && (prop.baseType == null ? true : !prop.baseType.isPrimitive())) {
            // don't support a collection default value. That's difficult to do.
            // primitive types default value is problematic too - we can't check whether it has been set or no ( ==null) isn't possible TODO: emit a waring in these cases

            if(use.getDefaultValue()!=null) {
                // this attribute use has a default value.
                // the item type is guaranteed to be a leaf type... or TODO: is it really so?
                // don't support default values if it's a list
                prop.defaultValue = CDefaultValue.create(attType,use.getDefaultValue());
            } else
            if(use.getFixedValue()!=null) {
                prop.defaultValue = CDefaultValue.create(attType,use.getFixedValue());
            }
        } else if(prop.baseType != null && prop.baseType.isPrimitive()) {
            ErrorReporter errorReporter = Ring.get(ErrorReporter.class);

            errorReporter.warning(prop.getLocator(), Messages.WARN_DEFAULT_VALUE_PRIMITIVE_TYPE, prop.baseType.name());
        }

        getCurrentBean().addProperty(prop);
    }

    private TypeUse bindAttDecl(XSAttributeDecl decl) {
        SimpleTypeBuilder stb = Ring.get(SimpleTypeBuilder.class);
        stb.refererStack.push( decl );
        try {
            return stb.build(decl.getType());
        } finally {
            stb.refererStack.pop();
        }
    }


    public void complexType(XSComplexType ct) {
        CClass ctBean = selector.bindToType(ct,null,false);
        if(getCurrentBean()!=ctBean)
            // in some case complex type and element binds to the same class
            // don't make it has-a. Just make it is-a.
            getCurrentBean().setBaseClass(ctBean);
    }

    public void wildcard(XSWildcard xsWildcard) {
        // element wildcards are processed by particle binders,
        // so this one is for attribute wildcard.

        getCurrentBean().hasAttributeWildcard(true);
    }

    public void modelGroupDecl(XSModelGroupDecl xsModelGroupDecl) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void modelGroup(XSModelGroup xsModelGroup) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void elementDecl(XSElementDecl xsElementDecl) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void simpleType(XSSimpleType type) {
        createSimpleTypeProperty(type,"Value");
    }

    public void particle(XSParticle xsParticle) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public void empty(XSContentType ct) {
        // empty generates nothing
    }
}
