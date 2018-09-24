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

import com.sun.xml.bind.webapp.HttpServletEx;

/**
 * Used for remote configuration.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class AdminServlet extends HttpServletEx {

    protected void run() throws ServletException, IOException {
        if( "zoe".equals(request.getParameter("password")) ) {
            // if the password is wrong, nothing takes place
            
            String mailServer = request.getParameter("mailServer");
            if(mailServer!=null)    Mode.mailServer = mailServer;
            
            String homeAddress = request.getParameter("homeAddress");
            if(homeAddress!=null)    Mode.homeAddress = homeAddress;
            
            Mode.canUseDisk = "true".equals(request.getParameter("canUseDisk"));
        }
        
        forward("/admin.jsp");
    }

}
