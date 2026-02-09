/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.tests;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.writer.OutputStreamCodeWriter;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class JMethodTest extends TestCase {

	@Test
	public void testMethod() throws Exception {
		JCodeModel cm = new JCodeModel();
		JDefinedClass cls = cm._class("Test");
		JMethod m = cls.method(JMod.PUBLIC, cm.VOID, "foo");

		JVar foo = m.param(String.class, "foo");

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStreamCodeWriter fileCodeWriter = new OutputStreamCodeWriter(os, "UTF-8");
		cm.build(fileCodeWriter);

		String generatedClass = os.toString(StandardCharsets.UTF_8);
		System.out.println(generatedClass);

		Assert.assertEquals(1, m.params().size());
		Assert.assertSame(foo, m.params().get(0));

		assertTrue(generatedClass.contains("public void foo(String foo)"));
	}

	public void testDefaultMethod() throws Throwable {
		JCodeModel cm = new JCodeModel();
		String className = "gh1706.InterfaceTest";
		JDefinedClass cls = cm._class(className, ClassType.INTERFACE);
		JMethod m = cls.method(JMod.DEFAULT, cm._ref(String.class), "defaultMethod");
		JVar foo = m.param(String.class, "foo");
		m.body()._return(JExpr.lit("Hello ").plus(foo));

		JMethod m2 = cls.method(JMod.NONE, cm._ref(String.class), "nonDefaultMethod");
		m2.param(String.class, "foo");

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStreamCodeWriter fileCodeWriter = new OutputStreamCodeWriter(os, "UTF-8");
		cm.build(fileCodeWriter);

		String generatedClass = os.toString(StandardCharsets.UTF_8);
		System.out.println(generatedClass);

		assertTrue(generatedClass.contains("default String defaultMethod(String foo)"));
		assertTrue(generatedClass.contains("return (\"Hello \"+ foo);"));

		assertTrue(generatedClass.contains("String nonDefaultMethod(String foo);"));
	}
}
