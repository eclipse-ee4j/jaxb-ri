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
 * $Id: AdapterPurchaseListToHashMap.java,v 1.1 2007-12-05 00:49:28 kohsuke Exp $
 */


package shoppingCart;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/*
 *
 *  PurchaseList - ValueType
 *  HashMap - BoundType
 */
public class AdapterPurchaseListToHashMap extends XmlAdapter<PurchaseList, HashMap> {
    public AdapterPurchaseListToHashMap(){}
    
    // Convert a value type to a bound type.
    // read xml content and put into Java class.
    public HashMap unmarshal(PurchaseList v){        
       HashMap aHashMap = new HashMap();
       int cnt = v.entry.size();
       for(int i=0; i < cnt; i++){
            PartEntry tmpE = (PartEntry)v.entry.get(i);
            aHashMap.put(new Integer(tmpE.key), tmpE.value);
        } 
       return aHashMap;
    }
    
    // Convert a bound type to a value type.
    // write Java content into class that generates desired XML 
    public PurchaseList marshal(HashMap v){
        PurchaseList pList = new PurchaseList();
        // For QA consistency order the output. 
        TreeMap tMap = new TreeMap(v);
        for(Iterator i=tMap.keySet().iterator(); i.hasNext();){
            Integer tmpI = (Integer)i.next();
            pList.entry.add(new PartEntry(tmpI.intValue(), (String)tMap.get(tmpI)));
        }
        return pList;
    }
}
