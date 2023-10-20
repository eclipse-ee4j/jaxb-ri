/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd;

import java.util.List;

import com.sun.xml.dtdparser.DTDEventListener;


/**
 * @author Kohsuke Kawaguchi
 */
final class Occurrence extends Term {
    final Term term;
    final boolean isOptional;
    final boolean isRepeated;

    Occurrence(Term term, boolean optional, boolean repeated) {
        this.term = term;
        isOptional = optional;
        isRepeated = repeated;
    }

    static Term wrap( Term t, int occurrence ) {
        switch(occurrence) {
        case DTDEventListener.OCCURRENCE_ONCE:
            return t;
        case DTDEventListener.OCCURRENCE_ONE_OR_MORE:
            return new Occurrence(t,false,true);
        case DTDEventListener.OCCURRENCE_ZERO_OR_MORE:
            return new Occurrence(t,true,true);
        case DTDEventListener.OCCURRENCE_ZERO_OR_ONE:
            return new Occurrence(t,true,false);
        default:
            throw new IllegalArgumentException();
        }
    }

    @Override
    void normalize(List<Block> r, boolean optional) {
        if(isRepeated) {
            Block b = new Block(isOptional||optional,true);
            addAllElements(b);
            r.add(b);
        } else {
            term.normalize(r,optional||isOptional);
        }
    }

    @Override
    void addAllElements(Block b) {
        term.addAllElements(b);
    }

    @Override
    boolean isOptional() {
        return isOptional||term.isOptional();
    }

    @Override
    boolean isRepeated() {
        return isRepeated||term.isRepeated();
    }
}
