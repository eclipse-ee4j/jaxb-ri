/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api.impl.s2j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JClass;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.JAXBModel;
import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.TypeAndAnnotation;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;

/**
 * {@link JAXBModel} implementation.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class JAXBModelImpl implements S2JJAXBModel {
    /*package*/ final Outline outline;

    /**
     * All the known classes.
     */
    private final Model model;

    private final Map<QName,Mapping> byXmlName = new HashMap<QName,Mapping>();

    JAXBModelImpl(Outline outline) {
        this.model = outline.getModel();
        this.outline = outline;

        for (CClassInfo ci : model.beans().values()) {
            if(!ci.isElement())
                continue;
            byXmlName.put(ci.getElementName(),new BeanMappingImpl(this,ci));
        }
        for (CElementInfo ei : model.getElementMappings(null).values()) {
            byXmlName.put(ei.getElementName(),new ElementMappingImpl(this,ei));
        }
    }

    public JCodeModel generateCode(Plugin[] extensions,ErrorListener errorListener) {
        // we no longer do any code generation
        return outline.getCodeModel();
    }

    public List<JClass> getAllObjectFactories() {
        List<JClass> r = new ArrayList<JClass>();
        for (PackageOutline pkg : outline.getAllPackageContexts()) {
            r.add(pkg.objectFactory());
        }
        return r;
    }

    public final Mapping get(QName elementName) {
        return byXmlName.get(elementName);
    }

    public final Collection<? extends Mapping> getMappings() {
        return byXmlName.values();
    }

    public TypeAndAnnotation getJavaType(QName xmlTypeName) {
        // TODO: primitive type handling?
        TypeUse use = model.typeUses().get(xmlTypeName);
        if(use==null)   return null;

        return new TypeAndAnnotationImpl(outline,use);
    }

    public final List<String> getClassList() {
        List<String> classList = new ArrayList<String>();

        // list up root classes
        for( PackageOutline p : outline.getAllPackageContexts() )
            classList.add( p.objectFactory().fullName() );
        return classList;
    }
}
