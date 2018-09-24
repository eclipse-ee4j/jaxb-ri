/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * XML Schema Object Model (XSOM) is a Java library that allows applications to easily parse XML Schema
 * documents and inspect information in them. It is expected to be useful for applications that need to take XML
 * Schema as an input.
 *
 */
module com.sun.xml.xsom {

    requires com.sun.tools.rngdatatype;
    requires java.desktop;
    requires java.logging;

    exports com.sun.xml.xsom;
    exports com.sun.xml.xsom.util;
    exports com.sun.xml.xsom.visitor;
    exports com.sun.xml.xsom.impl.util;
    exports com.sun.xml.xsom.parser;
}
