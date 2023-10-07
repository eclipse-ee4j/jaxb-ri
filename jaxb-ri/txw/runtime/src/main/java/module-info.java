/*
 * Copyright (c) 2017, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

@SuppressWarnings({"module"})
module com.sun.xml.txw2 {
    requires transitive java.xml;

    exports com.sun.xml.txw2;
    exports com.sun.xml.txw2.annotation;
    exports com.sun.xml.txw2.output;

}
