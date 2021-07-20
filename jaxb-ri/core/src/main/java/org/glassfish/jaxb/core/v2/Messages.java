/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
public enum Messages {
    ILLEGAL_ENTRY,          // 1 arg
    ERROR_LOADING_CLASS,    // 2 args
    INVALID_PROPERTY_VALUE, // 2 args
    UNSUPPORTED_PROPERTY,   // 1 arg
    BROKEN_CONTEXTPATH,     // 1 arg
    NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS, // 1 arg
    INVALID_TYPE_IN_MAP, // 0args
    INVALID_JAXP_IMPLEMENTATION, // 1 arg
    JAXP_SUPPORTED_PROPERTY, // 1 arg
    JAXP_UNSUPPORTED_PROPERTY, // 1 arg
    JAXP_XML_SECURITY_DISABLED, // no arg
    JAXP_EXTERNAL_ACCESS_CONFIGURED, // no arg
    ;

    private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

    public String toString() {
        return format();
    }

    public String format( Object... args ) {
        return MessageFormat.format( rb.getString(name()), args );
    }
}
