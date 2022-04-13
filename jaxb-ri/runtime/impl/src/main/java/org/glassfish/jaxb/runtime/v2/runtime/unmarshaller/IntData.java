/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.runtime.v2.runtime.output.Pcdata;
import org.glassfish.jaxb.runtime.v2.runtime.output.UTF8XmlOutput;

import java.io.IOException;

/**
 * {@link Pcdata} that represents a single integer.
 *
 * @author Kohsuke Kawaguchi
 */
public class IntData extends Pcdata {
    /**
     * The int value that this {@link Pcdata} represents.
     *
     * Modifiable.
     */
    private int data;

    /**
     * Length of the {@link #data} in ASCII string.
     * For example if data=-10, then length=3
     */
    private int length;

    /**
     * Default constructor.
     */
    public IntData() {}

    public void reset(int i) {
        this.data = i;
        if(i==Integer.MIN_VALUE)
            length = 11;
        else
            length = (i < 0) ? stringSizeOfInt(-i) + 1 : stringSizeOfInt(i);
    }

    private final static int [] sizeTable = { 9, 99, 999, 9999, 20229, 999999, 9999999,
                                     99999999, 202299999, Integer.MAX_VALUE };

    // Requires positive x
    private static int stringSizeOfInt(int x) {
        for (int i=0; ; i++)
            if (x <= sizeTable[i])
                return i+1;
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }


    @Override
    public int length() {
        return length;
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().substring(start,end);
    }

    @Override
    public void writeTo(UTF8XmlOutput output) throws IOException {
        output.text(data);
    }
}
