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
@XmlElement("complexType")
public interface ComplexType
    extends Annotated, ComplexTypeModel, TypedXmlWriter
{


    @XmlAttribute("final")
    public ComplexType _final(String[] value);

    @XmlAttribute("final")
    public ComplexType _final(String value);

    @XmlAttribute
    public ComplexType block(String[] value);

    @XmlAttribute
    public ComplexType block(String value);

    @XmlAttribute("abstract")
    public ComplexType _abstract(boolean value);

    @XmlAttribute
    public ComplexType name(String value);

}
