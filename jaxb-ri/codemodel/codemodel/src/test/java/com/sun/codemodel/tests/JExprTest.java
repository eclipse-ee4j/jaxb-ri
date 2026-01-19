/*
 * Copyright (c) 2010, 2021 Oracle and/or its affiliates. All rights reserved.
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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JExpr tests.
 */
public class JExprTest {

	/**
	 * Tests double literal expression.
	 */
	@Test
	public void testLitDouble() throws Exception {
		assertTrue(CodeModelTestsUtils.toString(
				JExpr.lit(Double.POSITIVE_INFINITY)).endsWith(
				"POSITIVE_INFINITY"));
		assertTrue(CodeModelTestsUtils.toString(
				JExpr.lit(Double.NEGATIVE_INFINITY)).endsWith(
				"NEGATIVE_INFINITY"));
		assertTrue(CodeModelTestsUtils.toString(JExpr.lit(Double.NaN))
				.endsWith("NaN"));

	}

	@Test
	public void testLitFloat() throws Exception {
		assertTrue(CodeModelTestsUtils.toString(
				JExpr.lit(Float.POSITIVE_INFINITY)).endsWith(
				"POSITIVE_INFINITY"));
		assertTrue(CodeModelTestsUtils.toString(
				JExpr.lit(Float.NEGATIVE_INFINITY)).endsWith(
				"NEGATIVE_INFINITY"));
		assertTrue(CodeModelTestsUtils.toString(JExpr.lit(Float.NaN))
				.endsWith("NaN"));

	}

}
