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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;

public class Issue1505Test {
    
    private void checks(String test) throws ClassNotFoundException {
        checks(test, test);
    }
    
    private void checks(String expected, String test) throws ClassNotFoundException {
        JCodeModel model = new JCodeModel();
        JType type = model.parseType(test);
        assertEquals(expected, type.fullName());
    }

    @Test
    public void test1() throws ClassNotFoundException {
        checks("Map<K,Pair<X,Y>>");
    }

    @Test
    public void test2() throws ClassNotFoundException {
        checks("Map<Pair<X,Y>,V>");
    }

    @Test
    public void test3() throws ClassNotFoundException {
        checks("M<K,V<X>>");
    }

    @Test
    public void test4() throws ClassNotFoundException {
        checks("M<K<X>,V>");
    }
    
    @Test
    public void test5() throws ClassNotFoundException {
        checks("A<B,C>");
    }

    @Test
    public void test6() throws ClassNotFoundException {
        checks("M<K>");
    }

    @Test
    public void test7() throws ClassNotFoundException {
        checks("M<K<Q>>");
    }

    @Test
    public void test8() throws ClassNotFoundException {
        checks("M<K,V>");
    }

    @Test
    public void test9() throws ClassNotFoundException {
        checks("M<K,V<Q>>");
    }

    @Test
    @Ignore("Not supported")
    public void test10() throws ClassNotFoundException {
        checks("java.util.Map<K extends ?,V extends ?>");
    }

    @Test
    public void test11() throws ClassNotFoundException {
        checks("Map<Key,Value<Que>>");
    }

    @Test
    public void test12() throws ClassNotFoundException {
        checks("M<K<T>,V>");
    }

    @Test
    public void test13() throws ClassNotFoundException {
        checks("M<T,Q,R>");
    }

    @Test
    public void test14() throws ClassNotFoundException {
        checks("M<A,B<C,D<E>,F<G>>>");
    }

    @Test
    public void test15() throws ClassNotFoundException {
        checks("M<A,B[]<C[],D[]<E>,F<G[]>>>");
    }
    
    @Test
    public void test16() throws ClassNotFoundException {
        checks("M<? extends A,? extends B>");
    }

    @Test
    @Ignore("Not supported")
    public void test17() throws ClassNotFoundException {
        checks("M<A extends Object,B extends Object>");
    }

    @Test
    public void test18() throws ClassNotFoundException {
        checks("java.lang.Object");
    }

    @Test
    public void test19() throws ClassNotFoundException {
        checks("java.util.ArrayList<String>");
    }
}
