/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Kohsuke Kawaguchi
 */
final class Printer {
    private final PrintWriter out;
    private int indent=0;

    public Printer(PrintWriter out) {
        this.out = out;
    }

    public Printer(PrintStream out) {
        this.out = new PrintWriter(out);
    }

    public void in() {
        indent++;
    }

    public void out() {
        indent--;
    }

    public void print(String msg) {
        printIndent();
        out.println(msg);
        out.flush();
    }

    private void printIndent() {
        for( int i=0; i<indent; i++ )
            out.print("  ");
    }
}
