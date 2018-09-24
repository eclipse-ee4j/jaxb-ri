/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * ErrorHandler that reports error to the specified output stream.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke,kawaguchi@sun.com)
 */
public class ErrorReporter implements ErrorHandler {
    
    private final PrintStream out;
    
    public ErrorReporter( PrintStream o ) { this.out = o; }
    public ErrorReporter( OutputStream o ) { this(new PrintStream(o)); }
    
    public void warning(SAXParseException e) throws SAXException {
        print("[Warning]",e);
    }

    public void error(SAXParseException e) throws SAXException {
        print("[Error  ]",e);
    }

    public void fatalError(SAXParseException e) throws SAXException {
        print("[Fatal  ]",e);
    }

    private void print( String header, SAXParseException e ) {
        out.println(header+' '+e.getMessage());
        out.println(MessageFormat.format("   line {0} at {1}",
            new Object[]{
                Integer.toString(e.getLineNumber()),
                e.getSystemId()}));
    }
}

