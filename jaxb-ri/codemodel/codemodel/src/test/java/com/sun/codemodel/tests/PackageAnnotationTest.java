/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
import com.sun.codemodel.writer.SingleStreamCodeWriter;

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
}
