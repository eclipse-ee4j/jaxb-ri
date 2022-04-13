/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect.opt;

import org.glassfish.jaxb.runtime.DatatypeConverterImpl;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.DefaultTransducedAccessor;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.TransducedAccessor;

/**
 * Template {@link TransducedAccessor} for a double field.
 * <p><b>
 *     Auto-generated, do not edit.
 * </b></p>
 * <p>
 *     All the TransducedAccessor_field are generated from <code>TransducedAccessor_field_Byte</code>
 * </p>
 * @author Kohsuke Kawaguchi
 *
 * @see TransducedAccessor#get
 */
@SuppressWarnings({"deprecation"})
public final class TransducedAccessor_field_Double<T> extends DefaultTransducedAccessor<T> {
    @Override
    public String print(T o) {
        return DatatypeConverterImpl._printDouble( ((Bean)o).f_double );
    }

    @Override
    public void parse(T o, CharSequence lexical) {
        ((Bean)o).f_double=DatatypeConverterImpl._parseDouble(lexical);
    }

    @Override
    public boolean hasValue(T o) {
        return true;
    }
}
