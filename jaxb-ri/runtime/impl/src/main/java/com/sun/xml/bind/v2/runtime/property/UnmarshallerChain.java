/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.unmarshaller.Scope;


/**
 * Pass around a 'ticket dispenser' when creating new
 * unmarshallers. This controls the index of the slot
 * allocated to the chain of handlers.

 *
 * <p>
 * A ticket dispenser also maintains the offset for handlers
 * to access state slots. A handler records this value when it's created.
 * 
 *
 */
public final class UnmarshallerChain {
    /**
     * This offset allows child unmarshallers to have its own {@link Scope} without colliding with siblings.
     */
    private int offset = 0;

    public final JAXBContextImpl context;

    public UnmarshallerChain(JAXBContextImpl context) {
        this.context = context;
    }

    /**
     * Allocates a new {@link Scope} offset.
     */
    public int allocateOffset() {
        return offset++;
    }

    /**
     * Gets the number of total scope offset allocated.
     */
    public int getScopeSize() {
        return offset;
    }
}

