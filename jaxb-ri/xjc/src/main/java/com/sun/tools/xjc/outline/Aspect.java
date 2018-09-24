/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.outline;

import com.sun.tools.xjc.generator.bean.ImplStructureStrategy;

/**
 * Sometimes a single JAXB-generated bean spans across multiple Java classes/interfaces.
 * We call them "aspects of a bean".
 *
 * <p>
 * This is an enumeration of all possible aspects.
 *
 * @author Kohsuke Kawaguchi
 *
 * TODO: move this to the model package. We cannot do this before JAXB3 because of old plugins
 */
public enum Aspect {
    /**
     * The exposed part of the bean.
     * <p>
     * This corresponds to the content interface when we are geneting one.
     * This would be the same as the {@link #IMPLEMENTATION} when we are
     * just generating beans.
     *
     * <p>
     * This could be an interface, or it could be a class.
     *
     * We don't have any other {@link ImplStructureStrategy} at this point,
     * but generally you can't assume anything about where this could be
     * or whether that's equal to {@link #IMPLEMENTATION}.
     */
    EXPOSED,
    /**
     * The part of the bean that holds all the implementations.
     *
     * <p>
     * This is always a class, never an interface.
     */
    IMPLEMENTATION
}
