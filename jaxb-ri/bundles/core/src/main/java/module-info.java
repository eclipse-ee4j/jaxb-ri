/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

module com.sun.xml.bind.core {
    requires transitive jakarta.xml.bind;
    requires java.compiler;
    requires java.logging;

    requires transitive jakarta.activation;
    requires transitive java.xml;

    exports com.sun.istack;
    exports com.sun.istack.localization;
    exports com.sun.istack.logging;

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

    exports com.sun.xml.txw2;
    exports com.sun.xml.txw2.annotation;
    exports com.sun.xml.txw2.output;

    opens org.glassfish.jaxb.core.v2.model.nav to
            com.sun.xml.bind,
            com.sun.tools.xjc,
            com.sun.xml.ws,
            com.sun.tools.ws;

}
