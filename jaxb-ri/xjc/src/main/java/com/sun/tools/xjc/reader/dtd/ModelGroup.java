/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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

    private final List<Term> terms = new ArrayList<>();

    @Override
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
        }
    }

    @Override
    void addAllElements(Block b) {
        for( Term t : terms )
            t.addAllElements(b);
    }

    @Override
    boolean isOptional() {
        return switch (kind) {
            case SEQUENCE -> {
                for (Term t : terms)
                    if (!t.isOptional())
                        yield false;
                yield true;
            }
            case CHOICE -> {
                for (Term t : terms)
                    if (t.isOptional())
                        yield true;
                yield false;
            }
        };
    }

    @Override
    boolean isRepeated() {
        return switch (kind) {
            case SEQUENCE -> true;
            case CHOICE -> {
                for (Term t : terms)
                    if (t.isRepeated())
                        yield true;
                yield false;
            }
        };
    }

    void setKind(short connectorType) {
        Kind k = switch (connectorType) {
            case DTDEventListener.SEQUENCE -> Kind.SEQUENCE;
            case DTDEventListener.CHOICE -> Kind.CHOICE;
            default -> throw new IllegalArgumentException();
        };

        assert kind==null || k==kind;
        kind = k;
    }

    void addTerm(Term t) {
        if (t instanceof ModelGroup mg) {
            if(mg.kind==this.kind) {
                terms.addAll(mg.terms);
                return;
            }
        }
        terms.add(t);
    }


    Term wrapUp() {
        return switch (terms.size()) {
            case 0 -> EMPTY;
            case 1 -> {
                assert kind == null;
                yield terms.get(0);
            }
            default -> {
                assert kind != null;
                yield this;
            }
        };
    }

}
