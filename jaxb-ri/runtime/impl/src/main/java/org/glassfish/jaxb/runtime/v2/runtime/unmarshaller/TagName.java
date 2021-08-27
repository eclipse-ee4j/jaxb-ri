/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;

/**
 * Represents an XML tag name (and attributes for start tags.)
 *
 * <p>
 * This object is used so reduce the number of method call parameters
 * among unmarshallers.
 *
 * An instance of this is expected to be reused by the caller of
 * {@link XmlVisitor}. Note that the rest of the unmarshaller may
 * modify any of the fields while processing an event (such as to
 * intern strings, replace attributes),
 * so {@link XmlVisitor} should reset all fields for each use.
 *
 * <p>
 * The 'qname' parameter, which holds the qualified name of the tag
 * (such as 'foo:bar' or 'zot'), is not used in the typical unmarshalling
 * route and it's also expensive to compute for some input.
 * Thus this parameter is computed lazily.
 *
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings({"StringEquality"})
public abstract class TagName {
    /**
     * URI of the attribute/element name.
     *
     * Can be empty, but never null. Interned.
     */
    public String uri;
    /**
     * Local part of the attribute/element name.
     *
     * Never be null. Interned.
     */
    public String local;

    /**
     * Used only for the enterElement event.
     * Otherwise the value is undefined.
     *
     * This might be {@link AttributesEx}.
     */
    public Attributes atts;

    public TagName() {
    }

    /**
     * Checks if the given name pair matches this name.
     */
    public final boolean matches( String nsUri, String local ) {
        return this.uri==nsUri && this.local==local;
    }

    /**
     * Checks if the given name pair matches this name.
     */
    public final boolean matches( Name name ) {
        return this.local==name.localName && this.uri==name.nsUri;
    }

//    /**
//     * @return
//     *      Can be empty but always non-null. NOT interned.
//     */
//    public final String getPrefix() {
//        int idx = qname.indexOf(':');
//        if(idx<0)   return "";
//        else        return qname.substring(0,idx);
//    }

    @Override
    public String toString() {
        return '{'+uri+'}'+local;
    }

    /**
     * Gets the qualified name of the tag.
     *
     * @return never null.
     */
    public abstract String getQname();

    /**
     * Gets the prefix. This is slow.
     *
     * @return can be "" but never null.
     */
    public String getPrefix() {
        String qname = getQname();
        int idx = qname.indexOf(':');
        if(idx<0)   return "";
        else        return qname.substring(0,idx);
    }

    /**
     * Creates {@link QName}.
     */
    public QName createQName() {
        return new QName(uri,local,getPrefix());
    }
}
