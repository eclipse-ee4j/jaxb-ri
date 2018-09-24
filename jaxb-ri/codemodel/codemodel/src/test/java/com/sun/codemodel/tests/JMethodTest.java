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

import org.junit.Assert;
import org.junit.Test;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

public class JMethodTest {

	@Test
	public void main() throws Exception {
		JCodeModel cm = new JCodeModel();
		JDefinedClass cls = cm._class("Test");
		JMethod m = cls.method(JMod.PUBLIC, cm.VOID, "foo");

		JVar foo = m.param(String.class, "foo");

		Assert.assertEquals(1, m.params().size());
		Assert.assertSame(foo, m.params().get(0));
	}
}
