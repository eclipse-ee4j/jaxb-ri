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
 * $Id: PartEntry.java,v 1.1 2007-12-05 00:49:28 kohsuke Exp $
 */


package shoppingCart;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class PartEntry {
  @XmlValue public String value;
  @XmlAttribute public int key;
  
  public PartEntry(){}
  public PartEntry(int tKey, String tValue){
    key = tKey;
    value = tValue;
  }
  public String toString(){
    return "key=" + key +"  value=" + value;
  }
}




