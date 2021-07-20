/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.sun.codemodel.tests;

import java.io.IOException;
import java.lang.annotation.Inherited;

import org.junit.Test;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import java.io.ByteArrayOutputStream;
import org.junit.Assert;

/**
 * @author Kohsuke Kawaguchi
 */
public class PackageAnnotationTest {

    @Test
    public void main() throws IOException {
        JCodeModel cm = new JCodeModel();
        cm._package("foo").annotate(Inherited.class);
        cm.build(new SingleStreamCodeWriter(System.out));
    }

    @Test
    public void noEmptyPInfo() throws IOException {
        // https://github.com/eclipse-ee4j/jaxb-ri/issues/1039
        JCodeModel cm = new JCodeModel();
        JPackage pkg = cm._package("bar");
        Assert.assertEquals(0, cm.countArtifacts());
        Assert.assertNotNull(pkg.annotations());
        Assert.assertEquals(0, cm.countArtifacts());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cm.build(new SingleStreamCodeWriter(baos));
        Assert.assertTrue(baos.toString().trim().isEmpty());
    }
}
