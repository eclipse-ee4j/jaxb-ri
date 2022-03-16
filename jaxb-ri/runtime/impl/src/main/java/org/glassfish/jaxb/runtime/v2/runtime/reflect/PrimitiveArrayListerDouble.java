/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;

/**
 * {@link Lister} for primitive type arrays.
 * <p><b>
 *     Auto-generated, do not edit.
 * </b></p>
 * <p>
 *     B y t e ArrayLister is used as the master to generate the rest of the
 *     lister classes. Do not modify the generated copies.
 * </p>
 */
final class PrimitiveArrayListerDouble<BeanT> extends Lister<BeanT,double[],Double,PrimitiveArrayListerDouble.DoubleArrayPack> {
    
    private PrimitiveArrayListerDouble() {
    }

    /*package*/ static void register() {
        Lister.primitiveArrayListers.put(Double.TYPE,new PrimitiveArrayListerDouble());
    }

    @Override
    public ListIterator<Double> iterator(final double[] objects, XMLSerializer context) {
        return new ListIterator<>() {
            int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < objects.length;
            }

            @Override
            public Double next() {
                return objects[idx++];
            }
        };
    }

    @Override
    public DoubleArrayPack startPacking(BeanT current, Accessor<BeanT, double[]> acc) {
        return new DoubleArrayPack();
    }

    @Override
    public void addToPack(DoubleArrayPack objects, Double o) {
        objects.add(o);
    }

    @Override
    public void endPacking( DoubleArrayPack pack, BeanT bean, Accessor<BeanT,double[]> acc ) throws AccessorException {
        acc.set(bean,pack.build());
    }

    @Override
    public void reset(BeanT o,Accessor<BeanT,double[]> acc) throws AccessorException {
        acc.set(o,new double[0]);
    }

    static final class DoubleArrayPack {
        double[] buf = new double[16];
        int size;

        void add(Double b) {
            if(buf.length==size) {
                // realloc
                double[] nb = new double[buf.length*2];
                System.arraycopy(buf,0,nb,0,buf.length);
                buf = nb;
            }
            if(b!=null)
                buf[size++] = b;
        }

        double[] build() {
            if(buf.length==size)
                // if we are lucky enough
                return buf;

            double[] r = new double[size];
            System.arraycopy(buf,0,r,0,size);
            return r;
        }
    }
}
