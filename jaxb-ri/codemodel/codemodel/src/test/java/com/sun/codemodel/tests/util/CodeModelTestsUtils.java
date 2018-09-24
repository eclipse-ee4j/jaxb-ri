/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.tests.util;

import java.io.StringWriter;

import com.sun.codemodel.JDeclaration;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JGenerable;

/**
 * Various utilities for codemodel tests.
 *
 * @author Aleksei Valikov
 */
public class CodeModelTestsUtils {

    /**
     * Hidden constructor.
     */
    private CodeModelTestsUtils() {
    }

    /**
     * Prints an expression into a string.
     *
     * @param expression expression to print into a string.
     * @return Expression formatted as a string.
     */
    public static String toString(JExpression expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Generable must not be null.");
        }
        final StringWriter stringWriter = new StringWriter();
        final JFormatter formatter = new JFormatter(stringWriter);
        expression.generate(formatter);
        return stringWriter.toString();
    }

    public static String declare(JDeclaration declaration) {
        if (declaration == null) {
            throw new IllegalArgumentException("Declaration must not be null.");
        }
        final StringWriter stringWriter = new StringWriter();
        final JFormatter formatter = new JFormatter(stringWriter);
        declaration.declare(formatter);
        return stringWriter.toString();
    }

    public static String generate(JGenerable generable) {
        if (generable == null) {
            throw new IllegalArgumentException("Generable must not be null.");
        }
        final StringWriter stringWriter = new StringWriter();
        final JFormatter formatter = new JFormatter(stringWriter);
        generable.generate(formatter);
        return stringWriter.toString();
    }
}
