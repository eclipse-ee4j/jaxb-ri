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

import jakarta.xml.bind.JAXBContext;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.fmt.JPropertyFile;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.runtime.JAXBContextFactory;

/**
 * Generates private ObjectFactory.
 *
 * <p>
 * This class also puts a copy of {@link JAXBContextFactory}
 * to the impl package.
 *
 * @author Kohsuke Kawaguchi
 */
final class PrivateObjectFactoryGenerator extends ObjectFactoryGeneratorImpl {
    public PrivateObjectFactoryGenerator(BeanGenerator outline, Model model, JPackage targetPackage) {
        super(outline, model, targetPackage.subPackage("impl"));

        JPackage implPkg = targetPackage.subPackage("impl");

        // put JAXBContextFactory into the impl package
        JClass factory = outline.generateStaticClass(JAXBContextFactory.class,implPkg);

        // and then put jaxb.properties to point to it
        JPropertyFile jaxbProperties = new JPropertyFile("jaxb.properties");
        targetPackage.addResourceFile(jaxbProperties);
        jaxbProperties.add(
            JAXBContext.JAXB_CONTEXT_FACTORY,
            factory.fullName());
    }

    void populate(CElementInfo ei) {
        populate(ei,Aspect.IMPLEMENTATION,Aspect.IMPLEMENTATION);
    }

    void populate(ClassOutlineImpl cc) {
        populate(cc,cc.implRef);
    }
}
