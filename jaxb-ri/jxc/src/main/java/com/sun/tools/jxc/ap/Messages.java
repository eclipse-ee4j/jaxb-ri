/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.ap;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Message resources.
 *
 * @author Kohsuke Kawaguchi
 */
enum Messages {
    // Accessor
    NON_EXISTENT_FILE, // 1 arg
    UNRECOGNIZED_PARAMETER, //1 arg
    OPERAND_MISSING, // 1 arg
    ;

    private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getPackage().getName() +".MessageBundle");

    public String toString() {
        return format();
    }

    public String format( Object... args ) {
        return MessageFormat.format( rb.getString(name()), args );
    }
}
