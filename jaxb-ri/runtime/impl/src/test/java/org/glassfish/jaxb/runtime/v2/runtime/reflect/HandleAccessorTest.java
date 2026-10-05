/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import org.glassfish.jaxb.runtime.AccessorFactoryImpl;
import org.glassfish.jaxb.runtime.api.AccessorException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HandleAccessorTest {

    @Test
    void fieldHandleReadsAndWritesPrimitiveAndReferenceFields() throws Exception {
        Bean bean = new Bean();
        Field primitive = Bean.class.getDeclaredField("number");
        Field reference = Bean.class.getDeclaredField("text");

        Accessor<Bean, Integer> number = AccessorFactoryImpl.getInstance()
                .createFieldAccessor(Bean.class, primitive, false);
        Accessor<Bean, String> text = AccessorFactoryImpl.getInstance()
                .createFieldAccessor(Bean.class, reference, false);

        number.set(bean, 42);
        text.set(bean, "value");

        assertEquals(42, number.get(bean));
        assertEquals("value", text.get(bean));
    }

    @Test
    void methodHandlesPreservePrimitiveAndReferencePropertyAccess() throws Exception {
        Bean bean = new Bean();
        Method getter = Bean.class.getMethod("getCount");
        Method setter = Bean.class.getMethod("setCount", int.class);

        Accessor<Bean, Integer> count = AccessorFactoryImpl.getInstance()
                .createPropertyAccessor(Bean.class, getter, setter);
        count.set(bean, 17);

        assertEquals(17, count.get(bean));
    }

    @Test
    void methodHandlesWrapCheckedUserExceptionsLikeReflection() throws Exception {
        Method getter = Bean.class.getMethod("getFailingValue");
        Method setter = Bean.class.getMethod("setFailingValue", String.class);
        Accessor<Bean, String> value = AccessorFactoryImpl.getInstance()
                .createPropertyAccessor(Bean.class, getter, setter);

        AccessorException exception = assertThrows(AccessorException.class, () -> value.get(new Bean()));
        assertInstanceOf(IOException.class, exception.getCause());
    }

    public static class Bean {
        private int number;
        private String text;
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getFailingValue() throws IOException {
            throw new IOException("expected");
        }

        public void setFailingValue(String value) {
        }
    }
}
