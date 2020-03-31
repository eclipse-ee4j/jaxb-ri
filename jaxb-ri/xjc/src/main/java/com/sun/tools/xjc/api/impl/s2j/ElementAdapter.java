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

import jakarta.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JInvocation;

/**
 * {@link FieldOutline} that wraps another {@link FieldOutline}
 * and allows JAX-WS to access values without using about
 * {@link JAXBElement}.
 *
 * <p>
 * That means if a value is requested, we unwrap JAXBElement
 * and give it to them. If a value is set, we wrap that into
 * JAXBElement, etc.
 *
 * <p>
 * This can be used only with {@link CReferencePropertyInfo}
 * (or else it won't be {@link JAXBElement),
 * with one {@link CElementInfo} (or else we can't infer the tag name.)
 *
 * @author Kohsuke Kawaguchi
 */
abstract class ElementAdapter implements FieldOutline {
    protected final FieldOutline core;

    /**
     * The only one {@link CElementInfo} that can be in the property.
     */
    protected final CElementInfo ei;

    public ElementAdapter(FieldOutline core, CElementInfo ei) {
        this.core = core;
        this.ei = ei;
    }

    public ClassOutline parent() {
        return core.parent();
    }

    public CPropertyInfo getPropertyInfo() {
        return core.getPropertyInfo();
    }

    protected final Outline outline() {
        return core.parent().parent();
    }

    protected final JCodeModel codeModel() {
        return outline().getCodeModel();
    }

    protected abstract class FieldAccessorImpl implements FieldAccessor {
        final FieldAccessor acc;

        public FieldAccessorImpl(JExpression target) {
            acc = core.create(target);
        }

        public void unsetValues(JBlock body) {
            acc.unsetValues(body);
        }

        public JExpression hasSetValue() {
            return acc.hasSetValue();
        }

        public FieldOutline owner() {
            return ElementAdapter.this;
        }

        public CPropertyInfo getPropertyInfo() {
            return core.getPropertyInfo();
        }

        /**
         * Wraps a type value into a {@link JAXBElement}.
         */
        protected final JInvocation createJAXBElement(JExpression $var) {
            JCodeModel cm = codeModel();

            return JExpr._new(cm.ref(JAXBElement.class))
                .arg(JExpr._new(cm.ref(QName.class))
                    .arg(ei.getElementName().getNamespaceURI())
                    .arg(ei.getElementName().getLocalPart()))
                .arg(getRawType().boxify().erasure().dotclass())
                .arg($var);
        }
    }
}
