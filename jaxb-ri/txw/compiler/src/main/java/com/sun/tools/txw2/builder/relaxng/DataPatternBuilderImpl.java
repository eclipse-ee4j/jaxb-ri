/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.builder.relaxng;

import com.sun.codemodel.JType;
import com.sun.tools.txw2.model.Data;
import com.sun.tools.txw2.model.Leaf;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.DataPatternBuilder;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.util.LocatorImpl;
import com.sun.tools.rngom.parse.Context;

/**
 * @author Kohsuke Kawaguchi
 */
final class DataPatternBuilderImpl implements DataPatternBuilder<Leaf,ParsedElementAnnotation,LocatorImpl,AnnotationsImpl,CommentListImpl> {
    final JType type;

    public DataPatternBuilderImpl(JType type) {
        this.type = type;
    }

    public Leaf makePattern(LocatorImpl locator, AnnotationsImpl annotations) throws BuildException {
        return new Data(locator,type);
    }

    public void addParam(String name, String value, Context context, String ns, LocatorImpl locator, AnnotationsImpl annotations) throws BuildException {
    }

    public void annotation(ParsedElementAnnotation parsedElementAnnotation) {
    }

    public Leaf makePattern(Leaf except, LocatorImpl locator, AnnotationsImpl annotations) throws BuildException {
        return makePattern(locator,annotations);
    }
}
