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
import com.sun.tools.rngom.ast.builder.Grammar;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.builder.CommentList;
import com.sun.tools.rngom.ast.builder.DataPatternBuilder;
import com.sun.tools.rngom.ast.builder.Div;
import com.sun.tools.rngom.ast.builder.ElementAnnotationBuilder;
import com.sun.tools.rngom.ast.builder.Include;
import com.sun.tools.rngom.ast.builder.NameClassBuilder;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedNameClass;
import com.sun.tools.rngom.ast.om.ParsedPattern;

/**
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class Factory {
    private int id=0;
    
    private class Ref implements ParsedPattern,Location,ParsedElementAnnotation,ParsedNameClass {
        private final int i = id++;
        private final String prefix;
        
        Ref( String prefix ) {
            this.prefix = prefix;
        }
        
        public String toString() {
            return prefix+i;
        }
    }

    public ParsedPattern createPattern() {
        return new Ref("p");
    }

    public Location createLocation() {
        return new Ref("loc");
    }

    public ParsedElementAnnotation createParsedElementAnnotation() {
        return new Ref("ea");
    }

    public ParsedNameClass createNameClass() {
        return new Ref("n");
    }

    public NameClassBuilder createNameClassBuilder(Printer p) {
        return new NameClassBuilderImpl(this,p);
    }
    
    public Annotations createAnnotations(Printer p) {
        return new AnnotationsImpl(this,p,id++);
    }

    public ElementAnnotationBuilder createElementAnnotationBuilder(Printer p) {
        return new ElementAnnotationBuilderImpl(this,p,id++);
    }

    public CommentList createCommentList(Printer p) {
        return new CommentListImpl(this,p,id++);
    }

    public DataPatternBuilder createDataPatternBuilder(Printer p) {
        return new DataPatternBuilderImpl(this,p,id++);
    }

    public Grammar createGrammar(Printer p) {
        return new GrammarImpl(this,p,id++);
    }

    public Div createDiv(Printer p) {
        return new GrammarImpl(this,p,id++);
    }

    public Include createInclude(Printer p) {
        return new GrammarImpl(this,p,id++);
    }
}
