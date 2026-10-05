/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0,
 * or the BSD 3-Clause license.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.perf;

import org.glassfish.jaxb.runtime.AccessorFactoryImpl;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class AccessorBenchmark {
    private final Bean bean = new Bean();
    private Accessor<Bean, Integer> reflectedField;
    private Accessor<Bean, Integer> varHandleField;
    private Accessor<Bean, Integer> reflectedProperty;
    private Accessor<Bean, Integer> methodHandleProperty;

    @Setup
    public void setup() throws Exception {
        Field field = Bean.class.getDeclaredField("fieldValue");
        Method getter = Bean.class.getMethod("getPropertyValue");
        Method setter = Bean.class.getMethod("setPropertyValue", int.class);
        reflectedField = new Accessor.FieldReflection<>(field);
        varHandleField = new Accessor.FieldVarHandle<>(field);
        reflectedProperty = new Accessor.GetterSetterReflection<>(getter, setter);
        methodHandleProperty = new Accessor.GetterSetterMethodHandle<>(getter, setter);

        if (!reflectedField.get(bean).equals(varHandleField.get(bean))
                || !reflectedProperty.get(bean).equals(methodHandleProperty.get(bean))) {
            throw new IllegalStateException("Accessor implementations disagree");
        }
    }

    @Benchmark
    public Integer fieldGetReflection() throws Exception {
        return reflectedField.get(bean);
    }

    @Benchmark
    public Integer fieldGetVarHandle() throws Exception {
        return varHandleField.get(bean);
    }

    @Benchmark
    public void fieldSetReflection() throws Exception {
        reflectedField.set(bean, 42);
    }

    @Benchmark
    public void fieldSetVarHandle() throws Exception {
        varHandleField.set(bean, 42);
    }

    @Benchmark
    public Integer propertyGetReflection() throws Exception {
        return reflectedProperty.get(bean);
    }

    @Benchmark
    public Integer propertyGetMethodHandle() throws Exception {
        return methodHandleProperty.get(bean);
    }

    @Benchmark
    public void propertySetReflection() throws Exception {
        reflectedProperty.set(bean, 42);
    }

    @Benchmark
    public void propertySetMethodHandle() throws Exception {
        methodHandleProperty.set(bean, 42);
    }

    public static final class Bean {
        private int fieldValue = 42;
        private int propertyValue = 42;

        public int getPropertyValue() {
            return propertyValue;
        }

        public void setPropertyValue(int propertyValue) {
            this.propertyValue = propertyValue;
        }
    }
}
