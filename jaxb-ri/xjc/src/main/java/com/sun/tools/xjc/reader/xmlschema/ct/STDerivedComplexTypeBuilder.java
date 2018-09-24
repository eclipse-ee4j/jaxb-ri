/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.ct;

import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;

/**
 * Binds a complex type derived from a simple type.
 * When a complex type is derived from a simple type, it is always
 * by extension.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class STDerivedComplexTypeBuilder extends CTBuilder {

    public boolean isApplicable(XSComplexType ct) {
        return ct.getBaseType().isSimpleType();
    }

    public void build(XSComplexType ct) {
        assert ct.getDerivationMethod()==XSType.EXTENSION;

        // base type is a simple type
        XSSimpleType baseType = ct.getBaseType().asSimpleType();

        // determine the binding of this complex type.
        builder.recordBindingMode(ct,ComplexTypeBindingMode.NORMAL);

        simpleTypeBuilder.refererStack.push(ct);
        TypeUse use = simpleTypeBuilder.build(baseType);
        simpleTypeBuilder.refererStack.pop();

        BIProperty prop = BIProperty.getCustomization(ct);
        CPropertyInfo p = prop.createValueProperty("Value",false,baseType,use, BGMBuilder.getName(baseType));
        selector.getCurrentBean().addProperty(p);

        // adds attributes and we are through.
        green.attContainer(ct);
    }

}
