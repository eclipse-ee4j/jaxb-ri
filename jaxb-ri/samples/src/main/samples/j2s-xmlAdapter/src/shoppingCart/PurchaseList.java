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
 * $Id: PurchaseList.java,v 1.1 2007-12-05 00:49:28 kohsuke Exp $
 */

package shoppingCart;

import java.util.List;
import java.util.Vector;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name="PurchaseListType")
public class PurchaseList {
    //- this must be a public field for the adapter to function
    //- When it is public the generated xml uses the variable name
    //- as the element tag.
    //- If the entry is not public the generic identifier is used
    //- as the element tag.  Settter/getter methods would be
    //- needed.
    public List<PartEntry> entry;
    
    public PurchaseList(){
        entry = new Vector<PartEntry>();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        int cnt = entry.size();
        for(int i=0; i < cnt; i++){
            buf.append(entry.get(i).toString());
            buf.append("\n");
        }
        return buf.toString();
    }
}

