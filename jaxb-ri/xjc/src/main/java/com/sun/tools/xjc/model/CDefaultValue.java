/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XmlString;

/**
 * Object that computes the default value expression lazily.
 *
 * The computation is done lazily because often the default value
 * needs to refer to things (such as enum classes) that are only generated
 * after some of the outline is built.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class CDefaultValue {
    public abstract JExpression compute(Outline outline);

    /**
     * Creates a new {@link CDefaultValue} that computes the default value
     * by applying a lexical representation to a {@link TypeUse}.
     */
    public static CDefaultValue create(final TypeUse typeUse, final XmlString defaultValue) {
        return new CDefaultValue() {
            public JExpression compute(Outline outline) {
                return typeUse.createConstant(outline,defaultValue);
            }
        };
    }
}
