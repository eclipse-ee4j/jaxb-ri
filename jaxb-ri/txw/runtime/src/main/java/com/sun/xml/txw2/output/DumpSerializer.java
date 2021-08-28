/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2.output;

import java.io.PrintStream;

/**
 * Shows the call sequence of {@link XmlSerializer} methods.
 *
 * Useful for debugging and learning how TXW works.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class DumpSerializer implements XmlSerializer {
    private final PrintStream out;

    public DumpSerializer(PrintStream out) {
        this.out = out;
    }

    @Override
    public void beginStartTag(String uri, String localName, String prefix) {
        out.println('<'+prefix+':'+localName);
    }

    @Override
    public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
        out.println('@'+prefix+':'+localName+'='+value);
    }

    @Override
    public void writeXmlns(String prefix, String uri) {
        out.println("xmlns:"+prefix+'='+uri);
    }

    @Override
    public void endStartTag(String uri, String localName, String prefix) {
        out.println('>');
    }

    @Override
    public void endTag() {
        out.println("</  >");
    }

    @Override
    public void text(StringBuilder text) {
        out.println(text);
    }

    @Override
    public void cdata(StringBuilder text) {
        out.println("<![CDATA[");
        out.println(text);
        out.println("]]>");
    }

    @Override
    public void comment(StringBuilder comment) {
        out.println("<!--");
        out.println(comment);
        out.println("-->");
    }

    @Override
    public void startDocument() {
        out.println("<?xml?>");
    }

    @Override
    public void endDocument() {
        out.println("done");
    }

    @Override
    public void flush() {
        out.println("flush");
    }
}
