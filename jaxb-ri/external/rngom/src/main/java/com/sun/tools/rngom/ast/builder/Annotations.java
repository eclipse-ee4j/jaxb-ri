/*
 * Copyright (C) 2004-2011
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sun.tools.rngom.ast.builder;

import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.om.Location;


/**
 * Includes attributes and child elements before any RELAX NG element.
 */
public interface Annotations<
    E extends ParsedElementAnnotation,
    L extends Location,
    CL extends CommentList<L>> {

    /**
     * Called for an attribute annotation.
     */
    void addAttribute(String ns, String localName, String prefix, String value, L loc) throws BuildException;

    /**
     * Called for a child element annotation.
     */
    void addElement(E ea) throws BuildException;

    /**
     * Adds comments following the last initial child element annotation.
     */
    void addComment(CL comments) throws BuildException;

    void addLeadingComment(CL comments) throws BuildException;
}
