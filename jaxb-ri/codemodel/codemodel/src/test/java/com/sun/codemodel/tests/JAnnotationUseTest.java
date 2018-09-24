/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.tests;

import org.junit.Assert;
import org.junit.Test;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.tests.util.CodeModelTestsUtils;

public class JAnnotationUseTest {

	@Test
	public void generatesGenericParam() throws JClassAlreadyExistsException {
		
		final JCodeModel codeModel = new JCodeModel();
		final JDefinedClass testClass = codeModel._class("Test");
		final JAnnotationUse suppressWarningAnnotation = testClass.annotate(SuppressWarnings.class);
		suppressWarningAnnotation.param("value", JExpr.lit("unused"));
		
		Assert.assertEquals("@java.lang.SuppressWarnings(\"unused\")", CodeModelTestsUtils.generate(suppressWarningAnnotation));

	}

}
