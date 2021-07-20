/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * $Id: KitchenWorldBasket.java,v 1.1 2007-12-05 00:49:28 kohsuke Exp $
 */

package shoppingCart;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name="KitchenWorldBasketType")
public class KitchenWorldBasket {
    @XmlJavaTypeAdapter(AdapterPurchaseListToHashMap.class)
    HashMap basket = new HashMap();
    
    public KitchenWorldBasket(){}
    public String toString(){
        StringBuilder buf = new StringBuilder();
        buf.append("KitchenWorldBasket:\n");
        
        // For QA consistency order the output. 
        TreeMap tMap = new TreeMap(basket);
        for(Iterator i=tMap.keySet().iterator(); i.hasNext();){
            Integer key = (Integer)i.next();
            buf.append("key: " + key.toString() + "\tvalue: " + tMap.get(key) +"\n");
        }     
        return buf.toString();
    }
}

