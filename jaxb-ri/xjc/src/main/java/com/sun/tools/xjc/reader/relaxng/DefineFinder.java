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

import java.util.HashSet;
import java.util.Set;

import com.sun.tools.rngom.digested.DDefine;
import com.sun.tools.rngom.digested.DGrammarPattern;
import com.sun.tools.rngom.digested.DPatternWalker;
import com.sun.tools.rngom.digested.DRefPattern;

/**
 * Recursively find all {@link DDefine}s in the grammar.
 *
 * @author Kohsuke Kawaguchi
 */
final class DefineFinder extends DPatternWalker {

    public final Set<DDefine> defs = new HashSet<DDefine>();

    public Void onGrammar(DGrammarPattern p) {
        for( DDefine def : p ) {
            defs.add(def);
            def.getPattern().accept(this);
        }

        return p.getStart().accept(this);
    }

    /**
     * We visit all {@link DDefine}s from {@link DGrammarPattern},
     * so no point in resolving refs.
     */
    public Void onRef(DRefPattern p) {
        return null;
    }
}
