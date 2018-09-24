/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.unmarshaller;

/**
 * Pair of {@link Loader} and {@link Receiver}.
 *
 * Used by {@link StructureLoader}.
 * 
 * @author Kohsuke Kawaguchi
 */
public final class ChildLoader {
    public final Loader loader;
    public final Receiver receiver;

    public ChildLoader(Loader loader, Receiver receiver) {
        assert loader!=null;
        this.loader = loader;
        this.receiver = receiver;
    }
}
