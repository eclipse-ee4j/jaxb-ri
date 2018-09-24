/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package cardfile;

import javax.xml.bind.annotation.*;

@XmlRootElement
public class BusinessCard {

    private String name;
    private String title;
    private String company;
    private Address address;
    private String phone;
    private String cellPhone;
    private String fax;
    private String email;

    public BusinessCard() {}

    public BusinessCard(String name, String title, String company, Address address,
                        String phone, String cellPhone, String fax, String email) {
        this.name = name;
        this.title = title;
        this.company = company;
        this.address = address;
        this.phone = phone;
        this.cellPhone = cellPhone;
        this.fax = fax;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        if(name!=null) s.append(name).append('\n');
        if(title!=null) s.append(title).append('\n');
        if(company!=null) s.append(company).append('\n');
        if(address!=null) s.append(address.toString()).append('\n');
        if(phone!=null) s.append("phone: ").append(phone).append('\n');
        if(cellPhone!=null) s.append("cell:  ").append(cellPhone).append('\n');
        if(fax!=null) s.append("fax:  ").append(fax).append('\n');
        if(email!=null) s.append(email).append('\n');
        return s.toString();
    }
}
