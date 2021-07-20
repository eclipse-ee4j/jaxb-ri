/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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

import com.sun.tools.xjc.model.CClassInfo;

import org.w3c.dom.Element;
import org.xml.sax.Locator;

/**
 * {@code <constructor>} declaration in the binding file.
 * 
 * <p>
 * Since JAXB will generate both interfaces and implementations,
 * A constructor declaration will create:
 * 
 * <ul>
 *  <li> a method declaration in the factory interface
 *  <li> a method implementation in the factory implementation class
 *  <li> a constructor implementation in the actual implementation class
 * </ul>
 */
public class BIConstructor
{
    BIConstructor( Element _node ) {
        this.dom = _node;
        
        StringTokenizer tokens = new StringTokenizer(
            DOMUtil.getAttribute(_node,"properties"));
        
        List<String> vec = new ArrayList<String>();
        while(tokens.hasMoreTokens())
            vec.add(tokens.nextToken());
        properties = vec.toArray(new String[0]);
        
        if( properties.length==0 )
            throw new AssertionError("this error should be catched by the validator");
    }
    
    /** {@code <constructor>} element in the source binding file. */
    private final Element dom;
    
    /** properties specified by @properties. */
    private final String[] properties;
    
    /**
     * Creates a constructor declaration into the ClassItem.
     * 
     * @param   cls
     *      ClassItem object that corresponds to the
     *      element declaration that contains this declaration.
     */
    public void createDeclaration( CClassInfo cls ) {
        cls.addConstructor(properties);
    }

    /** Gets the location where this declaration is declared. */
    public Locator getSourceLocation() {
        return DOMLocator.getLocationInfo(dom);
    }
    

}
