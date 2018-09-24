/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.builder.relaxng;

import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.CommentList;
import com.sun.tools.rngom.ast.builder.ElementAnnotationBuilder;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;

/**
 * @author Kohsuke Kawaguchi
 */
final class ElementAnnotationBuilderImpl implements ElementAnnotationBuilder {
    public void addText(String value, Location location, CommentList commentList) throws BuildException {
    }

    public ParsedElementAnnotation makeElementAnnotation() throws BuildException {
        return null;
    }

    public void addAttribute(String ns, String localName, String prefix, String value, Location location) throws BuildException {
    }

    public void addElement(ParsedElementAnnotation parsedElementAnnotation) throws BuildException {
    }

    public void addComment(CommentList commentList) throws BuildException {
    }

    public void addLeadingComment(CommentList commentList) throws BuildException {
    }
}
