/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module com.sun.tools.jxc {
    requires transitive java.xml.bind;
    requires java.compiler;
    requires jdk.compiler;
    requires java.logging;
    requires transitive com.sun.xml.bind;
    requires transitive com.sun.tools.xjc;

    exports com.sun.tools.jxc;
    exports com.sun.tools.jxc.ap;
    exports com.sun.tools.jxc.model.nav;
    exports com.sun.tools.jxc.api;
}