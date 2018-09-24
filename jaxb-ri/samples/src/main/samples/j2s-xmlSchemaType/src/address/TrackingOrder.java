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
 * $Id: TrackingOrder.java,v 1.1 2007-12-05 00:49:30 kohsuke Exp $
 */


package address;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement
@XmlType(name="TrackingOrderType")
public class TrackingOrder {
  String trackingDuration;
  
  @XmlSchemaType(name="date")
  XMLGregorianCalendar shipDate;
  @XmlElement XMLGregorianCalendar orderDate;
  @XmlElement XMLGregorianCalendar deliveryDate;
    
  @XmlSchemaType(name="duration")
  public String getTrackingDuration(){
    return trackingDuration;
  }
  public void setTrackingDuration( String d){
    trackingDuration = d;
  }
}

