/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package address;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace="http://www.example.com/MYPO1")
public class USAddress {

    private String city;
    private String name;
    private String state;
    private String street;
    private int    zip;
    
    /**
     * The zero arg constructor is used by JAXB Unmarshaller to create
     * an instance of this type.
     */

    public USAddress() {}

    public USAddress(String name, String street, String city, String state, int zip) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        if(name!=null) s.append(name).append('\n');
        s.append(street).append('\n').append(city).append(", ").append(state).append(" ").append(zip);
        return s.toString();
    }
}

