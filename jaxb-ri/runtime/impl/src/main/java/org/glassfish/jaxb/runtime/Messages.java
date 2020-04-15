/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Message resources
 */
enum Messages {
    FAILED_TO_INITIALE_DATATYPE_FACTORY, // 0 args
    ;

    private static final ResourceBundle rb = ResourceBundle.getBundle(Messages.class.getName());

    @Override
    public String toString() {
        return format();
    }

    public String format( Object... args ) {
        return MessageFormat.format( rb.getString(name()), args );
    }
}
