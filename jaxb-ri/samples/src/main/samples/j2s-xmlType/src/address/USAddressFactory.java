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
 * $Id: USAddressFactory.java,v 1.1 2007-12-05 00:49:32 kohsuke Exp $
 */

package address;

public class USAddressFactory {
    public static USAddress getUSAddress(){
        return new USAddress("Mark Baker", "23 Elm St", 
            "Dayton", "OH", 90952);
    }
}
