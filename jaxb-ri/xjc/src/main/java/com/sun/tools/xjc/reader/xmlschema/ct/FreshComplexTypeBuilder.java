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
import static com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeBindingMode.FALLBACK_CONTENT;
import static com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeBindingMode.NORMAL;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.visitor.XSContentTypeVisitor;

/**
 * Builds a complex type that inherits from the anyType complex type.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class FreshComplexTypeBuilder extends CTBuilder {

    public boolean isApplicable(XSComplexType ct) {
        return ct.getBaseType()==schemas.getAnyType()
            &&  !ct.isMixed();  // not mixed
    }

    public void build(final XSComplexType ct) {
        XSContentType contentType = ct.getContentType();

        contentType.visit(new XSContentTypeVisitor() {
            public void simpleType(XSSimpleType st) {
                builder.recordBindingMode(ct,ComplexTypeBindingMode.NORMAL);

                simpleTypeBuilder.refererStack.push(ct);
                TypeUse use = simpleTypeBuilder.build(st);
                simpleTypeBuilder.refererStack.pop();

                BIProperty prop = BIProperty.getCustomization(ct);
                CPropertyInfo p = prop.createValueProperty("Value",false,ct,use, BGMBuilder.getName(st));
                selector.getCurrentBean().addProperty(p);
            }

            public void particle(XSParticle p) {
                // determine the binding of this complex type.

                builder.recordBindingMode(ct,
                    bgmBuilder.getParticleBinder().checkFallback(p)?FALLBACK_CONTENT:NORMAL);

                bgmBuilder.getParticleBinder().build(p);

                XSTerm term = p.getTerm();
                if(term.isModelGroup() && term.asModelGroup().getCompositor()==XSModelGroup.ALL)
                    selector.getCurrentBean().setOrdered(false);

            }

            public void empty(XSContentType e) {
                builder.recordBindingMode(ct,NORMAL);
            }
        });

        // adds attributes and we are through.
        green.attContainer(ct);
    }

}
