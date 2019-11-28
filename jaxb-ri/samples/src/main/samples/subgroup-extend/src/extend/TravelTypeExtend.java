/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package extend;

public class TravelTypeExtend extends TravelType {
    public void printTravelSummary() {
	System.out.println("Origin=" + getOrigin());
	System.out.println("Destination=" + getDestination());
    }
}
