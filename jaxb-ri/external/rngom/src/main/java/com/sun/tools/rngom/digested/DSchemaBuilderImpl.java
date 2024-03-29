/*
 * Copyright (C) 2004-2024
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
package com.sun.tools.rngom.digested;

import com.sun.tools.rngom.ast.builder.Grammar;
import com.sun.tools.rngom.ast.builder.NameClassBuilder;
import com.sun.tools.rngom.ast.builder.Scope;
import com.sun.tools.rngom.ast.om.ParsedPattern;
import com.sun.tools.rngom.nc.NameClass;
import com.sun.tools.rngom.parse.Context;
import com.sun.tools.rngom.parse.IllegalSchemaException;
import com.sun.tools.rngom.parse.Parseable;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.DataPatternBuilder;
import com.sun.tools.rngom.ast.builder.ElementAnnotationBuilder;
import com.sun.tools.rngom.ast.builder.SchemaBuilder;
import com.sun.tools.rngom.ast.util.LocatorImpl;
import com.sun.tools.rngom.nc.NameClassBuilderImpl;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

/**
 * Parses as {@link Parseable} into a {@link DPattern}.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class DSchemaBuilderImpl implements SchemaBuilder
    <NameClass,DPattern,ElementWrapper,LocatorImpl,Annotation,CommentListImpl> {

    private final NameClassBuilder ncb = new NameClassBuilderImpl();

    /**
     * Used to parse annotations.
     */
    private final Document dom;

    public DSchemaBuilderImpl() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            this.dom = dbf.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            // impossible
            throw new InternalError(e.getMessage());
        }
    }

    public NameClassBuilder getNameClassBuilder() throws BuildException {
        return ncb;
    }

    static  DPattern wrap( DPattern p, LocatorImpl loc, Annotation anno ) {
        p.location = loc;
        if(anno!=null)
            p.annotation = anno.getResult();
        return p;
    }

    static DContainerPattern addAll( DContainerPattern parent, List<DPattern> children) {
        for (DPattern c : children)
            parent.add(c);
        return parent;
    }

    static DUnaryPattern addBody(DUnaryPattern parent, ParsedPattern _body, LocatorImpl loc ) {
        parent.setChild( (DPattern)_body );
        return parent;
    }

    public DPattern makeChoice(List<DPattern> patterns, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addAll(new DChoicePattern(),patterns),loc,anno);
    }

    public DPattern makeInterleave(List<DPattern> patterns, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addAll(new DInterleavePattern(),patterns),loc,anno);
    }

    public DPattern makeGroup(List<DPattern> patterns, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addAll(new DGroupPattern(),patterns),loc,anno);
    }

    public DPattern makeOneOrMore(DPattern p, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addBody(new DOneOrMorePattern(),p,loc),loc,anno);
    }

    public DPattern makeZeroOrMore(DPattern p, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addBody(new DZeroOrMorePattern(),p,loc),loc,anno);
    }

    public DPattern makeOptional(DPattern p, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addBody(new DOptionalPattern(),p,loc),loc,anno);
    }

    public DPattern makeList(DPattern p, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addBody(new DListPattern(),p,loc),loc,anno);
    }

    public DPattern makeMixed(DPattern p, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addBody(new DMixedPattern(),p,loc),loc,anno);
    }

    public DPattern makeEmpty(LocatorImpl loc, Annotation anno) {
        return wrap(new DEmptyPattern(),loc,anno);
    }

    public DPattern makeNotAllowed(LocatorImpl loc, Annotation anno) {
        return wrap(new DNotAllowedPattern(),loc,anno);
    }

    public DPattern makeText(LocatorImpl loc, Annotation anno) {
        return wrap(new DTextPattern(),loc,anno);
    }

    public DPattern makeAttribute(NameClass nc, DPattern p, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addBody(new DAttributePattern(nc),p,loc),loc,anno);
    }

    public DPattern makeElement(NameClass nc, DPattern p, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(addBody(new DElementPattern(nc),p,loc),loc,anno);
    }

    public DataPatternBuilder makeDataPatternBuilder(String datatypeLibrary, String type, LocatorImpl loc) throws BuildException {
        return new DataPatternBuilderImpl(datatypeLibrary,type,loc);
    }

    public DPattern makeValue(String datatypeLibrary, String type, String value, Context c, String ns, LocatorImpl loc, Annotation anno) throws BuildException {
        return wrap(new DValuePattern(datatypeLibrary,type,value,c.copy(),ns),loc,anno);
    }

    public Grammar makeGrammar(Scope parent) {
        return new GrammarBuilderImpl(new DGrammarPattern(),parent,this);
    }

    public DPattern annotate(DPattern p, Annotation anno) throws BuildException {
        // TODO: not sure when this is used
        return p;
    }

    public DPattern annotateAfter(DPattern p, ElementWrapper e) throws BuildException {
        // TODO
        return p;
    }

    public DPattern commentAfter(DPattern p, CommentListImpl comments) throws BuildException {
        // TODO
        return p;
    }

    public DPattern makeExternalRef(Parseable current, String uri, String ns,
                                    Scope<DPattern, ElementWrapper, LocatorImpl, Annotation, CommentListImpl> scope, LocatorImpl loc, Annotation anno) throws BuildException, IllegalSchemaException {
        // TODO
        return null;
    }

    public LocatorImpl makeLocation(String systemId, int lineNumber, int columnNumber) {
        return new LocatorImpl(systemId,lineNumber,columnNumber);
    }

    public Annotation makeAnnotations(CommentListImpl comments, Context context) {
        return new Annotation();
    }

    public ElementAnnotationBuilder makeElementAnnotationBuilder(String ns, String localName, String prefix, LocatorImpl loc, CommentListImpl comments, Context context) {
        String qname;
        if(prefix==null)
            qname = localName;
        else
            qname = prefix+':'+localName;
        return new ElementAnnotationBuilderImpl(dom.createElementNS(ns,qname));
    }

    public CommentListImpl makeCommentList() {
        return new CommentListImpl();
    }

    public DPattern makeErrorPattern() {
        return new DNotAllowedPattern();
    }

    public boolean usesComments() {
        return false;
    }

    public DPattern expandPattern(DPattern p) throws BuildException, IllegalSchemaException {
        return p;
    }
}
