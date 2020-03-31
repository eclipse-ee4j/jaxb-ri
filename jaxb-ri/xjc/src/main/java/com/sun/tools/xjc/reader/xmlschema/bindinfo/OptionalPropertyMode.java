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

import jakarta.xml.bind.annotation.XmlEnumValue;

/**
 * Represents three constants of globalBindings/@optionalProperty.
 *
 * @author Kohsuke Kawaguchi
 */
public enum OptionalPropertyMode {
    @XmlEnumValue("primitive")
    PRIMITIVE,
    @XmlEnumValue("wrapper")
    WRAPPER,
    @XmlEnumValue("isSet")
    ISSET
}
