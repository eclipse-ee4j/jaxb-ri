/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
final class PrimitiveArrayListerCharacter<BeanT> extends Lister<BeanT,char[],Character,PrimitiveArrayListerCharacter.CharacterArrayPack> {
    
    private PrimitiveArrayListerCharacter() {
    }

    /*package*/ static void register() {
        primitiveArrayListers.put(Character.TYPE,new PrimitiveArrayListerCharacter());
    }

    public ListIterator<Character> iterator(final char[] objects, XMLSerializer context) {
        return new ListIterator<Character>() {
            int idx=0;
            public boolean hasNext() {
                return idx<objects.length;
            }

            public Character next() {
                return objects[idx++];
            }
        };
    }

    public CharacterArrayPack startPacking(BeanT current, Accessor<BeanT, char[]> acc) {
        return new CharacterArrayPack();
    }

    public void addToPack(CharacterArrayPack objects, Character o) {
        objects.add(o);
    }

    public void endPacking( CharacterArrayPack pack, BeanT bean, Accessor<BeanT,char[]> acc ) throws AccessorException {
        acc.set(bean,pack.build());
    }

    public void reset(BeanT o,Accessor<BeanT,char[]> acc) throws AccessorException {
        acc.set(o,new char[0]);
    }

    static final class CharacterArrayPack {
        char[] buf = new char[16];
        int size;

        void add(Character b) {
            if(buf.length==size) {
                // realloc
                char[] nb = new char[buf.length*2];
                System.arraycopy(buf,0,nb,0,buf.length);
                buf = nb;
            }
            if(b!=null)
                buf[size++] = b;
        }

        char[] build() {
            if(buf.length==size)
                // if we are lucky enough
                return buf;

            char[] r = new char[size];
            System.arraycopy(buf,0,r,0,size);
            return r;
        }
    }
}
