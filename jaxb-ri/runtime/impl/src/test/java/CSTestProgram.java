/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

/**
 * Encoding test chart.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class CSTestProgram {

    public static void main(String[] args) {
        test("UTF-8");
        test("UTF-16");
        test("iso-2022-jp");
        test("iso2022jp");
        test("shift_jis");
        test("shift-jis");
        test("euc-jp");
        
        System.out.println("\n\n");
        Map m = Charset.availableCharsets();
        for( Iterator itr=m.keySet().iterator(); itr.hasNext(); ) {
        	System.out.println(itr.next());
        	
        }
    }

    private static void test(String name) {
        try {
            Charset cs = Charset.forName(name);
            System.out.println(name+" is suppoted. canonical="+cs);
        } catch( Throwable e ) {
            System.out.println(name+" is not supported");
        }
    }
}
