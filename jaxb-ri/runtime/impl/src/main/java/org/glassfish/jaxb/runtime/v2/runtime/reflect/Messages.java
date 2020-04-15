/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Message resources
 */
enum Messages {
    // Accessor
    UNABLE_TO_ACCESS_NON_PUBLIC_FIELD,  // 2 args
    UNASSIGNABLE_TYPE, // 2 args
    NO_SETTER, // 1 arg
    NO_GETTER, // 1 arg
    ;

    private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

    public String toString() {
        return format();
    }

    public String format( Object... args ) {
        return MessageFormat.format( rb.getString(name()), args );
    }
}
