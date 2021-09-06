/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

public class Issue1505Test {

    private void checks(String test) {
        checks(test, test);
    }

    private void checks(String expected, String test) {
        JCodeModel model = new JCodeModel();
        try {
            JType type = model.parseType(test);
            assertEquals(expected, type.fullName());
        } catch (ClassNotFoundException e) {
            fail(e.getMessage());
        } 
    }

    @Test
    public void test1() {
        checks("Map<K,Pair<X,Y>>");
    }

    @Test
    public void test2() {
        checks("Map<Pair<X,Y>,V>");
    }

    @Test
    public void test3() {
        checks("M<K,V<X>>");
    }

    @Test
    public void test4() {
        checks("M<K<X>,V>");
    }

    @Test
    public void test5() {
        checks("A<B,C>");
    }

    @Test
    public void test6() {
        checks("M<K>");
    }

    @Test
    public void test7() {
        checks("M<K<Q>>");
    }

    @Test
    public void test8() {
        checks("M<K,V>");
    }

    @Test
    public void test9() {
        checks("M<K,V<Q>>");
    }

    @Test
    @Ignore("Not supported")
    public void test10() {
        checks("java.util.Map<K extends ?,V extends ?>");
    }

    @Test
    public void test11() {
        checks("Map<Key,Value<Que>>");
    }

    @Test
    public void test12() {
        checks("M<K<T>,V>");
    }

    @Test
    public void test13() {
        checks("M<T,Q,R>");
    }

    @Test
    public void test14() {
        checks("M<A,B<C,D<E>,F<G>>>");
    }

    @Test
    public void test15() {
        checks("M<A,B[]<C[],D[]<E>,F<G[]>>>");
    }

    @Test
    public void test16() {
        checks("M<? extends A,? extends B>");
    }

    @Test
    @Ignore("Not supported")
    public void test17() {
        checks("M<A extends Object,B extends Object>");
    }

    @Test
    public void test18() {
        checks("java.lang.Object");
    }

    @Test
    public void test19() {
        checks("java.util.ArrayList<String>");
    }
}
