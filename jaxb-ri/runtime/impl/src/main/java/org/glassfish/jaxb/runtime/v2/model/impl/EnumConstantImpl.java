/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.core.v2.model.core.EnumConstant;
import org.glassfish.jaxb.core.v2.model.core.EnumLeafInfo;

/**
 * @author Kohsuke Kawaguchi
 */
class EnumConstantImpl<T,C,F,M> implements EnumConstant<T,C> {
    protected final String lexical;
    protected final EnumLeafInfoImpl<T,C,F,M> owner;
    protected final String name;

    /**
     * All the constants of the {@link EnumConstantImpl} is linked in one list.
     */
    protected final EnumConstantImpl<T,C,F,M> next;

    public EnumConstantImpl(EnumLeafInfoImpl<T,C,F,M> owner, String name, String lexical, EnumConstantImpl<T,C,F,M> next) {
        this.lexical = lexical;
        this.owner = owner;
        this.name = name;
        this.next = next;
    }

    public EnumLeafInfo<T,C> getEnclosingClass() {
        return owner;
    }

    public final String getLexicalValue() {
        return lexical;
    }

    public final String getName() {
        return name;
    }
}
