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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Kohsuke Kawaguchi
 */
final class Block {
    final boolean isOptional;
    final boolean isRepeated;

    /**
     * {@link Element}s that belong to this block.
     * <p>
     * We want to preserve the order they are added, but we don't want
     * dupliates.
     */
    final Set<Element> elements = new LinkedHashSet<Element>();

    Block(boolean optional, boolean repeated) {
        isOptional = optional;
        isRepeated = repeated;
    }
}
