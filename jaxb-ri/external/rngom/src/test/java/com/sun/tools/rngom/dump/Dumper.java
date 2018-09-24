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
import com.sun.tools.rngom.ast.builder.Grammar;
import com.sun.tools.rngom.ast.builder.NameClassBuilder;
import com.sun.tools.rngom.ast.builder.Scope;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.om.ParsedPattern;
import com.sun.tools.rngom.parse.Context;
import com.sun.tools.rngom.parse.IllegalSchemaException;
import com.sun.tools.rngom.parse.Parseable;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.DataPatternBuilder;
import com.sun.tools.rngom.ast.builder.ElementAnnotationBuilder;
import com.sun.tools.rngom.ast.builder.SchemaBuilder;
import com.sun.tools.rngom.ast.om.ParsedNameClass;

import java.util.List;
import java.io.OutputStream;

/**
 * Dumps the callback invocations to an {@link OutputStream}.
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class Dumper implements SchemaBuilder {
    
    private final Factory factory;
    private final Printer printer;
    private NameClassBuilder ncb;
    
    public Dumper() {
        this(new Factory(),new Printer(System.out));
    }
    
    public Dumper(Factory f,Printer p) {
        factory = f;
        printer = p;
    }
    
    public NameClassBuilder getNameClassBuilder() throws BuildException {
        if(ncb==null)
            ncb = factory.createNameClassBuilder(printer);
        return ncb;
    }

    public ParsedPattern makeChoice(List patterns, Location loc, Annotations anno) throws BuildException {
        printer.name("makeChoice");
        return makeNode(patterns, loc, anno);
    }

    public ParsedPattern makeInterleave(List patterns, Location loc, Annotations anno) throws BuildException {
        printer.name("makeInterleave");
        return makeNode(patterns, loc, anno);
    }

    public ParsedPattern makeGroup(List patterns, Location loc, Annotations anno) throws BuildException {
        printer.name("makeGroup");
        return makeNode(patterns, loc, anno);
    }

    private ParsedPattern makeNode(List patterns, Location loc, Annotations anno) {
        for( int i=0; i<patterns.size(); i++ )
            printer.param((ParsedPattern)patterns.get(i));
        printer.param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeOneOrMore(ParsedPattern p, Location loc, Annotations anno) throws BuildException {
        printer.name("makeOneOrMore").param(p).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeZeroOrMore(ParsedPattern p, Location loc, Annotations anno) throws BuildException {
        printer.name("makeZeroOrMore").param(p).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeOptional(ParsedPattern p, Location loc, Annotations anno) throws BuildException {
        printer.name("makeOptional").param(p).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeList(ParsedPattern p, Location loc, Annotations anno) throws BuildException {
        printer.name("makeList").param(p).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeMixed(ParsedPattern p, Location loc, Annotations anno) throws BuildException {
        printer.name("makeMixed").param(p).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeEmpty(Location loc, Annotations anno) {
        printer.name("makeEmpty").param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeNotAllowed(Location loc, Annotations anno) {
        printer.name("makeNotAllowed").param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeText(Location loc, Annotations anno) {
        printer.name("makeText").param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeAttribute(ParsedNameClass nc, ParsedPattern p, Location loc, Annotations anno) throws BuildException {
        printer.name("makeAttribute").param(nc).param(p).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeElement(ParsedNameClass nc, ParsedPattern p, Location loc, Annotations anno) throws BuildException {
        printer.name("makeElement").param(nc).param(p).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public DataPatternBuilder makeDataPatternBuilder(String datatypeLibrary, String type, Location loc) throws BuildException {
        printer.name("makeDataPatternBuilder")
            .param(datatypeLibrary).param(type).param(loc);
        return printer.result(factory.createDataPatternBuilder(printer));
    }

    public ParsedPattern makeValue(String datatypeLibrary, String type, String value, Context c, String ns, Location loc, Annotations anno) throws BuildException {
        printer.name("makeValue").param(datatypeLibrary).param(type).param(value).param(ns).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public Grammar makeGrammar(Scope parent) {
        printer.name("makeGrammar").param(parent);
        return printer.result(factory.createGrammar(printer));
    }

    public ParsedPattern annotate(ParsedPattern p, Annotations anno) throws BuildException {
        printer.name("annotate").param(p).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern annotateAfter(ParsedPattern p, ParsedElementAnnotation e) throws BuildException {
        printer.name("annotateAfter").param(p).param(e);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern commentAfter(ParsedPattern p, CommentList comments) throws BuildException {
        printer.name("commentAfter").param(p).param(comments);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makeExternalRef(Parseable current, String uri, String ns, Scope scope, Location loc, Annotations anno) throws BuildException, IllegalSchemaException {
        printer.name("makeExternalRef").param(uri).param(ns).param(scope).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public Location makeLocation(String systemId, int lineNumber, int columnNumber) {
        printer.name("makeLocation").param(systemId).param(lineNumber).param(columnNumber);
        return printer.result(factory.createLocation());
    }

    public Annotations makeAnnotations(CommentList comments, Context context) {
        printer.name("makeAnnotations").param(comments);
        return printer.result(factory.createAnnotations(printer));
    }

    public ElementAnnotationBuilder makeElementAnnotationBuilder(
        String ns, String localName, String prefix, Location loc, CommentList comments, Context context) {
        
        printer.name("makeElementAnnotationBuilder")
            .param(ns).param(localName).param(prefix).param(loc).param(comments);
        return printer.result(factory.createElementAnnotationBuilder(printer));
    }

    public CommentList makeCommentList() {
        printer.name("makeCommentList");
        return printer.result(factory.createCommentList(printer));
    }

    public ParsedPattern makeErrorPattern() {
        printer.name("makeErrorPattern");
        return printer.result(factory.createPattern());
    }

    public boolean usesComments() {
        return true;
    }

    public ParsedPattern expandPattern(ParsedPattern p) throws BuildException, IllegalSchemaException {
        printer.name("expandPattern").param(p);
        return printer.result(factory.createPattern());
    }
}
