/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;


/**
 * Modifier constants.
 */
public final class JMod {

    private JMod() {
    }

    public static final int NONE         = 0x000;
    public static final int PUBLIC       = 0x001;
    public static final int PROTECTED    = 0x002;
    public static final int PRIVATE      = 0x004;
    public static final int FINAL        = 0x008;
    public static final int STATIC       = 0x010;
    public static final int ABSTRACT     = 0x020;
    public static final int NATIVE       = 0x040;
    public static final int SYNCHRONIZED = 0x080;
    public static final int TRANSIENT    = 0x100;
    public static final int VOLATILE     = 0x200;
    public static final int DEFAULT      = 0x400;
}
