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

import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.om.ParsedNameClass;

import java.util.List;


/**
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public interface NameClassBuilder<
    N extends ParsedNameClass,
    E extends ParsedElementAnnotation,
    L extends Location,
    A extends Annotations<E,L,CL>,
    CL extends CommentList<L> > {

    N annotate(N nc, A anno) throws BuildException;
    N annotateAfter(N nc, E e) throws BuildException;
    N commentAfter(N nc, CL comments) throws BuildException;
    N makeChoice(List<N> nameClasses, L loc, A anno);

// should be handled by parser - KK
//    static final String INHERIT_NS = new String("#inherit");

// similarly, xmlns:* attribute should be rejected by the parser -KK
    
    N makeName(String ns, String localName, String prefix, L loc, A anno);
    N makeNsName(String ns, L loc, A anno);
    /**
     * Caller must enforce constraints on except.
     */
    N makeNsName(String ns, N except, L loc, A anno);
    N makeAnyName(L loc, A anno);
    /**
     * Caller must enforce constraints on except.
     */
    N makeAnyName(N except, L loc, A anno);

    N makeErrorNameClass();
}
