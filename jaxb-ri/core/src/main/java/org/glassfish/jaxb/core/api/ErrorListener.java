/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.api;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Implemented by the driver of the compiler engine to handle
 * errors found during the compiliation.
 *
 * <p>
 * This class implements {@link ErrorHandler} so it can be
 * passed to anywhere where {@link ErrorHandler} is expected.
 *
 * <p>
 * However, to make the error handling easy (and make it work
 * with visitor patterns nicely), this interface is not allowed
 * to abort the processing. It merely receives errors.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * @since 2.1 EA2
 */
public interface ErrorListener extends ErrorHandler {
    @Override
    void error(SAXParseException exception);
    @Override
    void fatalError(SAXParseException exception);
    @Override
    void warning(SAXParseException exception);
    /**
     * Used to report possibly verbose information that
     * can be safely ignored.
     */
    void info(SAXParseException exception);
}
