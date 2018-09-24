/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2;

import org.apache.tools.ant.Project;
import org.xml.sax.SAXParseException;

import java.text.MessageFormat;

/**
 * @author Kohsuke Kawaguchi
 */
public class AntErrorListener implements ErrorListener {
    private final Project project;

    public AntErrorListener(Project p) {
        this.project = p;
    }

    public void error(SAXParseException e) {
        print(e,Project.MSG_ERR);
    }

    public void fatalError(SAXParseException e) {
        print(e,Project.MSG_ERR);
    }

    public void warning(SAXParseException e) {
        print(e,Project.MSG_WARN);
    }

    private void print(SAXParseException e, int level) {
        String msg = e.getMessage();
        if(msg==null)   msg=e.toString();
        project.log(msg,level);
        project.log(getLocation(e),level);
    }

    String getLocation(SAXParseException e) {
        return MessageFormat.format("  {0}:{1} of {2}",
            new Object[]{
                String.valueOf(e.getLineNumber()),
                String.valueOf(e.getColumnNumber()),
                e.getSystemId()});
    }
}
