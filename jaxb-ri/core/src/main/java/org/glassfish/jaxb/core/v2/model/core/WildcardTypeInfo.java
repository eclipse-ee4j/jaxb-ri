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
 * Type referenced as a result of having the wildcard.
 *
 * TODO: think about how to gracefully handle the difference between LAX,SKIP, and STRICT.
 *
 * @author Kohsuke Kawaguchi
 */
public interface WildcardTypeInfo<T,C> extends TypeInfo<T,C> {
}
