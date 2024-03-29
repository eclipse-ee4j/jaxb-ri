/*
 * Copyright (C) 2004-2012
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

import java.util.ArrayList;
import java.util.List;

import com.sun.tools.rngom.ast.builder.Annotations;
import com.sun.tools.rngom.ast.builder.CommentList;
import com.sun.tools.rngom.ast.builder.Grammar;
import com.sun.tools.rngom.ast.builder.Include;
import com.sun.tools.rngom.ast.builder.Scope;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.om.ParsedPattern;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.Div;
import com.sun.tools.rngom.ast.util.LocatorImpl;
import org.w3c.dom.Element;

/**
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
class GrammarBuilderImpl implements Grammar, Div {

    protected final DGrammarPattern grammar;

    protected final Scope parent;

    protected final DSchemaBuilderImpl sb;

    /**
     * Additional top-level element annotations.
     * Can be null.
     */
    private List<Element> additionalElementAnnotations;

    public GrammarBuilderImpl(DGrammarPattern p, Scope parent, DSchemaBuilderImpl sb) {
        this.grammar = p;
        this.parent = parent;
        this.sb = sb;
    }

    public ParsedPattern endGrammar(Location loc, Annotations anno) throws BuildException {
    	// Harshit : Fixed possible NPE and issue in handling of annotations
        if (anno != null) {
            if (grammar.annotation != null) {
                grammar.annotation.contents.addAll(((Annotation) anno).getResult().contents);
            }
        }
        return grammar;
    }

    public void endDiv(Location loc, Annotations anno) throws BuildException {
    }

    public void define(String name, Combine combine, ParsedPattern pattern, Location loc, Annotations anno) throws BuildException {
        if(name==START) {
            grammar.start = (DPattern)pattern;
        } else {
            // TODO: handle combine
            DDefine d = grammar.getOrAdd(name);
            d.setPattern( (DPattern) pattern );
            if (anno!=null) {
                d.annotation = ((Annotation)anno).getResult();
            }
        }
    }

    public void topLevelAnnotation(ParsedElementAnnotation ea) throws BuildException {
    	// Harshit : Fixed issue in handling of annotations
    	if (additionalElementAnnotations==null) {
            additionalElementAnnotations = new ArrayList<>();
        }
        additionalElementAnnotations.add(((ElementWrapper)ea).element);
        if (grammar.annotation==null) {
            grammar.annotation = new DAnnotation();
        }
        grammar.annotation.contents.addAll(additionalElementAnnotations);
    }

    public void topLevelComment(CommentList comments) throws BuildException {
    }

    public Div makeDiv() {
        return this;
    }

    public Include makeInclude() {
        return new IncludeImpl(grammar,parent,sb);
    }

    public ParsedPattern makeParentRef(String name, Location loc, Annotations anno) throws BuildException {
        return parent.makeRef(name,loc,anno);
    }

    public ParsedPattern makeRef(String name, Location loc, Annotations anno) throws BuildException {
        return DSchemaBuilderImpl.wrap( new DRefPattern(grammar.getOrAdd(name)), (LocatorImpl)loc, (Annotation)anno );
    }
}
