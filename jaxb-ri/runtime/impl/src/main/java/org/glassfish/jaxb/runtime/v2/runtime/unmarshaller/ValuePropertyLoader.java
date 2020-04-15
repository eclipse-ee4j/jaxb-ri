/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.TransducedAccessor;
import jakarta.xml.bind.JAXBElement;
import org.xml.sax.SAXException;

/**
 * Reads a text value and set to the current target.
 *
 * @see LeafPropertyLoader
 * @author Kohsuke Kawaguchi
 */
public class ValuePropertyLoader extends Loader {

    private final TransducedAccessor xacc;

    public ValuePropertyLoader(TransducedAccessor xacc) {
        super(true);
        this.xacc = xacc;
    }

    public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
        try {
            xacc.parse(state.getTarget(),text);
        } catch (AccessorException e) {
            handleGenericException(e,true);
        } catch (RuntimeException e) {
            if(state.getPrev() != null) {
                if (!(state.getPrev().getTarget() instanceof JAXBElement))
                    handleParseConversionException(state,e);
                // else
                // do nothing - issue 601 - don't report exceptions like
                // NumberFormatException when unmarshalling "nillable" element
                // (I suppose JAXBElement indicates this
            } else {
                handleParseConversionException(state,e);
            }
        }
    }
}

