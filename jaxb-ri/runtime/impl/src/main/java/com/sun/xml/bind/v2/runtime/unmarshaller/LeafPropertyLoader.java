/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.unmarshaller;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;

import org.xml.sax.SAXException;

/**
 * Unmarshals a text into a property of the parent element.
 *
 * @see ValuePropertyLoader
 * @author Kohsuke Kawaguchi
 */
public class LeafPropertyLoader extends Loader {

    private final TransducedAccessor xacc;

    public LeafPropertyLoader(TransducedAccessor xacc) {
        super(true);
        this.xacc = xacc;
    }

    public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
        try {
            xacc.parse(state.getPrev().getTarget(), text);
        } catch (AccessorException e) {
            handleGenericException(e,true);
        } catch (RuntimeException e) {
            handleParseConversionException(state,e);
        }
    }
}
