/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.schemagen;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "nsParent", namespace = "http://a.b.c/services")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nsParent", namespace = "http://a.b.c/services")
public class NSParent {

    @XmlElement(name = "myRef", namespace = "http://a.b.c/data")
    private MyRef myRef;

    public MyRef getMyRef() {
        return this.myRef;
    }

    public void setMyRef(MyRef myRef) {
        this.myRef = myRef;
    }

}
