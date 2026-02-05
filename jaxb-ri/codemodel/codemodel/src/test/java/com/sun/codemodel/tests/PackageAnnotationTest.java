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

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Inherited;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(0, cm.countArtifacts());
        assertNotNull(pkg.annotations());
        assertEquals(0, cm.countArtifacts());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cm.build(new SingleStreamCodeWriter(baos));
        assertTrue(baos.toString().trim().isEmpty());
    }
}
