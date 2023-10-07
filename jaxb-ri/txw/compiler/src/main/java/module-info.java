/*
 * Copyright (c) 2021, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

@SuppressWarnings({"module"})
module com.sun.tools.txwc2 {
    requires transitive java.xml;

    requires com.sun.xml.txw2;
    requires com.sun.xml.xsom;
    requires com.sun.tools.rngdatatype;
    requires transitive com.sun.tools.rngom;
    requires transitive com.sun.codemodel;

    exports com.sun.tools.txw2;
    exports com.sun.tools.txw2.model;
    exports com.sun.tools.txw2.model.prop;
}
