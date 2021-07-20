/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
import jakarta.xml.bind.annotation.XmlType;
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

    /**
     * Name conversion rules. All defaults to {@link BISchemaBinding#defaultNamingRule}.
     */
    @XmlType(propOrder={})
    private static final class NameRules {
        @XmlElement
        NamingRule typeName = defaultNamingRule;
        @XmlElement
        NamingRule elementName = defaultNamingRule;
        @XmlElement
        NamingRule attributeName = defaultNamingRule;
        @XmlElement
        NamingRule modelGroupName = defaultNamingRule;
        @XmlElement
        NamingRule anonymousTypeName = defaultNamingRule;
    }

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
     * Default naming rule, that doesn't change the name.
     */
    private static final NamingRule defaultNamingRule = new NamingRule("","");
    

    /**
     * Default naming rules of the generated interfaces.
     * 
     * It simply adds prefix and suffix to the name, but
     * the caller shouldn't care how the name mangling is
     * done.
     */
    public static final class NamingRule {
        @XmlAttribute
        private String prefix = "";
        @XmlAttribute
        private String suffix = "";
        
        public NamingRule( String _prefix, String _suffix ) {
            this.prefix = _prefix;
            this.suffix = _suffix;
        }

        public NamingRule() {
        }

        /** Changes the name according to the rule. */
        public String mangle( String originalName ) {
            return prefix+originalName+suffix;
        }
    }
    
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
    
    public QName getName() { return NAME; }
    public static final QName NAME = new QName(
        Const.JAXB_NSURI, "schemaBinding" );
}
