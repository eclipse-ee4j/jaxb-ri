/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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
    Annotation annotation();

    @XmlElement("import")
    Import _import();

    @XmlAttribute
    Schema targetNamespace(String value);

    @XmlAttribute(ns = "http://www.w3.org/XML/1998/namespace")
    Schema lang(String value);

    @XmlAttribute
    Schema id(String value);

    @XmlAttribute
    Schema elementFormDefault(String value);

    @XmlAttribute
    Schema attributeFormDefault(String value);

    @XmlAttribute
    Schema blockDefault(String[] value);

    @XmlAttribute
    Schema blockDefault(String value);

    @XmlAttribute
    Schema finalDefault(String[] value);

    @XmlAttribute
    Schema finalDefault(String value);

    @XmlAttribute
    Schema version(String value);

}
