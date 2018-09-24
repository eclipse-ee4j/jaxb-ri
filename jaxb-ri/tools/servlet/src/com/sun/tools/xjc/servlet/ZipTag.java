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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.IterationTag;

import com.sun.xml.bind.webapp.*;

/**
 * Repeats the body for each zip entry in the file.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ZipTag extends AbstractTagImpl implements IterationTag {
    
    private ZipInputStream zip;
    private ZipEntry currentEntry;
    

    public ZipEntry getCurrentEntry() {
        return currentEntry;
    }

    public ZipInputStream getZip() {
        return zip;
    }

    
    public int startTag() throws IOException {
        Compiler c = (Compiler)getRequest().getSession().getAttribute("compiler");
        if(c==null)     return SKIP_BODY;
    
        zip = new ZipInputStream(new ByteArrayInputStream(c.getZipFile()));
        return next()?EVAL_BODY_INCLUDE:SKIP_BODY;
    }
    
    /**
     * Moves to the next zip entry.
     */
    private boolean next() throws IOException {
        currentEntry = zip.getNextEntry();
        if( currentEntry==null ) {
            zip.close();
            zip = null;     // release
            return false;
        }
        
        String name = currentEntry.getName();
        if( name.indexOf("/impl/")!=-1 || name.indexOf(".java")==-1 )
            return next();  // skip non-java file and impl file.
        
        return true;
    }

    public int doAfterBody() throws JspException {
        try {
            zip.closeEntry();
            return next()?EVAL_BODY_AGAIN:SKIP_BODY;
        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }
    }


    public int endTag() {
        return EVAL_PAGE;
    }
}
