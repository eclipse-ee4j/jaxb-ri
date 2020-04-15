/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
    exports com.sun.xml.bind.v2.model.util;
    exports com.sun.xml.bind.v2.runtime;
    exports com.sun.xml.bind.v2.runtime.unmarshaller;
    exports com.sun.xml.bind.v2.schemagen.episode;
    exports com.sun.xml.bind.v2.util;

    exports com.sun.xml.txw2;
    exports com.sun.xml.txw2.annotation;
    exports com.sun.xml.txw2.output;

    opens com.sun.xml.bind.v2.model.nav to
            com.sun.xml.bind,
            com.sun.tools.xjc,
            com.sun.xml.ws.rt,
            com.sun.xml.ws,
            com.sun.tools.ws;

//    uses jakarta.xml.bind.JAXBContextFactory with com.sun.xml.bind.v2.JAXBContextFactory;
}
