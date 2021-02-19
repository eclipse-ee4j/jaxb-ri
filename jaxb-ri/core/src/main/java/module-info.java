/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * The XML Binding implementation.
 *
 * @uses jakarta.xml.bind.JAXBContextFactory
 *
 */
module org.glassfish.jaxb.core {
    requires transitive jakarta.xml.bind;
    requires java.compiler;
    requires java.desktop;
    requires java.logging;

    requires transitive jakarta.activation;
    requires transitive java.xml;

    requires transitive com.sun.xml.txw2;
    requires transitive com.sun.istack.runtime;

    exports org.glassfish.jaxb.core;
    exports org.glassfish.jaxb.core.annotation;
    exports org.glassfish.jaxb.core.api;
    exports org.glassfish.jaxb.core.api.impl;
    exports org.glassfish.jaxb.core.marshaller;
    exports org.glassfish.jaxb.core.unmarshaller;
    exports org.glassfish.jaxb.core.util;
    exports org.glassfish.jaxb.core.v2;
    exports org.glassfish.jaxb.core.v2.model.annotation;
    exports org.glassfish.jaxb.core.v2.model.core;
    exports org.glassfish.jaxb.core.v2.model.impl;
    exports org.glassfish.jaxb.core.v2.model.nav;
    exports org.glassfish.jaxb.core.v2.model.util;
    exports org.glassfish.jaxb.core.v2.runtime;
    exports org.glassfish.jaxb.core.v2.runtime.unmarshaller;
    exports org.glassfish.jaxb.core.v2.schemagen.episode;
    exports org.glassfish.jaxb.core.v2.util;

    opens org.glassfish.jaxb.core.v2.model.nav to
            org.glassfish.jaxb.runtime,
            org.glassfish.jaxb.xjc,
            com.sun.xml.ws.rt,
            com.sun.tools.ws.wscompile;

}
