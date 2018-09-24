/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.tools.xjc.reader;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 *
 * @author snajper
 */
public class RingJUTest extends TestCase {

    public static void main(String[] args) {
        TestRunner.run(RingJUTest.class);
    }

    public void test1() throws InterruptedException {
        Ring r = Ring.get();
        assertNull(r);
        r = Ring.begin();
        assertNull(r);
        Ring.end(r);
        r = Ring.begin();
        assertNull(r);
        Ring.end(r);
        r = Ring.get();
        assertNull(r);
    }
    
}
