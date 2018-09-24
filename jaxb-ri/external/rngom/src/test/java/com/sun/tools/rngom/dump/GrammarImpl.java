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
import com.sun.tools.rngom.ast.builder.CommentList;
import com.sun.tools.rngom.ast.builder.Div;
import com.sun.tools.rngom.ast.builder.Grammar;
import com.sun.tools.rngom.ast.builder.Include;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.om.ParsedPattern;
import com.sun.tools.rngom.parse.IllegalSchemaException;
import com.sun.tools.rngom.parse.Parseable;
import com.sun.tools.rngom.ast.builder.BuildException;

/**
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class GrammarImpl extends Base implements Grammar,Div,Include {
    public GrammarImpl(Factory f, Printer p, int id) {
        super(f, p, id);
    }

    protected String prefix() {
        return "g";
    }

    public ParsedPattern endGrammar(Location loc, Annotations anno) throws BuildException {
        out("endGrammar").param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public void define(String name, Combine combine, ParsedPattern pattern, Location loc, Annotations anno) throws BuildException {
        out("define").param(name).param(combine).param(pattern).param(loc).param(anno).result();
    }

    public void topLevelAnnotation(ParsedElementAnnotation ea) throws BuildException {
        out("topLevelAnnotation").param(ea).result();
    }

    public void topLevelComment(CommentList comments) throws BuildException {
        out("topLevelComment").param(comments).result();
    }

    public Div makeDiv() {
        out("makeDiv");
        return printer.result(factory.createDiv(printer));
    }

    public Include makeInclude() {
        out("makeInclude");
        return printer.result(factory.createInclude(printer));
    }

    public ParsedPattern makeParentRef(String name, Location loc, Annotations anno) throws BuildException {
        out("makeParentRef").param(name).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeRef(String name, Location loc, Annotations anno) throws BuildException {
        out("makeRef").param(name).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public void endDiv(Location loc, Annotations anno) throws BuildException {
        out("endDiv").param(loc).param(anno).result();
    }

    public void endInclude(Parseable current, String uri, String ns, Location loc, Annotations anno) throws BuildException, IllegalSchemaException {
        out("endInclude").param(uri).param(ns).param(loc).param(anno).result();
    }
}
