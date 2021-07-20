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
 *  Author: Sekhar Vajjhala
 *
 *  $Id: PurchaseOrder.java,v 1.1 2007-12-05 00:49:37 kohsuke Exp $
 */

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PurchaseOrder {

    /**
     * NOTE: Address is used instead of USAddress or UKAddress since
     * the intent of this sample is to demonstrate the use of xsi type
     */
    public Address shipTo;
    public Address billTo;
}

