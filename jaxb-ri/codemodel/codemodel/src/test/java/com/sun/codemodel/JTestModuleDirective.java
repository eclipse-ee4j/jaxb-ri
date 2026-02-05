/*
 * Copyright (c) 2016, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * Common Java module directive testing code.
 * @author Tomas Kraus
 */
public abstract class JTestModuleDirective {

    /**
     * Default constructor.
     */
    public JTestModuleDirective() {}

    /** Character array writer used to verify code generation output. */
    protected CharArrayWriter out;

    /** Java formatter used to generate code output. */
    protected JFormatter jf;

    /**
     * Initialize test.
     */
    @BeforeEach
    public void setUp() {
        openOutput();
    }

    /**
     * Cleanup test.
     */
    @AfterEach
    public void tearDown() {
        closeOutput();
    }

    /**
     * Open Java formatting output for this test.
     */
    protected void openOutput() {
        out = new CharArrayWriter();
        jf = new JFormatter(new PrintWriter(out));
    }

    /**
     * Close Java formatting output for this test.
     */
    protected void closeOutput() {
        jf.close();
        out.close();
    }

    /**
     * Reopen Java formatting output for this test.
     * Used to reset output content during tests.
     */
    protected void reopenOutput() {
        closeOutput();
        openOutput();
    }

}
