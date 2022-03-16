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
 *
 * <p>
 * B y t e ArrayLister is used as the master to generate the rest of the
 * lister classes. Do not modify the generated copies.
 */
final class PrimitiveArrayListerByte<BeanT> extends Lister<BeanT,byte[],Byte,PrimitiveArrayListerByte.ByteArrayPack> {
    
    private PrimitiveArrayListerByte() {
    }

    /*package*/ static void register() {
        primitiveArrayListers.put(Byte.TYPE,new PrimitiveArrayListerByte());
    }

    @Override
    public ListIterator<Byte> iterator(final byte[] objects, XMLSerializer context) {
        return new ListIterator<>() {
            int idx = 0;

            @Override
            public boolean hasNext() {
                return idx < objects.length;
            }

            @Override
            public Byte next() {
                return objects[idx++];
            }
        };
    }

    @Override
    public ByteArrayPack startPacking(BeanT current, Accessor<BeanT, byte[]> acc) {
        return new ByteArrayPack();
    }

    @Override
    public void addToPack(ByteArrayPack objects, Byte o) {
        objects.add(o);
    }

    @Override
    public void endPacking( ByteArrayPack pack, BeanT bean, Accessor<BeanT,byte[]> acc ) throws AccessorException {
        acc.set(bean,pack.build());
    }

    @Override
    public void reset(BeanT o,Accessor<BeanT,byte[]> acc) throws AccessorException {
        acc.set(o,new byte[0]);
    }

    static final class ByteArrayPack {
        byte[] buf = new byte[16];
        int size;

        void add(Byte b) {
            if(buf.length==size) {
                // realloc
                byte[] nb = new byte[buf.length*2];
                System.arraycopy(buf,0,nb,0,buf.length);
                buf = nb;
            }
            if(b!=null)
                buf[size++] = b;
        }

        byte[] build() {
            if(buf.length==size)
                // if we are lucky enough
                return buf;

            byte[] r = new byte[size];
            System.arraycopy(buf,0,r,0,size);
            return r;
        }
    }
}
