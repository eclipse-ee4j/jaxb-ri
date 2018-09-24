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

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;

/**
 * {@link ObjectFactoryGenerator} used when we generate
 * interfaces and implementations in separate packages.
 *
 * <p>
 * {@link #publicOFG} and {@link #privateOFG} gives you access to
 * {@code ObjectFactory}s in both packages, if you need to.
 *
 * @author Kohsuke Kawaguchi
 */
public final class DualObjectFactoryGenerator extends ObjectFactoryGenerator {
    public final ObjectFactoryGenerator publicOFG;
    public final ObjectFactoryGenerator privateOFG;

    DualObjectFactoryGenerator(BeanGenerator outline, Model model, JPackage targetPackage) {
        this.publicOFG = new PublicObjectFactoryGenerator(outline,model,targetPackage);
        this.privateOFG = new PrivateObjectFactoryGenerator(outline,model,targetPackage);

        // put the marker so that we can detect missing jaxb.properties
        publicOFG.getObjectFactory().field(JMod.PRIVATE|JMod.STATIC|JMod.FINAL,
                Void.class, "_useJAXBProperties", JExpr._null());
    }

    void populate(CElementInfo ei) {
        publicOFG.populate(ei);
        privateOFG.populate(ei);
    }

    void populate(ClassOutlineImpl cc) {
        publicOFG.populate(cc);
        privateOFG.populate(cc);
    }

    /**
     * Returns the private version (which is what gets used at runtime.)
     */
    public JDefinedClass getObjectFactory() {
        return privateOFG.getObjectFactory();
    }
}
