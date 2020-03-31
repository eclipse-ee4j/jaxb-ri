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

import com.sun.codemodel.JType;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.tools.xjc.model.CElementInfo;

/**
 * {@link ElementAdapter} that works with a single {@link JAXBElement}.
 *
 * @author Kohsuke Kawaguchi
 */
final class ElementSingleAdapter extends ElementAdapter {
    public ElementSingleAdapter(FieldOutline core, CElementInfo ei) {
        super(core, ei);
    }

    public JType getRawType() {
        return ei.getContentInMemoryType().toType(outline(), Aspect.EXPOSED);
    }

    public FieldAccessor create(JExpression targetObject) {
        return new FieldAccessorImpl(targetObject);
    }

    final class FieldAccessorImpl extends ElementAdapter.FieldAccessorImpl {
        public FieldAccessorImpl(JExpression target) {
            super(target);
        }

        public void toRawValue(JBlock block, JVar $var) {
            // [RESULT]
            // if([core.hasSetValue])
            //   $var = [core.toRawValue].getValue();
            // else
            //   $var = null;

            JConditional cond = block._if(acc.hasSetValue());
            JVar $v = cond._then().decl(core.getRawType(), "v" + hashCode());// TODO: unique value control
            acc.toRawValue(cond._then(),$v);
            cond._then().assign($var,$v.invoke("getValue"));
            cond._else().assign($var, JExpr._null());
        }

        public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
            // [RESULT]
            // [core.fromRawValue](new JAXBElement(tagName, TYPE, $var));

            acc.fromRawValue(block,uniqueName, createJAXBElement($var));
        }
    }
}
