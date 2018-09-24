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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import com.sun.tools.rngom.ast.builder.Annotations;
import com.sun.tools.rngom.ast.builder.Grammar;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.builder.CommentList;
import com.sun.tools.rngom.ast.builder.DataPatternBuilder;
import com.sun.tools.rngom.ast.builder.Div;
import com.sun.tools.rngom.ast.builder.ElementAnnotationBuilder;
import com.sun.tools.rngom.ast.builder.Include;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedNameClass;
import com.sun.tools.rngom.ast.om.ParsedPattern;

/**
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class Printer {
    private final PrintWriter out;
    private boolean hasParams = false;
    
    public Printer( PrintWriter out ) {
        this.out = out;
    }
    
    public Printer( Writer out ) {
        this.out = new PrintWriter(out);
    }
    
    public Printer( OutputStream out ) {
        this(new OutputStreamWriter(out));
    }
    
    public Printer object(String name) {
        out.print(name);
        out.print('.');
        return this;
    }
    
    public Printer name( String name ) {
        out.print(name+'(');
        return this;
    }
    
    public Printer param( Object o ) {
        if(hasParams)   out.print(',');
        if(o instanceof String) {
            out.print('"');
            out.print(o);
            out.print('"');
        } else {
            out.print(o);
        }
        hasParams = true;
        return this;
    }

    public Printer param(int i) {
        return param(new Integer(i));
    }
    
    private Object r( Object r ) {
        out.print(") -> ");
        out.print(r);
        out.println();
        out.flush();
        hasParams = false;
        return r;
    }
    
    public void result() {
        out.println(")");
        out.flush();
        hasParams = false;
    }
    
    public ParsedPattern result( ParsedPattern p ) {
        r(p);
        return p;
    }
    
    public ParsedNameClass result( ParsedNameClass nc ) {
        r(nc);
        return nc;
    }
    
    public Location result( Location l ) {
        r(l);
        return l;
    }
    
    public Annotations result(Annotations a ) {
        r(a);
        return a;
    }
    
    public CommentList result( CommentList c ) {
        r(c);
        return c;
    }
    
    public Grammar result(Grammar g ) {
        r(g);
        return g;
    }
    
    public Div result( Div d ) {
        r(d);
        return d;
    }
    
    public Include result( Include i ) {
        r(i);
        return i;
    }
    
    public ElementAnnotationBuilder result( ElementAnnotationBuilder eab ) {
        r(eab);
        return eab;
    }
    
    public ParsedElementAnnotation result(ParsedElementAnnotation a ) {
        r(a);
        return a;
    }

    public DataPatternBuilder result(DataPatternBuilder builder) {
        r(builder);
        return builder;
    }
}
