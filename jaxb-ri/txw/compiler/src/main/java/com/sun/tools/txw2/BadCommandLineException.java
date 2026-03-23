/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2;

import java.io.Serial;

/**
 * Signals a bad command line argument.
 *
 * @author lukas
 */
public class BadCommandLineException extends Exception {

    @Serial
    private static final long serialVersionUID = -689101368298669934L;

    public BadCommandLineException(String msg) {
        super(msg);
    }

    public BadCommandLineException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadCommandLineException() {
        this(null);
    }

}
