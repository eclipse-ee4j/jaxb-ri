/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package extend;

public class AutoTypeExtend extends AutoType {
    public void printTravelSummary() {
	super.printTravelSummary();
	System.out.println("Rental Agency:" + getRentalAgency());
	System.out.println("Rate Per Hour:" + getRatePerHour());
    }
}
