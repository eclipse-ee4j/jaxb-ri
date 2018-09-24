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

/**
 * Sink node of a grpah.
 * @author Kohsuke Kawaguchi
 */
public final class SinkNode extends Element {
    public String toString() {
        return "#sink";
    }

    @Override
    boolean isSink() {
        return true;
    }
}
