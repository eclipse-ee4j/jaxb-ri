/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.acme;

import javax.xml.bind.annotation.XmlIDREF;

/**
 * @author Kohsuke Kawaguchi
 */
public class Foo {
    // in this bean we bind fields, just to show that we can.

    public int a;
    public String b;

    /**
     * JAXB will inject the proper bean here as configured in XML.
     */
    @XmlIDREF
    public Object c;

    public String toString() {
        return "Foo[a="+a+",b="+b+",c="+c+"]";
    }
}
