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
import com.sun.xml.bind.v2.runtime.Transducer;

import org.xml.sax.SAXException;

/**
 * Unmarshals a text into an object.
 *
 * <p>
 * If the caller can use {@link LeafPropertyLoader}, that's usually faster.
 *
 * @see LeafPropertyLoader
 * @see ValuePropertyLoader
 * @author Kohsuke Kawaguchi
 */
public class TextLoader extends Loader {

    private final Transducer xducer;

    public TextLoader(Transducer xducer) {
        super(true);
        this.xducer = xducer;
    }

    public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
        try {
            state.setTarget(xducer.parse(text));
        } catch (AccessorException e) {
            handleGenericException(e,true);
        } catch (RuntimeException e) {
            handleParseConversionException(state,e);
        }
    }
}
