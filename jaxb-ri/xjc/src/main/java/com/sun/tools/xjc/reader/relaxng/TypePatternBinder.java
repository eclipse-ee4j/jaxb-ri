/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
import java.util.Stack;

import com.sun.tools.rngom.digested.DAttributePattern;
import com.sun.tools.rngom.digested.DChoicePattern;
import com.sun.tools.rngom.digested.DDefine;
import com.sun.tools.rngom.digested.DListPattern;
import com.sun.tools.rngom.digested.DMixedPattern;
import com.sun.tools.rngom.digested.DOneOrMorePattern;
import com.sun.tools.rngom.digested.DOptionalPattern;
import com.sun.tools.rngom.digested.DPatternWalker;
import com.sun.tools.rngom.digested.DRefPattern;
import com.sun.tools.rngom.digested.DZeroOrMorePattern;

/**
 * Fumigate the named patterns that can be bound to inheritance.
 *
 * @author Kohsuke Kawaguchi
 */
final class TypePatternBinder extends DPatternWalker {
    private boolean canInherit;
    private final Stack<Boolean> stack = new Stack<Boolean>();

    /**
     * Patterns that are determined not to be bindable to inheritance.
     */
    private final Set<DDefine> cannotBeInherited = new HashSet<DDefine>();


    void reset() {
        canInherit = true;
        stack.clear();
    }

    public Void onRef(DRefPattern p) {
        if(!canInherit) {
            cannotBeInherited.add(p.getTarget());
        } else {
            // if the whole pattern is like "A,B", we can only inherit from
            // either A or B. For now, always derive from A.
            // it might be worthwhile to have a smarter binding logic where
            // we pick A and B based on their 'usefulness' --- by taking into
            // account how many other paterns are derived from those.
            canInherit = false;
        }
        return null;
    }

    /*
        Set the flag to false if we hit a pattern that cannot include
        a <ref> to be bound as an inheritance.

        All the following code are the same
    */
    public Void onChoice(DChoicePattern p) {
        push(false);
        super.onChoice(p);
        pop();
        return null;
    }

    public Void onAttribute(DAttributePattern p) {
        push(false);
        super.onAttribute(p);
        pop();
        return null;
    }

    public Void onList(DListPattern p) {
        push(false);
        super.onList(p);
        pop();
        return null;
    }

    public Void onMixed(DMixedPattern p) {
        push(false);
        super.onMixed(p);
        pop();
        return null;
    }

    public Void onOneOrMore(DOneOrMorePattern p) {
        push(false);
        super.onOneOrMore(p);
        pop();
        return null;
    }

    public Void onZeroOrMore(DZeroOrMorePattern p) {
        push(false);
        super.onZeroOrMore(p);
        pop();
        return null;
    }

    public Void onOptional(DOptionalPattern p) {
        push(false);
        super.onOptional(p);
        pop();
        return null;
    }

    private void push(boolean v) {
        stack.push(canInherit);
        canInherit = v;
    }

    private void pop() {
        canInherit = stack.pop();
    }
}
