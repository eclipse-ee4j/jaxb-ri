/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.reflect.opt;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import com.sun.xml.bind.v2.bytecode.ClassTailor;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import org.junit.Test;

public class ForeignAccessorTest {

    /**
     * Foreign accessor shouldn't throw ClassCastException, just skip optimization.
     */
    @Test
    public void foreignAccessor() throws NoSuchFieldException{
        String newClassName = EntityWithForeignAccessor.class.getName().replace('.','/') + "$JaxbAccessorF_author";
        Class<?> foreignAccessor = AccessorInjector.prepare(EntityWithForeignAccessor.class,
                ClassTailor.toVMClassName(ForeignAccessorTest.FieldAccessor_Ref.class),
                newClassName);
        assertNotNull(foreignAccessor);

        Accessor<Object, Object> accessor = OptimizedAccessorFactory.get(EntityWithForeignAccessor.class.getDeclaredField("author"));
        assertNull(accessor);
    }
    
    @Test
    public void knownAccessor() throws NoSuchFieldException {
        Accessor<Object, Object> accessor = OptimizedAccessorFactory.get(EntityWithKnownAccessor.class.getDeclaredField("author"));
        assertNotNull(accessor);
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class EntityWithKnownAccessor {
        String author;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class EntityWithForeignAccessor {
        String author;
    }

    /**
     * Test template doesn't extend accessor intentionally.
     */
    public static class FieldAccessor_Ref /*extends Accessor*/ {
        public FieldAccessor_Ref() {
           // super(Ref.class);
        }

        public Object get(Object bean) {
            return ((Bean)bean).f_ref;
        }

        public void set(Object bean, Object value) {
            ((Bean)bean).f_ref = (Ref)value;
        }
    }
}
