/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.relaxng;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.TypeUseFactory;

import com.sun.tools.rngom.digested.DAttributePattern;
import com.sun.tools.rngom.digested.DChoicePattern;
import com.sun.tools.rngom.digested.DContainerPattern;
import com.sun.tools.rngom.digested.DDataPattern;
import com.sun.tools.rngom.digested.DElementPattern;
import com.sun.tools.rngom.digested.DEmptyPattern;
import com.sun.tools.rngom.digested.DGrammarPattern;
import com.sun.tools.rngom.digested.DGroupPattern;
import com.sun.tools.rngom.digested.DInterleavePattern;
import com.sun.tools.rngom.digested.DListPattern;
import com.sun.tools.rngom.digested.DMixedPattern;
import com.sun.tools.rngom.digested.DNotAllowedPattern;
import com.sun.tools.rngom.digested.DOneOrMorePattern;
import com.sun.tools.rngom.digested.DOptionalPattern;
import com.sun.tools.rngom.digested.DPattern;
import com.sun.tools.rngom.digested.DPatternVisitor;
import com.sun.tools.rngom.digested.DRefPattern;
import com.sun.tools.rngom.digested.DTextPattern;
import com.sun.tools.rngom.digested.DValuePattern;
import com.sun.tools.rngom.digested.DZeroOrMorePattern;

/**
 * Walks the pattern tree and binds it to a {@link TypeUse}.
 *
 * The singleton instance is kept in {@link RELAXNGCompiler}.
 *
 * TODO: I should really normalize before process.
 *
 * @author Kohsuke Kawaguchi
 */
final class TypeUseBinder implements DPatternVisitor<TypeUse> {
    private final RELAXNGCompiler compiler;

    public TypeUseBinder(RELAXNGCompiler compiler) {
        this.compiler = compiler;
    }


    public TypeUse onGrammar(DGrammarPattern p) {
        return CBuiltinLeafInfo.STRING;
    }

    public TypeUse onChoice(DChoicePattern p) {
        // can't support unions
        return CBuiltinLeafInfo.STRING;
    }

    public TypeUse onData(DDataPattern p) {
        return onDataType(p.getDatatypeLibrary(), p.getType());
    }

    public TypeUse onValue(DValuePattern p) {
        return onDataType(p.getDatatypeLibrary(),p.getType());
    }

    private TypeUse onDataType(String datatypeLibrary, String type) {
        DatatypeLib lib = compiler.datatypes.get(datatypeLibrary);
        if(lib!=null) {
            TypeUse use = lib.get(type);
            if(use!=null)
                return use;
        }

        // unknown
        return CBuiltinLeafInfo.STRING;
    }

    public TypeUse onInterleave(DInterleavePattern p) {
        return onContainer(p);
    }

    public TypeUse onGroup(DGroupPattern p) {
        return onContainer(p);
    }

    private TypeUse onContainer(DContainerPattern p) {
        TypeUse t=null;
        for( DPattern child : p ) {
            TypeUse s = child.accept(this);
            if(t!=null && t!=s)
                return CBuiltinLeafInfo.STRING; // heterogenous
            t = s;
        }
        return t;
    }

    public TypeUse onNotAllowed(DNotAllowedPattern p) {
        // TODO
        return error();
    }

    public TypeUse onEmpty(DEmptyPattern p) {
        return CBuiltinLeafInfo.STRING;
    }

    public TypeUse onList(DListPattern p) {
        return p.getChild().accept(this);
    }

    public TypeUse onOneOrMore(DOneOrMorePattern p) {
        return TypeUseFactory.makeCollection( p.getChild().accept(this) );
    }

    public TypeUse onZeroOrMore(DZeroOrMorePattern p) {
        return TypeUseFactory.makeCollection( p.getChild().accept(this) );
    }

    public TypeUse onOptional(DOptionalPattern p) {
        return CBuiltinLeafInfo.STRING;
    }

    public TypeUse onRef(DRefPattern p) {
        // TODO: check for enums
        return p.getTarget().getPattern().accept(this);
    }

    public TypeUse onText(DTextPattern p) {
        return CBuiltinLeafInfo.STRING;
    }

//
//
// Not allowed in this context
//
//
    public TypeUse onAttribute(DAttributePattern p) {
        return error();
    }

    public TypeUse onElement(DElementPattern p) {
        return error();
    }

    public TypeUse onMixed(DMixedPattern p) {
        return error();
    }

    private TypeUse error() {
        throw new IllegalStateException();
    }
}
