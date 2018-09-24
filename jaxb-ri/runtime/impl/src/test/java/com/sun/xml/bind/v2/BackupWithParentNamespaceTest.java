/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.v2.runtime.unmarshaller.StructureLoader;

import junit.framework.TestCase;

public class BackupWithParentNamespaceTest extends TestCase {

    @XmlRootElement(name = "root", namespace = "http://example.org")
    static class Root {
        @XmlElement(namespace = "http://nested.example.org")
        Nested foo;
    }

    @XmlType(namespace = "http://example.org")
    static class Nested {
        @XmlElement(namespace = "http://example.org")
        String bar;    
    }

    // bug#25092248/21667799/JAXB-867: lookup loader by parent namespace also
    // root = example.org namespace
    // foo = nested.example.org namespace
    // bar = example.org namepace with no namespace specified, example.org namespace should be used, instead of nested.example.org
    // by SPEC unmarshaller should fail, but due to JAXB-867 there were few releases (from 2.2.5 to 2.3 (not including)
    // that handled it gracefully, so some clients rely on this behavior and need support for this further on
    // this is fullfilled with com.sun.xml.bind.v2.runtime.unmarshaller.BackupWithParentNamespace system property
    public void test1() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(JAXBRIContext.BACKUP_WITH_PARENT_NAMESPACE, Boolean.TRUE);
        JAXBContext c = JAXBContext.newInstance(new Class[] {Root.class}, properties);

        Root root = (Root) c.createUnmarshaller().unmarshal(new StringReader("<root xmlns='http://example.org'><foo xmlns='http://nested.example.org'><bar>bar</bar></foo></root>"));
        assertNotNull("root", root);
        Nested foo = root.foo;
        assertNotNull("foo", foo);
        assertEquals("bar", foo.bar);
    }
}
