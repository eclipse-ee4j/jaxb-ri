/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.schemagen.episode;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.annotation.XmlAttribute;

/**
 * @author Kohsuke Kawaguchi
 */
@XmlElement("bindings")
public interface Bindings extends TypedXmlWriter {
    /**
     * Nested bindings.
     */
    @XmlElement
    Bindings bindings();

    /**
     * Nested class customization.
     */
    @XmlElement("class")
    Klass klass();

    /**
     * Nested typesafeEnumClass customization
     */
    Klass typesafeEnumClass();

    @XmlElement
    SchemaBindings schemaBindings();

    @XmlAttribute
    void scd(String scd);

    @XmlAttribute
    void version(String v);
}
