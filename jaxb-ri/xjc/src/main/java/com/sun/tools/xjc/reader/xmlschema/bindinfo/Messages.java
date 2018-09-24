/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
enum Messages
{
    ERR_CANNOT_BE_BOUND_TO_SIMPLETYPE,
    ERR_UNDEFINED_SIMPLE_TYPE,
    ERR_ILLEGAL_FIXEDATTR
    ;

    /** Loads a string resource and formats it with specified arguments. */
    String format( Object... args ) {
        String text = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle").getString(name());
        return MessageFormat.format(text,args);
    }
}
