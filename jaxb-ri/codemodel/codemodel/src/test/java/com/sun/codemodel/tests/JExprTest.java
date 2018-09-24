/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.tests;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.tests.util.CodeModelTestsUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * JExpr tests.
 */
public class JExprTest extends TestCase {

	/**
	 * Tests double literal expression.
	 */
	public void testLitDouble() throws Exception {
		Assert.assertTrue(CodeModelTestsUtils.toString(
				JExpr.lit(Double.POSITIVE_INFINITY)).endsWith(
				"POSITIVE_INFINITY"));
		Assert.assertTrue(CodeModelTestsUtils.toString(
				JExpr.lit(Double.NEGATIVE_INFINITY)).endsWith(
				"NEGATIVE_INFINITY"));
		Assert.assertTrue(CodeModelTestsUtils.toString(JExpr.lit(Double.NaN))
				.endsWith("NaN"));

	}

	public void testLitFloat() throws Exception {
		Assert.assertTrue(CodeModelTestsUtils.toString(
				JExpr.lit(Float.POSITIVE_INFINITY)).endsWith(
				"POSITIVE_INFINITY"));
		Assert.assertTrue(CodeModelTestsUtils.toString(
				JExpr.lit(Float.NEGATIVE_INFINITY)).endsWith(
				"NEGATIVE_INFINITY"));
		Assert.assertTrue(CodeModelTestsUtils.toString(JExpr.lit(Float.NaN))
				.endsWith("NaN"));

	}

}
