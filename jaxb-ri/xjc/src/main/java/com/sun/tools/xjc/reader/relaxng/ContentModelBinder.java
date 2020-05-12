/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.relaxng;

import javax.xml.namespace.QName;

import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.Multiplicity;
import com.sun.tools.xjc.reader.RawTypeSet;
import org.glassfish.jaxb.core.v2.model.core.ID;

import com.sun.tools.rngom.digested.DAttributePattern;
import com.sun.tools.rngom.digested.DChoicePattern;
import com.sun.tools.rngom.digested.DMixedPattern;
import com.sun.tools.rngom.digested.DOneOrMorePattern;
import com.sun.tools.rngom.digested.DOptionalPattern;
import com.sun.tools.rngom.digested.DPattern;
import com.sun.tools.rngom.digested.DPatternWalker;
import com.sun.tools.rngom.digested.DZeroOrMorePattern;

import static com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode.REPEATED_ELEMENT;

/**
 * Recursively visits {@link DPattern} and
 * decides which patterns to map to properties.
 *
 * @author Kohsuke Kawaguchi
 */
final class ContentModelBinder extends DPatternWalker {
    private final RELAXNGCompiler compiler;
    private final CClassInfo clazz;

    private boolean insideOptional = false;
    private int iota=1;

    public ContentModelBinder(RELAXNGCompiler compiler,CClassInfo clazz) {
        this.compiler = compiler;
        this.clazz = clazz;
    }

    public Void onMixed(DMixedPattern p) {
        throw new UnsupportedOperationException();
    }

    public Void onChoice(DChoicePattern p) {
        boolean old = insideOptional;
        insideOptional = true;
        super.onChoice(p);
        insideOptional = old;
        return null;
    }

    public Void onOptional(DOptionalPattern p) {
        boolean old = insideOptional;
        insideOptional = true;
        super.onOptional(p);
        insideOptional = old;
        return null;
    }

    public Void onZeroOrMore(DZeroOrMorePattern p) {
        return onRepeated(p,true);
    }

    public Void onOneOrMore(DOneOrMorePattern p) {
        return onRepeated(p,insideOptional);

    }

    private Void onRepeated(DPattern p,boolean optional) {
        RawTypeSet rts = RawTypeSetBuilder.build(compiler, p, optional? Multiplicity.STAR : Multiplicity.PLUS);
        if(rts.canBeTypeRefs==RawTypeSet.Mode.SHOULD_BE_TYPEREF) {
            CElementPropertyInfo prop = new CElementPropertyInfo(
                    calcName(p),REPEATED_ELEMENT,ID.NONE,null,null,null,p.getLocation(),!optional);
            rts.addTo(prop);
            clazz.addProperty(prop);
        } else {
            CReferencePropertyInfo prop = new CReferencePropertyInfo(
                    calcName(p),true,!optional,false/*TODO*/,null,null,p.getLocation(), false, false, false);
            rts.addTo(prop);
            clazz.addProperty(prop);
        }

        return null;
    }

    public Void onAttribute(DAttributePattern p) {
        // TODO: support multiple names
        QName name = p.getName().listNames().iterator().next();

        CAttributePropertyInfo ap = new CAttributePropertyInfo(
           calcName(p), null,null/*TODO*/, p.getLocation(), name,
                p.getChild().accept(compiler.typeUseBinder), null,
                !insideOptional);
        clazz.addProperty(ap);

        return null;
    }

    private String calcName(DPattern p) {
        // TODO
        return "field"+(iota++);
    }
}
