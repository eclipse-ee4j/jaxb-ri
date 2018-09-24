/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.ap;

import com.sun.tools.xjc.ErrorReceiver;
import org.xml.sax.SAXParseException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

/**
 * @author Kohsuke Kawaguchi
 */
final class ErrorReceiverImpl extends ErrorReceiver {
    private final Messager messager;
    private final boolean debug;

    public ErrorReceiverImpl(Messager messager, boolean debug) {
        this.messager = messager;
        this.debug = debug;
    }

    public ErrorReceiverImpl(Messager messager) {
        this(messager,false);
    }

    public ErrorReceiverImpl(ProcessingEnvironment env) {
        this(env.getMessager());
    }

    public void error(SAXParseException exception) {
        messager.printMessage(Diagnostic.Kind.ERROR, exception.getMessage());
        messager.printMessage(Diagnostic.Kind.ERROR, getLocation(exception));
        printDetail(exception);
    }

    public void fatalError(SAXParseException exception) {
        messager.printMessage(Diagnostic.Kind.ERROR, exception.getMessage());
        messager.printMessage(Diagnostic.Kind.ERROR, getLocation(exception));
        printDetail(exception);
    }

    public void warning(SAXParseException exception) {
        messager.printMessage(Diagnostic.Kind.WARNING, exception.getMessage());
        messager.printMessage(Diagnostic.Kind.WARNING, getLocation(exception));
        printDetail(exception);
    }

    public void info(SAXParseException exception) {
        printDetail(exception);
    }

    private String getLocation(SAXParseException e) {
        // TODO: format the location information for printing
        return "";
    }

    private void printDetail(SAXParseException e) {
        if(debug) {
            e.printStackTrace(System.out);
        }
    }
}
