/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;



/**
 * {@link Loader} implementation that discards the whole sub-tree.
 *
 * Mostly used for recovering fom errors.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class Discarder extends Loader {
    public static final Loader INSTANCE = new Discarder();

    private Discarder() {
        super(false);
    }

    @Override
    public void childElement(UnmarshallingContext.State state, TagName ea) {
        state.setTarget(null);
        // registering this allows the discarder to process the whole subtree.
        state.setLoader(this);
    }
}
