/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlEnum;


/**
 * <p><b>Auto-generated, do not edit.</b></p>
 * 
 */
final class XmlEnumQuick
    extends Quick
    implements XmlEnum
{

    private final XmlEnum core;

    public XmlEnumQuick(Locatable upstream, XmlEnum core) {
        super(upstream);
        this.core = core;
    }

    protected Annotation getAnnotation() {
        return core;
    }

    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlEnumQuick(upstream, ((XmlEnum) core));
    }

    public Class<XmlEnum> annotationType() {
        return XmlEnum.class;
    }

    public Class value() {
        return core.value();
    }

}
