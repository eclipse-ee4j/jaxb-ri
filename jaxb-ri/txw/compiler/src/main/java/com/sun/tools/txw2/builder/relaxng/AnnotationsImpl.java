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

import com.sun.tools.rngom.ast.builder.Annotations;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.util.LocatorImpl;

/**
 * @author Kohsuke Kawaguchi
 */
final class AnnotationsImpl implements Annotations<ParsedElementAnnotation,LocatorImpl,CommentListImpl> {
    public void addAttribute(String ns, String localName, String prefix, String value, LocatorImpl locator) throws BuildException {
    }

    public void addElement(ParsedElementAnnotation parsedElementAnnotation) throws BuildException {
    }

    public void addComment(CommentListImpl commentList) throws BuildException {
    }

    public void addLeadingComment(CommentListImpl commentList) throws BuildException {
    }
}
