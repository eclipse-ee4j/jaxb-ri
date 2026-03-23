/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.builder.relaxng;

import javax.xml.namespace.QName;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;
import com.sun.tools.txw2.model.Data;

/**
 * Builds {@link Data} from a XML Schema datatype.
 * @author Kohsuke Kawaguchi
 */
public class DatatypeFactory {
    private final JCodeModel codeModel;

    public DatatypeFactory(JCodeModel codeModel) {
        this.codeModel = codeModel;
    }

    /**
     * Decides the Java datatype from XML datatype.
     *
     * @return null
     *      if none is found.
     */
    public JType getType(String datatypeLibrary, String type) {
        if(datatypeLibrary.equals("http://www.w3.org/2001/XMLSchema-datatypes")
        || datatypeLibrary.equals("http://www.w3.org/2001/XMLSchema")) {
            type = type.intern();

            switch (type) {
                case "boolean" -> {
                    return codeModel.BOOLEAN;
                }
                case "int", "nonNegativeInteger", "positiveInteger" -> {
                    return codeModel.INT;
                }
                case "QName" -> {
                    return codeModel.ref(QName.class);
                }
                case "float" -> {
                    return codeModel.FLOAT;
                }
                case "double" -> {
                    return codeModel.DOUBLE;
                }
                case "anySimpleType", "anyType" -> {
                    return codeModel.ref(String.class);
                }
            }
        }

        return null;
    }
}
