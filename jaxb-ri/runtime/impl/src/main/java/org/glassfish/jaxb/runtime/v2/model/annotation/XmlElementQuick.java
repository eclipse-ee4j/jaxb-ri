/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.annotation;

import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import jakarta.xml.bind.annotation.XmlElement;

import java.lang.annotation.Annotation;


/**
 * <p><b>Auto-generated, do not edit.</b></p>
 * 
 */
final class XmlElementQuick
    extends Quick
    implements XmlElement
{

    private final XmlElement core;

    public XmlElementQuick(Locatable upstream, XmlElement core) {
        super(upstream);
        this.core = core;
    }

    @Override
    protected Annotation getAnnotation() {
        return core;
    }

    @Override
    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlElementQuick(upstream, ((XmlElement) core));
    }

    @Override
    public Class<XmlElement> annotationType() {
        return XmlElement.class;
    }

    @Override
    public String name() {
        return core.name();
    }

    @Override
    public Class type() {
        return core.type();
    }

    @Override
    public String namespace() {
        return core.namespace();
    }

    @Override
    public String defaultValue() {
        return core.defaultValue();
    }

    @Override
    public boolean required() {
        return core.required();
    }

    @Override
    public boolean nillable() {
        return core.nillable();
    }

}
