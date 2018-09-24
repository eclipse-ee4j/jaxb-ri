/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime;


import javax.activation.DataHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * Marker class used to identify swaref attachments and to generate appropriate annotations later.
 */
public class SwaRefAdapterMarker extends XmlAdapter<String, DataHandler> {

    public DataHandler unmarshal(String v) throws Exception {
        throw new IllegalStateException("Not implemented");
    }

    public String marshal(DataHandler v) throws Exception {
        throw new IllegalStateException("Not implemented");
    }
}
