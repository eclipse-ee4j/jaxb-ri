/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xmlpp;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.transform.sax.SAXResult;

/**
 * {@link javax.xml.transform.Result} that goes through pretty-printing.  
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class PrettyPrintResult extends SAXResult {
    public PrettyPrintResult(OutputStream out) {
        this(new OutputStreamWriter(out));
    }
    public PrettyPrintResult(Writer out) {
        XMLPrettyPrinter pp = new XMLPrettyPrinter(out);
        setHandler(pp);
        setLexicalHandler(pp);
    }
}
