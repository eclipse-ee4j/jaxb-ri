/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * Enum member name handling mode.
 *
 * @author Kohsuke Kawaguchi
 */
@XmlEnum
public enum EnumMemberMode {
    @XmlEnumValue("skipGeneration")
    SKIP,
    @XmlEnumValue("generateError")
    ERROR,
    @XmlEnumValue("generateName")
    GENERATE

    ;

    /**
     * The mode will change to this when there's {@code <jaxb:enum>} customization.
     */
    public EnumMemberMode getModeWithEnum() {
        if(this==SKIP)  return ERROR;
        return this;
    }
}
