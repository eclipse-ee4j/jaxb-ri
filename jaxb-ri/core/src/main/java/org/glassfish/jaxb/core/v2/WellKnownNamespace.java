/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2;

import javax.xml.XMLConstants;

/**
 * Well-known namespace URIs.
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com), Martin Grebac (martin.grebac@oracle.com)
 * @since 2.0
 */
public abstract class WellKnownNamespace {
    private WellKnownNamespace() {} // no instanciation please

    /**
     * @deprecated Use javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI instead;
     */
    @Deprecated()
    public static final String XML_SCHEMA = XMLConstants.W3C_XML_SCHEMA_NS_URI;

    /**
     * @deprecated Use javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI instead
     */
    @Deprecated()
    public static final String XML_SCHEMA_INSTANCE = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI;

    /**
     * @deprecated Use javax.xml.XMLConstants.XML_NS_URI instead;
     */
    @Deprecated()
    public static final String XML_NAMESPACE_URI = XMLConstants.XML_NS_URI;

    public static final String XOP = "http://www.w3.org/2004/08/xop/include";

    public static final String SWA_URI = "http://ws-i.org/profiles/basic/1.1/xsd";

    public static final String XML_MIME_URI = "http://www.w3.org/2005/05/xmlmime";

    public static final String JAXB = "https://jakarta.ee/xml/ns/jaxb";

}
