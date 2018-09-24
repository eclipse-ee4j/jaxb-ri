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

import java.io.IOException;

import javax.servlet.ServletException;

import com.sun.xml.bind.webapp.LongProcessServlet;

/**
 * Launches {@link Compiler} from the form input.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XJCServlet extends LongProcessServlet {
    
    protected void run() throws ServletException, IOException {
        // check if the browser supports session
        if( request.getSession(false)==null ) {
            response.sendRedirect("cookieDisabled.jsp");
            return;
        }
        
        super.run();
    }
    
    protected Thread createTask() throws ServletException, IOException {
        return Compiler.get(request);
    }

    protected void renderResult( Thread task ) throws ServletException, IOException {
        Compiler compiler = (Compiler)task;
        
        // forward to the result
        if( compiler.getZipFile()==null )
            response.sendRedirect("compileError.jsp");
        else
            response.sendRedirect("compileSuccess.jsp");
    }
    
    
    

    protected String getProgressTitle() {
        return "Compiling your schema";
    }

    protected String getProgressMessage() {
        return "your browser will be redirected to the result page once the compilation is completed";
    }

}
