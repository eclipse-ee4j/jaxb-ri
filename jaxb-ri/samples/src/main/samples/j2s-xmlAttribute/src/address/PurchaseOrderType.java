/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package address;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

@XmlRootElement(name="purchaseOrder")
@XmlType(name="PurchaseOrderType")
public class PurchaseOrderType {

    public USAddress shipTo;
    public USAddress billTo;
    @XmlAttribute
    public CreditCardVendor cCardVendor;
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Ship To: ").append(shipTo).append('\n');
        s.append("Bill To: ").append(billTo).append('\n');
        s.append("Card: ").append(cCardVendor).append('\n');
        return s.toString();
    }
}

