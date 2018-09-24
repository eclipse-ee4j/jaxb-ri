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

import org.junit.Test;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.tests.util.CodeModelTestsUtils;

public class InnerClassTest {

	@Test
	public void innerClassesAreImported() throws JClassAlreadyExistsException {
		JCodeModel codeModel = new JCodeModel();
		JDefinedClass aClass = codeModel._class("org.test.DaTestClass");
//		JDefinedClass daInner = aClass._class("Inner");

//		Assert.assertEquals("org.test.DaTestClass.Inner", daInner.fullName());
//		Assert.assertEquals("org.test.DaTestClass$Inner", daInner.binaryName());
//		Assert.assertEquals("Inner", daInner.name());

//		aClass.method(JMod.PUBLIC, daInner, "getInner");
		final JDefinedClass otherClass = codeModel
				._class("org.test.OtherClass");
//		otherClass.method(JMod.PUBLIC, daInner, "getInner");
		otherClass.method(JMod.PUBLIC, aClass, "getOuter");
		System.out.println(CodeModelTestsUtils.declare(otherClass));

	}
}
