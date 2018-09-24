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

import org.xml.sax.SAXParseException;

import java.io.PrintStream;
import java.text.MessageFormat;

/**
 * Prints the error to a stream.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ConsoleErrorReporter implements ErrorListener {
    private final PrintStream out;

    public ConsoleErrorReporter(PrintStream out) {
        this.out = out;
    }

    public void error(SAXParseException exception) {
        out.print("[ERROR]   ");
        print(exception);
    }

    public void fatalError(SAXParseException exception) {
        out.print("[FATAL]   ");
        print(exception);
    }

    public void warning(SAXParseException exception) {
        out.print("[WARNING] ");
        print(exception);
    }

    private void print(SAXParseException e) {
        out.println(e.getMessage());
        out.println(MessageFormat.format("  {0}:{1} of {2}",
            new Object[]{
                String.valueOf(e.getLineNumber()),
                String.valueOf(e.getColumnNumber()),
                e.getSystemId()}));
    }


}
