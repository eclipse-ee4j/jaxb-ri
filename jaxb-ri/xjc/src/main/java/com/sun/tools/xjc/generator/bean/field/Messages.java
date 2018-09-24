/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean.field;


import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Message resources
 */
public enum Messages {
    DEFAULT_GETTER_JAVADOC,     // 1 arg
    DEFAULT_SETTER_JAVADOC,     // 1 arg
    ;

    private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName().substring(0, Messages.class.getName().lastIndexOf('.'))+ ".MessageBundle");

    public String toString() {
        return format();
    }

    public String format( Object... args ) {
        return MessageFormat.format( rb.getString(name()), args );
    }
}
