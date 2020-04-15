/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlAttribute;
import com.sun.xml.txw2.annotation.XmlElement;

/**
 * <p><b>
 *     Auto-generated, do not edit.
 * </b></p>
 */
@XmlElement("schema")
public interface Schema
    extends SchemaTop, TypedXmlWriter
{


    @XmlElement
    public Annotation annotation();

    @XmlElement("import")
    public Import _import();

    @XmlAttribute
    public Schema targetNamespace(String value);

    @XmlAttribute(ns = "http://www.w3.org/XML/1998/namespace")
    public Schema lang(String value);

    @XmlAttribute
    public Schema id(String value);

    @XmlAttribute
    public Schema elementFormDefault(String value);

    @XmlAttribute
    public Schema attributeFormDefault(String value);

    @XmlAttribute
    public Schema blockDefault(String[] value);

    @XmlAttribute
    public Schema blockDefault(String value);

    @XmlAttribute
    public Schema finalDefault(String[] value);

    @XmlAttribute
    public Schema finalDefault(String value);

    @XmlAttribute
    public Schema version(String value);

}
