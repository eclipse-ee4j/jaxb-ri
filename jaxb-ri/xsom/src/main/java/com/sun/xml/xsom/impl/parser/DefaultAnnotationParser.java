/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl.parser;

import com.sun.xml.xsom.parser.AnnotationContext;
import com.sun.xml.xsom.parser.AnnotationParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * AnnotationParser that just ignores annotation.
 * 
 * <p>
 * This class doesn't have any state. So it should be used as a singleton.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class DefaultAnnotationParser extends AnnotationParser {
    
    private DefaultAnnotationParser() {}
    
    public static final AnnotationParser theInstance = new DefaultAnnotationParser();
    
    public ContentHandler getContentHandler(
        AnnotationContext contest, String elementName,
        ErrorHandler errorHandler, EntityResolver entityResolver ) {
        return new DefaultHandler();
    }
    
    public Object getResult( Object existing ) {
        return null;
    }
    
    
}

