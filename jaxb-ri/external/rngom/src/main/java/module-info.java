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
 *  RNGOM is a RelaxNG Object model library (XSOM for RelaxNG).
 */
module com.sun.tools.rngom {

    requires com.sun.tools.rngdatatype;
    requires java.xml;
    requires java.logging;

    exports com.sun.tools.rngom.parse;
    exports com.sun.tools.rngom.parse.compact;
    exports com.sun.tools.rngom.parse.xml;
    exports com.sun.tools.rngom.digested;
    exports com.sun.tools.rngom.nc;
    exports com.sun.tools.rngom.xml.sax;
    exports com.sun.tools.rngom.xml.util;
    exports com.sun.tools.rngom.ast.builder;
    exports com.sun.tools.rngom.ast.util;
}
