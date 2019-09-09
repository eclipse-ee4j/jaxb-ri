/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module com.sun.xml.bind.osgi {

    requires transitive java.xml.bind;
    requires transitive jakarta.activation;
    requires transitive java.xml;
    requires java.compiler;
    requires java.desktop;
    requires java.logging;
    requires static jdk.compiler;

    exports com.sun.istack;
    exports com.sun.istack.localization;
    exports com.sun.istack.logging;

    exports com.sun.xml.bind;
    exports com.sun.xml.bind.annotation;
    exports com.sun.xml.bind.api;
    exports com.sun.xml.bind.api.impl;
    exports com.sun.xml.bind.marshaller;
    exports com.sun.xml.bind.unmarshaller;
    exports com.sun.xml.bind.util;
    exports com.sun.xml.bind.v2;
    exports com.sun.xml.bind.v2.model.annotation;
    exports com.sun.xml.bind.v2.model.core;
    exports com.sun.xml.bind.v2.model.impl;
    exports com.sun.xml.bind.v2.model.nav;
    exports com.sun.xml.bind.v2.model.runtime;
    exports com.sun.xml.bind.v2.model.util;
    exports com.sun.xml.bind.v2.runtime;
    exports com.sun.xml.bind.v2.runtime.unmarshaller;
    exports com.sun.xml.bind.v2.schemagen;
    exports com.sun.xml.bind.v2.schemagen.episode;
    exports com.sun.xml.bind.v2.schemagen.xmlschema;
    exports com.sun.xml.bind.v2.util;

    exports com.sun.tools.xjc;
    exports com.sun.tools.xjc.reader;
    exports com.sun.tools.xjc.reader.internalizer;
    exports com.sun.tools.xjc.api;
    exports com.sun.tools.xjc.util;

    exports com.sun.tools.jxc;
    exports com.sun.tools.jxc.ap;
    exports com.sun.tools.jxc.model.nav;
    exports com.sun.tools.jxc.api;


    exports com.sun.xml.txw2;
    exports com.sun.xml.txw2.annotation;
    exports com.sun.xml.txw2.output;

    exports com.sun.xml.fastinfoset;
    exports com.sun.xml.fastinfoset.algorithm;
    exports com.sun.xml.fastinfoset.alphabet;
    exports com.sun.xml.fastinfoset.dom;
    exports com.sun.xml.fastinfoset.sax;
    exports com.sun.xml.fastinfoset.stax;
    exports com.sun.xml.fastinfoset.stax.events;
    exports com.sun.xml.fastinfoset.stax.factory;
    exports com.sun.xml.fastinfoset.stax.util;
    exports com.sun.xml.fastinfoset.tools;
    exports com.sun.xml.fastinfoset.util;
    exports com.sun.xml.fastinfoset.vocab;
    exports org.jvnet.fastinfoset;
    exports org.jvnet.fastinfoset.sax;
    exports org.jvnet.fastinfoset.sax.helpers;
    exports org.jvnet.fastinfoset.stax;

    exports org.jvnet.staxex;
    exports org.jvnet.staxex.util;


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

    opens com.sun.tools.xjc.reader.xmlschema.bindinfo to java.xml.bind;

    uses javax.xml.bind.JAXBContextFactory;
    uses com.sun.tools.xjc.Plugin;

    provides javax.xml.bind.JAXBContextFactory with com.sun.xml.bind.v2.JAXBContextFactory;
    provides com.sun.tools.xjc.Plugin with
        com.sun.tools.xjc.addon.accessors.PluginImpl,
        com.sun.tools.xjc.addon.at_generated.PluginImpl,
        com.sun.tools.xjc.addon.code_injector.PluginImpl,
        com.sun.tools.xjc.addon.episode.PluginImpl,
        com.sun.tools.xjc.addon.locator.SourceLocationAddOn,
        com.sun.tools.xjc.addon.sync.SynchronizedMethodAddOn;
}
