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
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;

import com.sun.xml.bind.webapp.HttpServletEx;

/**
 * Serves a static Java source file in the output directory.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class FileServlet extends HttpServletEx {

    protected void run() throws ServletException, IOException {
        String fileName = request.getPathInfo().substring(1);
        File doc = new File( Compiler.get(request).getOutDir(), fileName );
        if( !doc.exists() ) {
            response.sendError(404);
            return;
        } else {
            if(fileName.endsWith(".html"))
                response.setContentType("text/html");
        
            Util.copyStream( response.getOutputStream(), new FileInputStream(doc) );
        }
    }

}
