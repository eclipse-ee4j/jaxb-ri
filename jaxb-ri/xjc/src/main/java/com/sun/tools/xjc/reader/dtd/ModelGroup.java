/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.dtdparser.DTDEventListener;


/**
 * @author Kohsuke Kawaguchi
 */
final class ModelGroup extends Term {
    enum Kind {
        CHOICE, SEQUENCE
    }

    Kind kind;

    private final List<Term> terms = new ArrayList<Term>();

    void normalize(List<Block> r, boolean optional) {
        switch(kind) {
        case SEQUENCE:
            for( Term t : terms )
                t.normalize(r,optional);
            return;
        case CHOICE:
            Block b = new Block(isOptional()||optional,isRepeated());
            addAllElements(b);
            r.add(b);
            return;
        }
    }

    void addAllElements(Block b) {
        for( Term t : terms )
            t.addAllElements(b);
    }

    boolean isOptional() {
        switch(kind) {
        case SEQUENCE:
            for( Term t : terms )
                if(!t.isOptional())
                    return false;
            return true;
        case CHOICE:
            for( Term t : terms )
                if(t.isOptional())
                    return true;
            return false;
        default:
            throw new IllegalArgumentException();
        }
    }

    boolean isRepeated() {
        switch(kind) {
        case SEQUENCE:
            return true;
        case CHOICE:
            for( Term t : terms )
                if(t.isRepeated())
                    return true;
            return false;
        default:
            throw new IllegalArgumentException();
        }
    }

    void setKind(short connectorType) {
        Kind k;
        switch(connectorType) {
        case DTDEventListener.SEQUENCE:
            k = Kind.SEQUENCE;
            break;
        case DTDEventListener.CHOICE:
            k = Kind.CHOICE;
            break;
        default:
            throw new IllegalArgumentException();
        }

        assert kind==null || k==kind;
        kind = k;
    }

    void addTerm(Term t) {
        if (t instanceof ModelGroup) {
            ModelGroup mg = (ModelGroup) t;
            if(mg.kind==this.kind) {
                terms.addAll(mg.terms);
                return;
            }
        }
        terms.add(t);
    }


    Term wrapUp() {
        switch(terms.size()) {
        case 0:
            return EMPTY;
        case 1:
            assert kind==null;
            return terms.get(0);
        default:
            assert kind!=null;
            return this;
        }
    }

}
