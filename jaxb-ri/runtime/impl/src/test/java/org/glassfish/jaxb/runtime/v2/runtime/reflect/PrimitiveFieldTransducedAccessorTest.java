/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import org.glassfish.jaxb.runtime.v2.model.impl.RuntimeBuiltinLeafInfoImpl;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrimitiveFieldTransducedAccessorTest {

    public static class Bean {
        public int i;
        public long l;
        public boolean z;
        public double d;
        public float f;
        public short s;
        public byte b;
        public char c;
        public Integer boxed;
        public String text;
    }

    private static Transducer<?> builtin(Class<?> type) {
        return RuntimeBuiltinLeafInfoImpl.LEAVES.get(type);
    }

    private static TransducedAccessor<Bean> create(String field, Class<?> box) throws Exception {
        return PrimitiveFieldTransducedAccessor.create(builtin(box),
                new Accessor.FieldReflection<>(Bean.class.getField(field)));
    }

    @Test
    public void selection() throws Exception {
        for (String[] p : new String[][] {{"i", "java.lang.Integer"}, {"l", "java.lang.Long"},
                {"z", "java.lang.Boolean"}, {"d", "java.lang.Double"}, {"f", "java.lang.Float"},
                {"s", "java.lang.Short"}, {"b", "java.lang.Byte"}}) {
            assertNotNull(create(p[0], Class.forName(p[1])), p[0]);
        }
        // not primitive, or not the built-in transducer of the wrapper type
        assertNull(create("boxed", Integer.class));
        assertNull(create("text", String.class));
        assertNull(create("i", Long.class));
        assertNull(PrimitiveFieldTransducedAccessor.create(builtin(Character.class),
                new Accessor.FieldReflection<>(Bean.class.getField("c"))));
        // any accessor other than plain field reflection keeps the generic path
        assertNull(PrimitiveFieldTransducedAccessor.create(builtin(Integer.class),
                new Accessor.ReadOnlyFieldReflection<>(Bean.class.getField("i"))));
    }

    @Test
    public void conversionsMatchTheBuiltinTransducers() throws Exception {
        Bean bean = new Bean();
        TransducedAccessor<Bean> i = create("i", Integer.class);
        for (String lexical : new String[] {"0", "-0", "+7", " 12 ", "2147483647", "-2147483648", "\t-5\n"}) {
            i.parse(bean, new StringBuilder(lexical));
            assertEquals(builtin(Integer.class).parse(lexical), bean.i, lexical);
            assertEquals(((Transducer<Integer>) builtin(Integer.class)).print(bean.i), i.print(bean));
        }
        assertThrows(NumberFormatException.class, () -> i.parse(bean, "1x"));

        TransducedAccessor<Bean> l = create("l", Long.class);
        l.parse(bean, " 9223372036854775807 ");
        assertEquals(Long.MAX_VALUE, bean.l);
        assertEquals("9223372036854775807", l.print(bean));
        assertThrows(NumberFormatException.class, () -> l.parse(bean, "x"));

        TransducedAccessor<Bean> z = create("z", Boolean.class);
        for (String lexical : new String[] {"true", "1", " true ", "false", "0", "yes", "TRUE"}) {
            bean.z = !bean.z;
            z.parse(bean, lexical);
            Object expected = builtin(Boolean.class).parse(lexical);
            assertEquals(expected == null ? Boolean.FALSE : expected, bean.z, lexical);
        }

        TransducedAccessor<Bean> d = create("d", Double.class);
        for (String lexical : new String[] {"1.5", "-0.0", "INF", "-INF", "NaN", " 1e300 ", "4.9E-324"}) {
            d.parse(bean, lexical);
            assertEquals(builtin(Double.class).parse(lexical), bean.d, lexical);
            assertEquals(((Transducer<Double>) builtin(Double.class)).print(bean.d), d.print(bean));
        }

        TransducedAccessor<Bean> f = create("f", Float.class);
        f.parse(bean, "3.25");
        assertEquals(3.25f, bean.f);
        assertEquals("3.25", f.print(bean));

        TransducedAccessor<Bean> s = create("s", Short.class);
        s.parse(bean, "-32768");
        assertEquals(Short.MIN_VALUE, bean.s);
        TransducedAccessor<Bean> b = create("b", Byte.class);
        b.parse(bean, "127");
        assertEquals(Byte.MAX_VALUE, bean.b);
        assertEquals("127", b.print(bean));
    }
}
