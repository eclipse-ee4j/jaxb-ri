/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Aspect;

/**
 * Generates public ObjectFactory.
 *
 * @author Kohsuke Kawaguchi
 */
final class PublicObjectFactoryGenerator extends ObjectFactoryGeneratorImpl {
    public PublicObjectFactoryGenerator(BeanGenerator outline, Model model, JPackage targetPackage) {
        super(outline, model, targetPackage);
    }

    void populate(CElementInfo ei) {
        populate(ei,Aspect.IMPLEMENTATION,Aspect.EXPOSED);
    }

    void populate(ClassOutlineImpl cc) {
        populate(cc,cc.ref);
    }
}
