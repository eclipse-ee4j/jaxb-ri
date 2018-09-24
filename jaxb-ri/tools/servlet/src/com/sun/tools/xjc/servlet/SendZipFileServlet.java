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
 * Sends a zip file to the client.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SendZipFileServlet extends HttpServletEx {

    protected void run() throws ServletException, IOException {
        Compiler compiler = Compiler.get(request);

        response.setContentType("application/x-zip-compressed");
        response.getOutputStream().write(compiler.getZipFile());
    }

}
