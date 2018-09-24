/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model;

import com.sun.tools.rngom.ast.builder.GrammarSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

/**
 * Represents a RELAX NG grammar.
 *
 * A {@link Grammar} extends a {@link Define} as "start"
 *
 * @author Kohsuke Kawaguchi
 */
public class Grammar extends Define {
    private final Map<String,Define> patterns = new HashMap<String,Define>();

    public Grammar() {
        super(null,START);
        patterns.put(START,this);
    }

    public Define get(String name) {
        Define def = patterns.get(name);
        if(def==null)
            patterns.put(name,def=new Define(this,name));
        return def;
    }

    public Collection<Define> getDefinitions() {
        return patterns.values();
    }

    /**
     * The name for the start pattern
     */
    public static final String START = GrammarSection.START;
}
