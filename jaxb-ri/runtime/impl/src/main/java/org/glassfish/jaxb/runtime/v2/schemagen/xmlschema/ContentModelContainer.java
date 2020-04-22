/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.schemagen.xmlschema;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlElement;

/**
 * Used to write a content model.
 *
 * This mixes the particle and model group as the child of complex type.
 * 
 * @author Kohsuke Kawaguchi
 */
public interface ContentModelContainer extends TypedXmlWriter {
    @XmlElement
    LocalElement element();

    @XmlElement
    Any any();

    @XmlElement
    ExplicitGroup all();

    @XmlElement
    ExplicitGroup sequence();

    @XmlElement
    ExplicitGroup choice();
}
