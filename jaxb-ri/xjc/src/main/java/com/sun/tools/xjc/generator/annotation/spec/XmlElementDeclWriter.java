/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.annotation.spec;

import jakarta.xml.bind.annotation.XmlElementDecl;
import com.sun.codemodel.JAnnotationWriter;
import com.sun.codemodel.JType;

/**
 * <p><b>
 *     Auto-generated, do not edit.
 * </b></p>
 */
public interface XmlElementDeclWriter
    extends JAnnotationWriter<XmlElementDecl>
{


    XmlElementDeclWriter name(String value);

    XmlElementDeclWriter scope(Class value);

    XmlElementDeclWriter scope(JType value);

    XmlElementDeclWriter namespace(String value);

    XmlElementDeclWriter defaultValue(String value);

    XmlElementDeclWriter substitutionHeadNamespace(String value);

    XmlElementDeclWriter substitutionHeadName(String value);

}
