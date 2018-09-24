/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
enum Messages {
    // AnnotationParser
    METHOD_COLLISION, // 3 args
    ERR_UNUSABLE_NAME, // 2 args
    ERR_KEYNAME_COLLISION, // 1 arg
    ERR_NAME_COLLISION, // 1 arg
    ILLEGAL_CONSTRUCTOR_PARAM, // 1 arg
    OBJECT_FACTORY_CONFLICT,    // 1 arg
    OBJECT_FACTORY_CONFLICT_RELATED,
    ;

    private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle");

    public String toString() {
        return format();
    }

    public String format( Object... args ) {
        return MessageFormat.format( rb.getString(name()), args );
    }
}
