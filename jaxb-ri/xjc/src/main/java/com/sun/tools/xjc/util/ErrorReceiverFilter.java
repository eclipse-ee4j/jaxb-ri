/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.util;

import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.api.ErrorListener;

import org.xml.sax.SAXParseException;

/**
 * Filter implementation of the ErrorReceiver.
 * 
 * If an error is encountered, this filter sets a flag.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ErrorReceiverFilter extends ErrorReceiver {

    public ErrorReceiverFilter() {}

    public ErrorReceiverFilter( ErrorListener h ) {
        setErrorReceiver(h);
    }

    private ErrorListener core;
    public void setErrorReceiver( ErrorListener handler ) {
        core = handler;
    }

    private boolean hadError = false;
    public final boolean hadError() { return hadError; }

    public void info(SAXParseException exception) {
        if(core!=null)  core.info(exception);
    }

    public void warning(SAXParseException exception) {
        if(core!=null)  core.warning(exception);
    }

    public void error(SAXParseException exception) {
        hadError = true;
        if(core!=null)  core.error(exception);
    }

    public void fatalError(SAXParseException exception) {
        hadError = true;
        if(core!=null)  core.fatalError(exception);
    }

}
