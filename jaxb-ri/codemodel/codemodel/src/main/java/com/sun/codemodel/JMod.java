/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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
    public final static int NONE         = 0x000;
    public final static int PUBLIC       = 0x001;
    public final static int PROTECTED    = 0x002;
    public final static int PRIVATE      = 0x004;
    public final static int FINAL        = 0x008;
    public final static int STATIC       = 0x010;
    public final static int ABSTRACT     = 0x020;
    public final static int NATIVE       = 0x040;
    public final static int SYNCHRONIZED = 0x080;
    public final static int TRANSIENT    = 0x100;
    public final static int VOLATILE     = 0x200;
}
