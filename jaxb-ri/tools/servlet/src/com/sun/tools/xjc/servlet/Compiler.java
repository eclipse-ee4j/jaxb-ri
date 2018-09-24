/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

/**
 * Runs the compilation in a separate thread.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class Compiler extends Thread {
    
    /**
     * Gets the compiler instance associated with the current client.
     */
    public static Compiler get( HttpServletRequest request ) {
        return (Compiler)request.getSession().getAttribute("compiler");
    }
    
    /**
     * Associates this instance to the current client.
     */
    public void associateTo( HttpServletRequest request ) {
        request.getSession().setAttribute("compiler", this);
    }

    public abstract File getOutDir();


    /**
     * This method returns the status message produced by the compiler.
     */
    public abstract String getStatusMessages();
    
    /**
     * If the compilation was successful, this method
     * returns the byte image of the source code zip file.
     */
    public abstract byte[] getZipFile();
}
