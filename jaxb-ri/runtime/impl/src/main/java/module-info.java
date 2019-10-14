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
 * The XML Binding (JAXB) RI modularization implementation.
 *
 * @uses javax.xml.bind.JAXBContextFactory
 *
 */
module org.glassfish.jaxb.runtime {
    requires transitive java.xml.bind;
    requires java.compiler;
    requires java.desktop;
    requires java.logging;

    requires transitive jakarta.activation;
    requires transitive java.xml;

    requires transitive com.sun.xml.txw2;
    requires static com.sun.xml.fastinfoset;
    requires static org.jvnet.staxex;
    requires transitive com.sun.istack.runtime;

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

    opens com.sun.xml.bind.v2.model.nav to org.glassfish.jaxb.xjc;

    uses javax.xml.bind.JAXBContextFactory;

    provides javax.xml.bind.JAXBContextFactory with com.sun.xml.bind.v2.JAXBContextFactory;
}
