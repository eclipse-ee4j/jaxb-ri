/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2;

/**
 * Signals an incorrect use of TXW annotations.
 *
 * @author Kohsuke Kawaguchi
 */
public class IllegalAnnotationException extends TxwException {
    public IllegalAnnotationException(String message) {
        super(message);
    }

    public IllegalAnnotationException(Throwable cause) {
        super(cause);
    }

    public IllegalAnnotationException(String message, Throwable cause) {
        super(message, cause);
    }

    private static final long serialVersionUID = 1L;
}
