/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.schemagen;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
