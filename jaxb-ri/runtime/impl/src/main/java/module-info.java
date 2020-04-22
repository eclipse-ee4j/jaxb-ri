/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
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
 * @uses jakarta.xml.bind.JAXBContextFactory
 *
 */
module org.glassfish.jaxb.runtime {
    requires transitive jakarta.xml.bind;
    requires java.compiler;
    requires java.desktop;
    requires java.logging;

    requires transitive jakarta.activation;
    requires transitive java.xml;

    requires transitive org.glassfish.jaxb.core;
    requires static com.sun.xml.fastinfoset;
    requires static org.jvnet.staxex;

    exports org.glassfish.jaxb.runtime;
    exports org.glassfish.jaxb.runtime.api;
    exports org.glassfish.jaxb.runtime.marshaller;
    exports org.glassfish.jaxb.runtime.unmarshaller;
    exports org.glassfish.jaxb.runtime.util;
    exports org.glassfish.jaxb.runtime.v2;
    exports org.glassfish.jaxb.runtime.v2.model.annotation;
    exports org.glassfish.jaxb.runtime.v2.model.impl;
    exports org.glassfish.jaxb.runtime.v2.model.runtime;
    exports org.glassfish.jaxb.runtime.v2.runtime;
    exports org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;
    exports org.glassfish.jaxb.runtime.v2.schemagen;
    exports org.glassfish.jaxb.runtime.v2.schemagen.xmlschema;
    exports org.glassfish.jaxb.runtime.v2.util;

    opens org.glassfish.jaxb.runtime.v2.runtime.reflect.opt to jakarta.xml.bind;
    opens org.glassfish.jaxb.runtime.v2.schemagen to jakarta.xml.bind;
    opens org.glassfish.jaxb.runtime.v2.schemagen.xmlschema to jakarta.xml.bind;
    opens org.glassfish.jaxb.runtime.v2 to jakarta.xml.bind;

    uses jakarta.xml.bind.JAXBContextFactory;

    provides jakarta.xml.bind.JAXBContextFactory
            with org.glassfish.jaxb.runtime.v2.JAXBContextFactory;
}
