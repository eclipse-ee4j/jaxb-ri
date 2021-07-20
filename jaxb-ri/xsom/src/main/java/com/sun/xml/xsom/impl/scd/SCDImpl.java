/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl.scd;

import com.sun.xml.xsom.SCD;
import com.sun.xml.xsom.XSComponent;

import java.util.Iterator;

/**
 * Schema component designator.
 *
 * @author Kohsuke Kawaguchi
 */
public final class SCDImpl extends SCD {
    /**
     * SCD is fundamentally a list of steps.
     */
    private final Step[] steps;

    /**
     * The original textual SCD representation.
     */
    private final String text;

    public SCDImpl(String text, Step[] steps) {
        this.text = text;
        this.steps = steps;
    }

    public Iterator<XSComponent> select(Iterator<? extends XSComponent> contextNode) {
        Iterator<XSComponent> nodeSet = (Iterator)contextNode;

        int len = steps.length;
        for( int i=0; i<len; i++ ) {
            if(i!=0 && i!=len-1 && !steps[i-1].axis.isModelGroup() && steps[i].axis.isModelGroup()) {
                // expand the current nodeset by adding abbreviatable complex type and model groups.
                // note that such expansion is not allowed to occure in in between model group axes.

                // TODO: this step is not needed if the next step is known not to react to
                // complex type nor model groups, such as, say Axis.FACET
                nodeSet = new Iterators.Unique<XSComponent>(
                    new Iterators.Map<XSComponent,XSComponent>(nodeSet) {
                        protected Iterator<XSComponent> apply(XSComponent u) {
                            return new Iterators.Union<XSComponent>(
                                Iterators.singleton(u),
                                Axis.INTERMEDIATE_SKIP.iterator(u) );
                        }
                    }
                );
            }
            nodeSet = steps[i].evaluate(nodeSet);
        }

        return nodeSet;
    }

    public String toString() {
        return text;
    }
}
