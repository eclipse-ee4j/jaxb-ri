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
 * JAXB Binding Compiler. Contains source code needed for binding customization files into java sources.
 * In other words: the *tool* to generate java classes for the given xml representation.
 */
module org.glassfish.jaxb.xjc {

    requires transitive jakarta.activation;

    requires java.logging;
    requires transitive java.compiler;
    requires transitive java.xml;
    requires jdk.compiler;
    requires java.desktop;

    requires com.sun.tools.rngdatatype;
    requires transitive com.sun.codemodel;
    requires transitive java.xml.bind;
    requires transitive org.glassfish.jaxb.runtime;
    requires com.sun.istack.runtime;
    requires com.sun.istack.tools;
    requires transitive com.sun.xml.xsom;
    requires com.sun.tools.rngom;
    requires com.sun.xml.dtdparser;
    requires com.sun.xml.txw2;

    opens com.sun.tools.xjc.reader.xmlschema.bindinfo to java.xml.bind;
    opens com.sun.tools.xjc.generator.bean to org.glassfish.jaxb.runtime;

    exports com.sun.tools.xjc;
    exports com.sun.tools.xjc.model;
    exports com.sun.tools.xjc.outline;
    exports com.sun.tools.xjc.reader;
    exports com.sun.tools.xjc.reader.internalizer;
    exports com.sun.tools.xjc.api;
    exports com.sun.tools.xjc.util;

    uses com.sun.tools.xjc.Plugin;

}
