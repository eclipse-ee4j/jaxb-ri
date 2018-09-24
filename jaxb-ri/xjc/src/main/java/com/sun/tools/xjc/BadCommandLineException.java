/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import com.sun.istack.Nullable;

/**
 * Signals a bad command line argument.
 */
public class BadCommandLineException extends Exception {
    private Options options;

    public BadCommandLineException(String msg) {
        super(msg);
    }

    public BadCommandLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadCommandLineException() {
        this(null);
    }

    public void initOptions(Options opt) {
        assert this.options==null;
        this.options = opt;
    }

    /**
     * Gets the partly parsed option object, if any.
     */
    public @Nullable Options getOptions() {
        return options;
    }
}
