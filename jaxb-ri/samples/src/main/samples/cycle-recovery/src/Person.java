/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import jakarta.xml.bind.annotation.XmlRootElement;

import org.glassfish.jaxb.runtime.CycleRecoverable;

@XmlRootElement
public class Person implements CycleRecoverable {

    public int id;

    public String name;

    public Person parent;

    // this method is called by JAXB when a cycle is detected
    public Person onCycleDetected(CycleRecoverable.Context context) {
        // when a cycle is detected, let's just write out an ID
        Person replacement = new Person();
        replacement.id = this.id;
        return replacement;
    }
}
