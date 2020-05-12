/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.annotation;

import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.runtime.Location;

import java.lang.annotation.Annotation;

/**
 * Base implementation of {@link Locatable} {@link Annotation}.
 *
 * <p>
 * Derived classes of this class is provided for annotations that are commonly
 * used in JAXB, to improve the performance of {@link LocatableAnnotation#create}.
 *
 * @author Kohsuke Kawaguchi
 */
public /*so that our code generator can refer to this class*/ abstract class Quick implements Annotation, Locatable, Location {
    private final Locatable upstream;

    protected Quick(Locatable upstream) {
        this.upstream = upstream;
    }

    /**
     * Gets the annotation object that this object is wrapping.
     */
    protected abstract Annotation getAnnotation();

    /**
     * Factory method to create a new instance of the same kind.
     * A {@link Quick} object also works as a factory of itself
     */
    protected abstract Quick newInstance( Locatable upstream, Annotation core );

    public final Location getLocation() {
        return this;
    }

    public final Locatable getUpstream() {
        return upstream;
    }

    public final String toString() {
        return getAnnotation().toString();
    }
}
