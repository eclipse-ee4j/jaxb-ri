/*
 * Copyright (c) 2022, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Modifier;

import org.junit.Test;

public class JModsTest {
	static final int ALL_JMODS = JMod.PUBLIC | JMod.PROTECTED | JMod.PRIVATE
			| JMod.ABSTRACT | JMod.STATIC | JMod.FINAL
			| JMod.TRANSIENT | JMod.VOLATILE
			| JMod.SYNCHRONIZED | JMod.NATIVE;

	static final int ALL_REFLECT_MODS = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE
			| Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL
			| Modifier.TRANSIENT | Modifier.VOLATILE
			| Modifier.SYNCHRONIZED | Modifier.NATIVE;

	public JModsTest() {
	}

	@Test
	public void testToString() {
		assertArrayEquals(
				Modifier.toString(ALL_REFLECT_MODS).split("\\s+"),
				new JMods(ALL_JMODS).toString().split("\\s+"));
	}
}
