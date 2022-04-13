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
package com.sun.tools.rngom.parse.host;

import com.sun.tools.rngom.ast.builder.Annotations;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.CommentList;
import com.sun.tools.rngom.ast.builder.NameClassBuilder;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.om.ParsedNameClass;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
final class NameClassBuilderHost extends Base implements NameClassBuilder {
    final NameClassBuilder lhs;
    final NameClassBuilder rhs;
    
    NameClassBuilderHost( NameClassBuilder lhs, NameClassBuilder rhs ) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public ParsedNameClass annotate(ParsedNameClass _nc, Annotations _anno) throws BuildException {
        ParsedNameClassHost nc = (ParsedNameClassHost) _nc;
        AnnotationsHost anno = cast(_anno);
        
        return new ParsedNameClassHost(
            lhs.annotate(nc.lhs, anno.lhs),
            rhs.annotate(nc.rhs, anno.rhs) );
    }

    public ParsedNameClass annotateAfter(ParsedNameClass _nc, ParsedElementAnnotation _e) throws BuildException {
        ParsedNameClassHost nc = (ParsedNameClassHost) _nc;
        ParsedElementAnnotationHost e = (ParsedElementAnnotationHost) _e;
        
        return new ParsedNameClassHost(
            lhs.annotateAfter(nc.lhs, e.lhs),
            rhs.annotateAfter(nc.rhs, e.rhs));
    }

    public ParsedNameClass commentAfter(ParsedNameClass _nc, CommentList _comments) throws BuildException {
        ParsedNameClassHost nc = (ParsedNameClassHost) _nc;
        CommentListHost comments = (CommentListHost) _comments;
        
        return new ParsedNameClassHost(
            lhs.commentAfter(nc.lhs, comments==null?null:comments.lhs),
            rhs.commentAfter(nc.rhs, comments==null?null:comments.rhs));
    }

    public ParsedNameClass makeChoice(List _nameClasses, Location _loc, Annotations _anno) {
        List<ParsedNameClass> lnc = new ArrayList<>();
        List<ParsedNameClass> rnc = new ArrayList<>();
        for( int i=0; i<_nameClasses.size(); i++ ) {
            lnc.add(((ParsedNameClassHost)_nameClasses.get(i)).lhs);
            rnc.add(((ParsedNameClassHost)_nameClasses.get(i)).rhs);
        }
        LocationHost loc = cast(_loc);
        AnnotationsHost anno = cast(_anno);
        
        return new ParsedNameClassHost(
            lhs.makeChoice( lnc, loc.lhs, anno.lhs ),
            rhs.makeChoice( rnc, loc.rhs, anno.rhs ) );
    }

    public ParsedNameClass makeName(String ns, String localName, String prefix, Location _loc, Annotations _anno) {
        LocationHost loc = cast(_loc);
        AnnotationsHost anno = cast(_anno);
        
        return new ParsedNameClassHost(
            lhs.makeName( ns, localName, prefix, loc.lhs, anno.lhs ),
            rhs.makeName( ns, localName, prefix, loc.rhs, anno.rhs ) );
    }

    public ParsedNameClass makeNsName(String ns, Location _loc, Annotations _anno) {
        LocationHost loc = cast(_loc);
        AnnotationsHost anno = cast(_anno);
        
        return new ParsedNameClassHost(
            lhs.makeNsName( ns, loc.lhs, anno.lhs ),
            rhs.makeNsName( ns, loc.rhs, anno.rhs ) );
    }

    public ParsedNameClass makeNsName(String ns, ParsedNameClass _except, Location _loc, Annotations _anno) {
        ParsedNameClassHost except = (ParsedNameClassHost) _except;
        LocationHost loc = cast(_loc);
        AnnotationsHost anno = cast(_anno);
        
        return new ParsedNameClassHost(
            lhs.makeNsName( ns, except.lhs, loc.lhs, anno.lhs ),
            rhs.makeNsName( ns, except.rhs, loc.rhs, anno.rhs ) );
    }

    public ParsedNameClass makeAnyName(Location _loc, Annotations _anno) {
        LocationHost loc = cast(_loc);
        AnnotationsHost anno = cast(_anno);
        
        return new ParsedNameClassHost(
            lhs.makeAnyName( loc.lhs, anno.lhs ),
            rhs.makeAnyName( loc.rhs, anno.rhs ) );
    }

    public ParsedNameClass makeAnyName(ParsedNameClass _except, Location _loc, Annotations _anno) {
        ParsedNameClassHost except = (ParsedNameClassHost) _except;
        LocationHost loc = cast(_loc);
        AnnotationsHost anno = cast(_anno);
        
        return new ParsedNameClassHost(
            lhs.makeAnyName( except.lhs, loc.lhs, anno.lhs ),
            rhs.makeAnyName( except.rhs, loc.rhs, anno.rhs ) );
    }

    public ParsedNameClass makeErrorNameClass() {
        return new ParsedNameClassHost( lhs.makeErrorNameClass(), rhs.makeErrorNameClass() );
    }
}
