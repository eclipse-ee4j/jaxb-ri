/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package t1;

import com.sun.xml.txw2.TXW;
import com.sun.xml.txw2.output.DumpSerializer;

import javax.xml.namespace.QName;

/**
 * @author Kohsuke Kawaguchi
 */
public class Main {
    public static void main(String[] args) {
        Foo root = TXW.create(Foo.class,new DumpSerializer(System.out));

        root.hello(5);
        root.foo();
        
        root.commit();
    }
}
