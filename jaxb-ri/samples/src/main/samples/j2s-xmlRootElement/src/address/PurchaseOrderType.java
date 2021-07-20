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
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name="purchaseOrder",namespace="http://www.example.com/MYPO1")
@XmlType(name="PurchaseOrderType")
public class PurchaseOrderType {

    public USAddress shipTo;
    public USAddress billTo;
    public CreditCardVendor creditCardVendor;
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Ship To: ");
        s.append(shipTo.toString()).append('\n');
        s.append("Bill To: ");
        return s.toString();
    }
}

