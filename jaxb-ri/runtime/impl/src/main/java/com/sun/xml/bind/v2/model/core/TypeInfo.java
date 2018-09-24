/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.core;

import javax.xml.bind.annotation.XmlIDREF;

import com.sun.xml.bind.v2.model.annotation.Locatable;


/**
 * Either {@link ClassInfo}, {@link ElementInfo}, or {@link LeafInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface TypeInfo<T,C> extends Locatable {

    /**
     * Gets the underlying Java type that object represents.
     *
     * @return
     *      always non-null.
     */
    T getType();

    /**
     * True if this type is a valid target from a property annotated with {@link XmlIDREF}.
     */
    boolean canBeReferencedByIDREF();
}
