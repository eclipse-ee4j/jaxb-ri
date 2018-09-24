/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: PurchaseOrderType.java,v 1.1 2007-12-05 00:49:32 kohsuke Exp $
 */


package address;

import java.util.List;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="purchaseOrder")
@XmlType(name="PurchaseOrderType")
public class PurchaseOrderType {

    public USAddress shipTo;
    public USAddress billTo;
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Ship To: ");
        s.append(shipTo.toString()).append('\n');
        s.append("Bill To: ");
        s.append(billTo.toString()).append('\n');
        return s.toString();
    }
}

