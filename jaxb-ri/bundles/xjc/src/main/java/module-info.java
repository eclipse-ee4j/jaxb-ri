/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
module com.sun.tools.xjc {

    requires java.logging;
    requires transitive java.compiler;
    requires jdk.compiler;
    requires transitive java.desktop;

    requires transitive jakarta.activation;

    requires transitive java.xml.bind;
    requires transitive com.sun.xml.bind;

    opens com.sun.tools.xjc.reader.xmlschema.bindinfo to java.xml.bind;

    exports com.sun.tools.xjc;
    exports com.sun.tools.xjc.api;
    exports com.sun.tools.xjc.generator.bean to com.sun.xml.bind;
    exports com.sun.tools.xjc.model;
    exports com.sun.tools.xjc.model.nav;
    exports com.sun.tools.xjc.outline;
    exports com.sun.tools.xjc.reader;
    exports com.sun.tools.xjc.reader.internalizer;
    exports com.sun.tools.xjc.util;

    exports com.sun.xml.xsom;
    exports com.sun.xml.xsom.util;
    exports com.sun.xml.xsom.visitor;
    exports com.sun.xml.xsom.impl.util;
    exports com.sun.xml.xsom.parser;

    exports com.sun.tools.rngom.parse;
    exports com.sun.tools.rngom.parse.compact;
    exports com.sun.tools.rngom.parse.xml;
    exports com.sun.tools.rngom.digested;
    exports com.sun.tools.rngom.nc;
    exports com.sun.tools.rngom.xml.sax;
    exports com.sun.tools.rngom.xml.util;
    exports com.sun.tools.rngom.ast.builder;
    exports com.sun.tools.rngom.ast.om;
    exports com.sun.tools.rngom.ast.util;
    exports com.sun.tools.rngom.dt;
    exports com.sun.tools.rngom.dt.builtin;

    exports com.sun.codemodel;
    exports com.sun.codemodel.util;
    exports com.sun.codemodel.writer;
    exports com.sun.codemodel.fmt;

    exports com.sun.xml.dtdparser;

    exports com.sun.istack.tools;

    exports com.sun.tools.rngdatatype;
    exports com.sun.tools.rngdatatype.helpers;

    uses com.sun.tools.xjc.Plugin;
}
