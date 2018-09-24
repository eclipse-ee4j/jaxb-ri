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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sun.xml.bind.webapp.HttpServletEx;

/**
 * Parses the form submission and build {@link Compiler}.
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SubmissionServlet extends HttpServletEx {
    
    protected void run() throws ServletException, IOException {
        System.out.println("accepting a new submission");
        
        // to workaround a bug in JWSDP1.2, all the XJC-related handling is done
        // in a separate class loaded by a separate class loader.
        Compiler compiler;
        try {
            Class parserClass = XJCClassLoader.getInstance(getServletContext())
                .loadClass("com.sun.tools.xjc.servlet.boxed.SubmissionParser");
                
                
            // launch compilation
            compiler = (Compiler)
                parserClass.getMethod("parse",new Class[]{HttpServletRequest.class})
                .invoke(null,new Object[]{request});
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        
        // start a new session. remember the compiler so that we can access it later.
        HttpSession old = request.getSession(false);
        if( old!=null ) old.invalidate();
        
        compiler.associateTo(request);
        
        response.sendRedirect("compiler");

    }

}
