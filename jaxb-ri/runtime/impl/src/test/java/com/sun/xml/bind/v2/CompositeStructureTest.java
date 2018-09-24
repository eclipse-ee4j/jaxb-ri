/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2;

import java.util.Arrays;
import java.io.StringWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import com.sun.xml.bind.api.Bridge;
import com.sun.xml.bind.api.CompositeStructure;
import com.sun.xml.bind.api.JAXBRIContext;
import com.sun.xml.bind.api.TypeReference;

import junit.framework.TestCase;

/**
 * @author Kohsuke Kawaguchi
 */
public class CompositeStructureTest extends TestCase {

    // this annotation is just so that we can pass it to tr4.
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    public void test1() throws Exception {
        TypeReference tr1 = new TypeReference(new QName("","foo"),String.class);
        TypeReference tr2 = new TypeReference(new QName("","bar"),int.class);
        TypeReference tr3 = new TypeReference(new QName("","zot"),byte[].class);
        TypeReference tr4 = new TypeReference(new QName("","zoo"),byte[].class,
                this.getClass().getMethod("test1").getAnnotation(XmlJavaTypeAdapter.class));
        JAXBRIContext c = JAXBRIContext.newInstance(new Class[0],
                Arrays.asList(tr1,tr2,tr3,tr4),"",false);

        CompositeStructure cs = new CompositeStructure();
        cs.bridges = new Bridge[] {
            c.createBridge(tr1),
            c.createBridge(tr2),
            c.createBridge(tr3),
            c.createBridge(tr4),
        };
        cs.values = new Object[] { "foo", 5, new byte[4], new byte[4] };

        JAXBElement<CompositeStructure> root = new JAXBElement<CompositeStructure>(
                new QName("", "root"), CompositeStructure.class, cs);

        StringWriter sw = new StringWriter();
        c.createMarshaller().marshal(root,System.out);
        c.createMarshaller().marshal(root,sw);
        assertTrue(sw.toString().contains(
            "<root><foo>foo</foo><bar>5</bar><zot>AAAAAA==</zot><zoo>00000000</zoo></root>"));
    }
}
