/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api.impl.s2j;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * {@link ErrorHandler} that reports all errors as warnings.
 *
 * @author Kohsuke Kawaguchi
 */
final class DowngradingErrorHandler implements ErrorHandler {
    private final ErrorHandler core;

    public DowngradingErrorHandler(ErrorHandler core) {
        this.core = core;
    }

    public void warning(SAXParseException exception) throws SAXException {
        core.warning(exception);
    }

    public void error(SAXParseException exception) throws SAXException {
        core.warning(exception);
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        core.warning(exception);
    }
}
