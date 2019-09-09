/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module com.sun.tools.txw2 {
    requires com.sun.xml.txw2;
    requires com.sun.xml.xsom;

    requires com.sun.tools.rngdatatype;
    requires com.sun.tools.rngom;
    requires com.sun.codemodel;

    requires ant;
    requires ant.launcher;
    requires args4j;
}
