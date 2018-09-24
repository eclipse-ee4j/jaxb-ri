/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Used internally to report errors.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface ErrorListener extends ErrorHandler {
    abstract void error (SAXParseException exception);
    abstract void fatalError (SAXParseException exception);
    abstract void warning (SAXParseException exception);

}
