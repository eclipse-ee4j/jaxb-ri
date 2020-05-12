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

import javax.xml.namespace.QName;

import com.sun.codemodel.JType;
import com.sun.tools.xjc.outline.Aspect;
import org.glassfish.jaxb.core.v2.model.util.ArrayInfoUtil;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.outline.Outline;
import org.glassfish.jaxb.core.v2.model.core.ArrayInfo;
import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * Because XJC doesn't generate the array binding, this class will
 * never show up in the model constructed by XJC.
 *
 * <p>
 * This class is nevertheless defined to make the type checker happy. 
 *
 * @author Kohsuke Kawaguchi
 */
public final class CArrayInfo extends AbstractCTypeInfoImpl implements ArrayInfo<NType,NClass>, CNonElement, NType {

    private final CNonElement itemType;

    private final QName typeName;

    public CArrayInfo(Model model,CNonElement itemType, XSComponent source, CCustomizations customizations) {
        super(model,source,customizations);
        this.itemType = itemType;
        assert itemType.getTypeName()!=null;
        this.typeName = ArrayInfoUtil.calcArrayTypeName(itemType.getTypeName());
    }

    public CNonElement getItemType() {
        return itemType;
    }

    public QName getTypeName() {
        return typeName;
    }

    public boolean isSimpleType() {
        return false;
    }

    @Deprecated // guaranteed to return this
    public CNonElement getInfo() {
        return this;
    }

    public JType toType(Outline o, Aspect aspect) {
        return itemType.toType(o,aspect).array();
    }

    public NType getType() {
        return this;
    }

    public boolean isBoxedType() {
        return false;
    }

    public String fullName() {
        return itemType.getType().fullName()+"[]";
    }

    public Locator getLocator() {
        return Model.EMPTY_LOCATOR;
    }
}
