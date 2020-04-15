/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Maps an error to a fatal error.
 * 
 * @author Kohsuke Kawaguchi
 */
public class FatalAdapter implements ErrorHandler {
    private final ErrorHandler core;

    public FatalAdapter(ErrorHandler handler) {
        this.core = handler;
    }

    public void warning (SAXParseException exception) throws SAXException {
        core.warning(exception);
    }

    public void error (SAXParseException exception) throws SAXException {
        core.fatalError(exception);
    }

    public void fatalError (SAXParseException exception) throws SAXException {
        core.fatalError(exception);
    }
}
