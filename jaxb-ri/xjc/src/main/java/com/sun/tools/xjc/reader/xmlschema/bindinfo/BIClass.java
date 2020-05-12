/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
import org.glassfish.jaxb.core.api.impl.NameConverter;
import com.sun.istack.Nullable;

/**
 * Class declaration.
 * 
 * This customization turns arbitrary schema component into a Java
 * content interface.
 * 
 * <p>
 * This customization is acknowledged by the ClassSelector.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
@XmlRootElement(name="class")
public final class BIClass extends AbstractDeclarationImpl {
    protected BIClass() {
    }

    @XmlAttribute(name="name")
    private String className;

    /**
     * Gets the specified class name, or null if not specified.
     * (Not a fully qualified name.)
     *
     * @return
     *      Returns a class name. The caller should <em>NOT</em>
     *      apply XML-to-Java name conversion to the name
     *      returned from this method.
     */
    public @Nullable String getClassName() {
        if( className==null )   return null;

        BIGlobalBinding gb = getBuilder().getGlobalBinding();
        NameConverter nc = getBuilder().model.getNameConverter();

        if(gb.isJavaNamingConventionEnabled()) return nc.toClassName(className);
        else
            // don't change it
            return className;
    }

    @XmlAttribute(name="implClass")
    private String userSpecifiedImplClass;

    /**
     * Gets the fully qualified name of the
     * user-specified implementation class, if any.
     * Or null.
     */
    public String getUserSpecifiedImplClass() {
        return userSpecifiedImplClass;
    }

    @XmlAttribute(name="ref")
    private String ref;

    @XmlAttribute(name="recursive", namespace=Const.XJC_EXTENSION_URI)
    private String recursive;

    /**
     * Reference to the existing class, or null.
     * Fully qualified name.
     *
     * <p>
     * Caller needs to perform error check on this.
     */
    public String getExistingClassRef() {
        return ref;
    }

    public String getRecursive() {
        return recursive;
    }

    @XmlElement
    private String javadoc;
    /**
     * Gets the javadoc comment specified in the customization.
     * Can be null if none is specified.
     */
    public String getJavadoc() { return javadoc; }

    public QName getName() { return NAME; }

    public void setParent(BindInfo p) {
        super.setParent(p);
        // if this specifies a reference to external class,
        // then it's OK even if noone actually refers this class.
        if(ref!=null)
            markAsAcknowledged();
    }

    /** Name of this declaration. */
    public static final QName NAME = new QName( Const.JAXB_NSURI, "class" );
}

