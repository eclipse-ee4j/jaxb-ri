/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd.bindinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CClassInfoParent;
import com.sun.tools.xjc.model.CEnumConstant;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.TypeUse;

import org.w3c.dom.Element;

/**
 * {@code <enumeration>} declaration in the binding file.
 */
public final class BIEnumeration implements BIConversion
{
    /** Creates an object from {@code <enumeration>} declaration. */
    private BIEnumeration( Element _e, TypeUse _xducer ) {
        this.e = _e;
        this.xducer = _xducer;
    }
    
    /** {@code <enumeration>} element in DOM. */
    private final Element e;
    
    private final TypeUse xducer;
    
    public String name() { return DOMUtil.getAttribute(e,"name"); }
    
    /** Returns a transducer for this enumeration declaration. */
    public TypeUse getTransducer() { return xducer; }
    
    
    
    
    /** Creates a global enumeration declaration. */
    static BIEnumeration create( Element dom, BindInfo parent ) {
        // create a class in the target package.
        return new BIEnumeration(
            dom,
            new CEnumLeafInfo(
                parent.model,
                null,
                new CClassInfoParent.Package(parent.getTargetPackage()),
                DOMUtil.getAttribute(dom,"name"),
                CBuiltinLeafInfo.STRING,
                buildMemberList(parent.model,dom),
                null, null/*TODO*/,
                DOMLocator.getLocationInfo(dom)));
    }
    
    /** Creates an element-local enumeration declaration. */
    static BIEnumeration create( Element dom, BIElement parent ) {
        // create a class as a nested class
        return new BIEnumeration(
            dom,
            new CEnumLeafInfo(
                parent.parent.model,
                null,
                parent.clazz,
                DOMUtil.getAttribute(dom,"name"),
                CBuiltinLeafInfo.STRING,
                buildMemberList(parent.parent.model,dom),
                null, null/*TODO*/,
                DOMLocator.getLocationInfo(dom) ));
    }
    
    private static List<CEnumConstant> buildMemberList( Model model, Element dom ) {
        List<CEnumConstant> r = new ArrayList<CEnumConstant>();

        String members = DOMUtil.getAttribute(dom,"members");
        if(members==null) members="";   // TODO: error handling
        
        StringTokenizer tokens = new StringTokenizer(members);
        while(tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            r.add(new CEnumConstant(model.getNameConverter().toConstantName(token),
                    null,token,null/*TODO*/,null,null));
        }
        
        return r;
    }
}
