/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 *  Illustrates the use of
 *  @jakarta.xml.bind.annotation.XmlType.propOrder() to customize the
 *  ordering of properties.
 *
 *  $Id: USAddress.java,v 1.1 2007-12-05 00:49:36 kohsuke Exp $
 * 
 *  Author: Sekhar Vajjhala
 */  

import jakarta.xml.bind.annotation.XmlType;

@XmlType(propOrder={"name", "street", "city", "state",  "zip"})
public class USAddress {
    private java.math.BigDecimal zip;
    private String name;
    private String street;
    private String city;
    private String state;


    String getName() {
	return name;
    };

    void setName(String name) {
	this.name = name;
    }
 
    String getStreet() {
	return street;
    }

    void setStreet(String street) {
	this.street = street;
    };

    String getCity() {
	return city;
    }; 

    void setCity(String city) {
	this.city = city;
    }

    String getState() {
	return state;
    }

    void setState(String state) {
	this.state = state;
    }

     java.math.BigDecimal getZip() {
	 return zip;
     }

     void setZip(java.math.BigDecimal zip) {
	 this.zip = zip;
     }
 }
