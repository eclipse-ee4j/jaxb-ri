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

import java.util.ArrayList;

import org.junit.Test;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * 
 * Simple program to test the generation of the enhanced for loop in jdk 1.5
 * 
 * @author Bhakti Mehta Bhakti.Mehta@sun.com
 * 
 */

public class ForEachTest {

	@Test
	public void main() throws Exception {

		JCodeModel cm = new JCodeModel();
		JDefinedClass cls = cm._class("Test");

		JMethod m = cls.method(JMod.PUBLIC, cm.VOID, "foo");
		m.body().decl(cm.INT, "getCount");

		// This is not exactly right because we need to
		// support generics
		JClass arrayListclass = cm.ref(ArrayList.class);
		JVar $list = m.body().decl(arrayListclass, "alist",
				JExpr._new(arrayListclass));

		JClass $integerclass = cm.ref(Integer.class);
		JForEach foreach = m.body().forEach($integerclass, "count", $list);
		JVar $count1 = foreach.var();
		foreach.body().assign(JExpr.ref("getCount"), JExpr.lit(10));

		// printing out the variable
		JFieldRef out1 = cm.ref(System.class).staticRef("out");
		// JInvocation invocation =
		foreach.body().invoke(out1, "println").arg($count1);

		cm.build(new SingleStreamCodeWriter(System.out));
	}
}
