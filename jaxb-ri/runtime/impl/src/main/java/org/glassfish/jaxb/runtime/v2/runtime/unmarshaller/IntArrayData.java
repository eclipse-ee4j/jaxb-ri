/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * Typed {@link CharSequence} for int[].
 *
 * <p>
 * Fed to unmarshaller when the 'text' data is actually
 * a virtual image of int array.
 *
 * <p>
 * This class holds int[] as a triplet of (data,start,len)
 * where 'start' and 'len' represents the start position of the
 * data and the length.
 *
 * @author Kohsuke Kawaguchi
 */
public final class IntArrayData extends Pcdata {

    private int[] data;
    private int start;
    private int len;

    /**
     * String representation of the data. Lazily computed.
     */
    private StringBuilder literal;


    public IntArrayData(int[] data, int start, int len) {
        set(data, start, len);
    }

    public IntArrayData() {
    }

    /**
     * Sets the int[] data to this object.
     *
     * <p>
     * This method doesn't make a copy for a performance reason.
     * The caller is still free to modify the array it passed to this method,
     * but he should do so with a care. The unmarshalling code isn't expecting
     * the value to be changed while it's being routed.
     */
    public void set(int[] data, int start, int len) {
        this.data = data;
        this.start = start;
        this.len = len;
        this.literal = null;
    }

    @Override
    public int length() {
        return getLiteral().length();
    }

    @Override
    public char charAt(int index) {
        return getLiteral().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return getLiteral().subSequence(start,end);
    }

    /**
     * Computes the literal form from the data.
     */
    private StringBuilder getLiteral() {
        if(literal!=null)   return literal;

        literal = new StringBuilder();
        int p = start;
        for( int i=len; i>0; i-- ) {
            if(literal.length()>0)  literal.append(' ');
            literal.append(data[p++]);
        }

        return literal;
    }

    @Override
    public String toString() {
        return literal.toString();
    }

    @Override
    public void writeTo(UTF8XmlOutput output) throws IOException {
        int p = start;
        for( int i=len; i>0; i-- ) {
            if(i!=len)
                output.write(' ');
            output.text(data[p++]);
        }
    }
}
