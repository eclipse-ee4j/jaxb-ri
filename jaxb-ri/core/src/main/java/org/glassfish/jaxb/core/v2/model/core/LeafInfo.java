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
 * Either {@link BuiltinLeafInfo} or {@link EnumLeafInfo}.
 *
 * <p>
 * Those Java types are all mapped to a chunk of text, so we call
 * them "leaves".
 * This interface represents the mapping information for those
 * special Java types.
 *
 * @author Kohsuke Kawaguchi
 */
public interface LeafInfo<T,C> extends MaybeElement<T,C> {
}
