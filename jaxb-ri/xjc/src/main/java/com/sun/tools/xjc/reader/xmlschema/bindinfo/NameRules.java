/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Name conversion rules. All defaults to {@link NameRules#defaultNamingRule}.
 */
@XmlType(propOrder={})
public final class NameRules {
    @XmlElement
    NamingRule typeName = defaultNamingRule;
    @XmlElement
    NamingRule elementName = defaultNamingRule;
    @XmlElement
    NamingRule attributeName = defaultNamingRule;
    @XmlElement
    NamingRule modelGroupName = defaultNamingRule;
    @XmlElement
    NamingRule anonymousTypeName = defaultNamingRule;

    /**
     * Default naming rule, that doesn't change the name.
     */
    private static final NamingRule defaultNamingRule = new NamingRule("", "");

    /**
     * Default naming rules of the generated interfaces.
     *
     * It simply adds prefix and suffix to the name, but
     * the caller shouldn't care how the name mangling is
     * done.
     */
    public static final class NamingRule {
        @XmlAttribute
        private String prefix = "";
        @XmlAttribute
        private String suffix = "";

        public NamingRule(String _prefix, String _suffix) {
            this.prefix = _prefix;
            this.suffix = _suffix;
        }

        public NamingRule() {
        }

        /** Changes the name according to the rule. */
        public String mangle(String originalName) {
            return prefix + originalName + suffix;
        }
    }
}