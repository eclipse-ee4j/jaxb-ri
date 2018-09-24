/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.gbind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Graph of {@link Element}s.
 *
 * @author Kohsuke Kawaguchi
 */
public final class Graph implements Iterable<ConnectedComponent> {
    private final Element source = new SourceNode();
    private final Element sink = new SinkNode();

    /**
     * Strongly connected components of this graph.
     */
    private final List<ConnectedComponent> ccs = new ArrayList<ConnectedComponent>();

    /**
     * Builds a {@link Graph} from an {@link Expression} tree.
     *
     * {@link Expression} given to the graph will be modified forever,
     * and it will not be able to create another {@link Graph}.
     */
    public Graph(Expression body) {
        // attach source and sink
        Expression whole = new Sequence(new Sequence(source,body),sink);

        // build up a graph
        whole.buildDAG(ElementSet.EMPTY_SET);

        // decompose into strongly connected components.
        // the algorithm is standard DFS-based algorithm,
        // one illustration of this algorithm is available at
        // http://www.personal.kent.edu/~rmuhamma/Algorithms/MyAlgorithms/GraphAlgor/strongComponent.htm
        source.assignDfsPostOrder(sink);
        source.buildStronglyConnectedComponents(ccs);

        // cut-set check
        Set<Element> visited = new HashSet<Element>();
        for (ConnectedComponent cc : ccs) {
            visited.clear();
            if(source.checkCutSet(cc,visited)) {
                cc.isRequired = true;
            }
        }
    }

    /**
     * List up {@link ConnectedComponent}s of this graph in an order.
     */
    public Iterator<ConnectedComponent> iterator() {
        return ccs.iterator();
    }

    public String toString() {
        return ccs.toString();
    }
}
