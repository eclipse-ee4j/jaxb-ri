/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom;

import com.sun.tools.rngdatatype.ValidationContext;

/**
 * String with in-scope namespace binding information.
 *
 * <p>
 * In a general case, text (PCDATA/attributes) that appear in XML schema
 * cannot be correctly interpreted unless you also have in-scope namespace
 * binding (a case in point is QName.) Therefore, it's convenient to
 * handle the lexical representation and the in-scope namespace binding
 * in a pair.
 *
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings({"exports"})
public final class XmlString {
    /**
     * Textual value. AKA lexical representation.
     */
    public final String value;

    /**
     * Used to resole in-scope namespace bindings.
     */
    public final ValidationContext context;

    /**
     * Creates a new  from a lexical representation and in-scope namespaces.
     */
    public XmlString(String value, ValidationContext context) {
        this.value = value;
        this.context = context;
        if(context==null)
            throw new IllegalArgumentException();
    }

    /**
     * Creates a new  with empty in-scope namespace bindings.
     */
    public XmlString(String value) {
        this(value,NULL_CONTEXT);
    }

    /**
     * Resolves a namespace prefix to the corresponding namespace URI.
     *
     * This method is used for resolving prefixes in the {@link #value}
     * (such as when {@link #value} represents a QName type.)
     *
     * <p>
     * If the prefix is "" (empty string), the method
     * returns the default namespace URI.
     *
     * <p>
     * If the prefix is "xml", then the method returns
     * "http://www.w3.org/XML/1998/namespace",
     * as defined in the XML Namespaces Recommendation.
     *
     * @return
     *		namespace URI of this prefix.
     *		If the specified prefix is not declared,
     *		the implementation returns null.
     */
    public String resolvePrefix(String prefix) {
        return context.resolveNamespacePrefix(prefix);
    }

    public String toString() {
        return value;
    }

    private static final ValidationContext NULL_CONTEXT = new ValidationContext() {
        public String resolveNamespacePrefix(String s) {
            if(s.length()==0)   return "";
            if(s.equals("xml")) return "http://www.w3.org/XML/1998/namespace";
            return null;
        }

        public String getBaseUri() {
            return null;
        }

        public boolean isUnparsedEntity(String s) {
            return false;
        }

        public boolean isNotation(String s) {
            return false;
        }
    };
}
