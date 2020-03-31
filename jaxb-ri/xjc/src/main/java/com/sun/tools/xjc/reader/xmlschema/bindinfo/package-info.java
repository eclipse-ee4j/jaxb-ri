/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Object Model that represents customization declarations.
 * <a href="http://relaxngcc.sourceforge.net/">RelaxNGCC</a> is used to parse
 * XML syntax into this representation, and the other parts of XJC will use
 * this object model.
 */
@XmlSchema(elementFormDefault = QUALIFIED, namespace=Const.JAXB_NSURI)
package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.annotation.XmlSchema;

import com.sun.tools.xjc.reader.Const;

import static jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED;
