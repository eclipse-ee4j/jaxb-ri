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

import com.sun.xml.bind.webapp.AbstractTagImpl;

/**
 * Custom tag that produces XJC build id.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class VersionTag extends AbstractTagImpl {

    public int startTag() {
        return SKIP_BODY;
    }

    public int endTag() throws IOException {
        // context.getOut().write( Driver.getBuildID() )
        try {
            Class driverClass = XJCClassLoader.getInstance(context.getServletContext())
                .loadClass("com.sun.tools.xjc.Driver");
            
            
            context.getOut().write( (String)
                driverClass.getMethod("getBuildID",new Class[0]).invoke(null,null));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return EVAL_PAGE;
    }
}
