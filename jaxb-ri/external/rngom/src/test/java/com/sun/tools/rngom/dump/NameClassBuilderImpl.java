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
package com.sun.tools.rngom.dump;

import com.sun.tools.rngom.ast.builder.Annotations;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.CommentList;
import com.sun.tools.rngom.ast.builder.NameClassBuilder;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.om.ParsedNameClass;

import java.util.List;

/**
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class NameClassBuilderImpl implements NameClassBuilder {
    private final Factory factory;
    private final Printer printer;
    
    public NameClassBuilderImpl(Factory f,Printer p) {
        factory = f;
        printer = p;
    }

    @Override
    public ParsedNameClass annotate(ParsedNameClass nc, Annotations anno) throws BuildException {
        printer.name("annotate").param(nc).param(anno);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass annotateAfter(ParsedNameClass nc, ParsedElementAnnotation e) throws BuildException {
        printer.name("annotateAfter").param(nc).param(e);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass commentAfter(ParsedNameClass nc, CommentList comments) throws BuildException {
        printer.name("commentAfter").param(nc).param(comments);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass makeChoice(List nameClasses, Location loc, Annotations anno) {
        printer.name("makeChoice");
        for( int i=0; i<nameClasses.size(); i++ )
            printer.param(nameClasses.get(i));
        printer.param(loc).param(anno);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass makeName(String ns, String localName, String prefix, Location loc, Annotations anno) {
        printer.name("makeName").param(ns).param(localName).param(prefix).param(loc).param(anno);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass makeNsName(String ns, Location loc, Annotations anno) {
        printer.name("makeNsName").param(ns).param(loc).param(anno);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass makeNsName(String ns, ParsedNameClass except, Location loc, Annotations anno) {
        printer.name("makeNsName").param(ns).param(except).param(loc).param(anno);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass makeAnyName(Location loc, Annotations anno) {
        printer.name("makeAnyName").param(loc).param(anno);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass makeAnyName(ParsedNameClass except, Location loc, Annotations anno) {
        printer.name("makeAnyName").param(except).param(loc).param(anno);
        return printer.result(factory.createNameClass());
    }

    @Override
    public ParsedNameClass makeErrorNameClass() {
        printer.name("makeErrorNameClass");
        return printer.result(factory.createNameClass());
    }
}
