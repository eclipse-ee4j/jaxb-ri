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
import javax.xml.bind.annotation.XmlType;


/**
 * <p><b>Auto-generated, do not edit.</b></p>
 * 
 */
final class XmlTypeQuick
    extends Quick
    implements XmlType
{

    private final XmlType core;

    public XmlTypeQuick(Locatable upstream, XmlType core) {
        super(upstream);
        this.core = core;
    }

    protected Annotation getAnnotation() {
        return core;
    }

    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlTypeQuick(upstream, ((XmlType) core));
    }

    public Class<XmlType> annotationType() {
        return XmlType.class;
    }

    public String name() {
        return core.name();
    }

    public String namespace() {
        return core.namespace();
    }

    public String[] propOrder() {
        return core.propOrder();
    }

    public Class factoryClass() {
        return core.factoryClass();
    }

    public String factoryMethod() {
        return core.factoryMethod();
    }

}
