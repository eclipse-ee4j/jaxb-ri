/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import com.sun.tools.xjc.outline.Outline;

/**
 * Mark model components which does additional code generation.
 *
 * TODO: currently this is only used for enum xducers. Think about a way
 * to generalize this.
 *
 * TODO: is this a sensible abstraction? Who's responsible for registering
 * populatable components to the model? Isn't it better if the back end
 * just gives every component a chance to build it automatically?
 *
 * @author Kohsuke Kawaguchi
 */
public interface Populatable {
    public void populate( Model model, Outline context );
}
