/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.util;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
class Messages
{
    /** Loads a string resource and formats it with specified arguments. */
    static String format( String property, Object... args ) {
        String text = ResourceBundle.getBundle(Messages.class.getPackage().getName() +".MessageBundle").getString(property);
        return MessageFormat.format(text,args);
    }
    

    static final String ERR_CLASSNAME_COLLISION =
        "CodeModelClassFactory.ClassNameCollision";

    static final String ERR_CLASSNAME_COLLISION_SOURCE =
        "CodeModelClassFactory.ClassNameCollision.Source";

    static final String ERR_INVALID_CLASSNAME =
        "ERR_INVALID_CLASSNAME";

    static final String ERR_CASE_SENSITIVITY_COLLISION = // 2 args
        "CodeModelClassFactory.CaseSensitivityCollision";

    static final String ERR_CHAMELEON_SCHEMA_GONE_WILD = // no argts
        "ERR_CHAMELEON_SCHEMA_GONE_WILD";
}
