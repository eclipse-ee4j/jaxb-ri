/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.util;

import javax.xml.namespace.QName;

import org.glassfish.jaxb.core.v2.TODO;
import org.glassfish.jaxb.core.v2.WellKnownNamespace;

/**
 * Util class for ArrayInfo
 *
 * @author Iaroslav Savytskyi
 */
public class ArrayInfoUtil {

    private ArrayInfoUtil() {}

    /**
     * Computes the type name of the array from that of the item type.
     */
    public static QName calcArrayTypeName(QName n) {
        String uri;
        if(n.getNamespaceURI().equals(WellKnownNamespace.XML_SCHEMA)) {
            TODO.checkSpec("this URI");
            uri = "http://jaxb.dev.java.net/array";
        } else
            uri = n.getNamespaceURI();
        return new QName(uri,n.getLocalPart()+"Array");
    }
}
