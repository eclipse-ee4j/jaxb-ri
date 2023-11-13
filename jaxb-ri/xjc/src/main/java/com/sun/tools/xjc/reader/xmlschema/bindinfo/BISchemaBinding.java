/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import com.sun.tools.xjc.reader.Const;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSType;

/**
 * Schema-wide binding customization.
 * 
 * @author
 *  Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
@XmlRootElement(name="schemaBindings")
public final class BISchemaBinding extends AbstractDeclarationImpl {

    @XmlElement
    private NameRules nameXmlTransform = new NameRules();

    private static final class PackageInfo {
        @XmlAttribute
        String name;
        @XmlElement
        String javadoc;
    }

    @XmlElement(name="package")
    private PackageInfo packageInfo = new PackageInfo();

    /**
     * If false, it means not to generate any classes from this namespace.
     * No ObjectFactory, no classes (the only way to bind them is by using
     * {@code <jaxb:class ref="..."/>})
     */
    @XmlAttribute(name="map")
    public boolean map = true;

    /**
     * Default constructor.
     */
    public BISchemaBinding() {}

    /**
     * Transforms the default name produced from XML name
     * by following the customization.
     * 
     * This shouldn't be applied to a class name specified
     * by a customization.
     * 
     * @param cmp
     *      The schema component from which the default name is derived.
     */
    public String mangleClassName( String name, XSComponent cmp ) {
        if( cmp instanceof XSType )
            return nameXmlTransform.typeName.mangle(name);
        if( cmp instanceof XSElementDecl )
            return nameXmlTransform.elementName.mangle(name);
        if( cmp instanceof XSAttributeDecl )
            return nameXmlTransform.attributeName.mangle(name);
        if( cmp instanceof XSModelGroup || cmp instanceof XSModelGroupDecl )
            return nameXmlTransform.modelGroupName.mangle(name);
        
        // otherwise no modification
        return name;
    }
    
    public String mangleAnonymousTypeClassName( String name ) {
        return nameXmlTransform.anonymousTypeName.mangle(name);
    }
    
    
    public String getPackageName() { return packageInfo.name; }
    
    public String getJavadoc() { return packageInfo.javadoc; }
    
    @Override
    public QName getName() { return NAME; }
    public static final QName NAME = new QName(
        Const.JAXB_NSURI, "schemaBinding" );
}
