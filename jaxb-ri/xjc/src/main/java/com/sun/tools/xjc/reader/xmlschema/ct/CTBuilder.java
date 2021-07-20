/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.ct;

import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.BindGreen;
import com.sun.tools.xjc.reader.xmlschema.ClassSelector;
import com.sun.tools.xjc.reader.xmlschema.SimpleTypeBuilder;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSSchemaSet;

/**
 * Builds a field expression from a complex type.
 *
 * Depending on a "kind" of complex type, the binding is
 * quite different. For example, how a complex type is bound
 * when it is extended from another complex type is very
 * different from how it's bound when it has, say, mixed content model.
 *
 * Each different algorithm of binding a complex type is implemented
 * as an implementation of this interface.
 *
 * @author
 *  Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
abstract class CTBuilder {
    /**
     * Returns true if this owner can handle the given complex type.
     */
    abstract boolean isApplicable(XSComplexType ct);

    /**
     * Binds the given complex type. This method will be called
     * only when the <code>isApplicable</code> method returns true.
     */
    abstract void build(XSComplexType ct);

    protected final ComplexTypeFieldBuilder builder = Ring.get(ComplexTypeFieldBuilder.class);
    protected final ClassSelector selector = Ring.get(ClassSelector.class);
    protected final SimpleTypeBuilder simpleTypeBuilder = Ring.get(SimpleTypeBuilder.class);
    protected final ErrorReceiver errorReceiver = Ring.get(ErrorReceiver.class);
    protected final BindGreen green = Ring.get(BindGreen.class);
    protected final XSSchemaSet schemas = Ring.get(XSSchemaSet.class);
    protected final BGMBuilder bgmBuilder = Ring.get(BGMBuilder.class);
}
