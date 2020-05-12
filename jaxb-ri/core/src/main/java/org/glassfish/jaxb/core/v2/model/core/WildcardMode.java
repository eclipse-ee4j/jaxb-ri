/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.core;

/**
 * Mode of the wildcard.
 *
 * @author Kohsuke Kawaguchi
 */
public enum WildcardMode {
    STRICT(false,true), SKIP(true,false), LAX(true,true);

    public final boolean allowDom;
    public final boolean allowTypedObject;

    WildcardMode(boolean allowDom, boolean allowTypedObject) {
        this.allowDom = allowDom;
        this.allowTypedObject = allowTypedObject;
    }
}
