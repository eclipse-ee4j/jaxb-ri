/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model.prop;

/**
 * Immutable object that captures the characterstic
 * of the generated writer method.
 *
 * <p>
 * Instances of this class implement {@link #equals(Object)}
 * and {@link #hashCode()}. By using these we avoid generating
 * the same method twice.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Prop {
}
