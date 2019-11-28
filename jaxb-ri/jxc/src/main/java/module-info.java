/*
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * JAXB schema generator.The *tool* to generate XML schema based on java classes.
 */
module org.glassfish.jaxb.jxc {

    requires transitive java.xml.bind;
    requires java.compiler;
    requires jdk.compiler;
    requires java.logging;
    requires transitive org.glassfish.jaxb.runtime;
    requires transitive org.glassfish.jaxb.xjc;
    requires transitive com.sun.xml.txw2;


    exports com.sun.tools.jxc;
    exports com.sun.tools.jxc.ap;
    exports com.sun.tools.jxc.model.nav;
    exports com.sun.tools.jxc.api;
}
