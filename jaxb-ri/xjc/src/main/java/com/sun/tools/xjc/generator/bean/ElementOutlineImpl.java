/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean;


import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.ElementOutline;

/**
 * {@link ElementOutline} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
final class ElementOutlineImpl extends ElementOutline {
    private final BeanGenerator parent;

    public BeanGenerator parent() {
        return parent;
    }

    /*package*/ ElementOutlineImpl(BeanGenerator parent, CElementInfo ei) {
        super(ei,
              parent.getClassFactory().createClass(
                      parent.getContainer( ei.parent, Aspect.EXPOSED ), ei.shortName(), ei.getLocator() ));
        this.parent = parent;
        parent.elements.put(ei,this);

        JCodeModel cm = parent.getCodeModel();

        implClass._extends(
            cm.ref(JAXBElement.class).narrow(
                target.getContentInMemoryType().toType(parent,Aspect.EXPOSED).boxify()));

        if(ei.hasClass()) {
            JType implType = ei.getContentInMemoryType().toType(parent,Aspect.IMPLEMENTATION);
            JExpression declaredType = JExpr.cast(cm.ref(Class.class),implType.boxify().dotclass()); // why do we have to cast?
            JClass scope=null;
            if(ei.getScope()!=null)
                scope = parent.getClazz(ei.getScope()).implRef;
            JExpression scopeClass = scope==null?JExpr._null():scope.dotclass();
            JFieldVar valField = implClass.field(JMod.PROTECTED|JMod.FINAL|JMod.STATIC,QName.class,"NAME",createQName(cm,ei.getElementName()));

            // take this opportunity to generate a constructor in the element class
            JMethod cons = implClass.constructor(JMod.PUBLIC);
            cons.body().invoke("super")
                .arg(valField)
                .arg(declaredType)
                .arg(scopeClass)
                .arg(cons.param(implType,"value"));

            // generate no-arg constructor in the element class (bug #391; section 5.6.2 in JAXB spec 2.1)
            JMethod noArgCons = implClass.constructor(JMod.PUBLIC);
            noArgCons.body().invoke("super")
                .arg(valField)
                .arg(declaredType)
                .arg(scopeClass)
                .arg(JExpr._null());

        }
    }

    /**
     * Generates an expression that evaluates to "new QName(...)"
     */
    private JInvocation createQName(JCodeModel codeModel,QName name) {
        return JExpr._new(codeModel.ref(QName.class)).arg(name.getNamespaceURI()).arg(name.getLocalPart());
    }
}
