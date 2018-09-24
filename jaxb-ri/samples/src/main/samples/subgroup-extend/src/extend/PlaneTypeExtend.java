/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package extend;

import org.example.impl.PlaneTypeImpl;

public class PlaneTypeExtend extends PlaneTypeImpl {
    public void printTravelSummary() {
	super.printTravelSummary();
	System.out.println("Flight Number: " + getFlightNumber());
	System.out.println("Meal: " + getMeal());
    }
}
