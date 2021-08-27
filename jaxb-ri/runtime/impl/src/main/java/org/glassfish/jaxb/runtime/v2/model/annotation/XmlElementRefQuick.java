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
import jakarta.xml.bind.annotation.XmlElementRef;

import java.lang.annotation.Annotation;


/**
 * <p><b>Auto-generated, do not edit.</b></p>
 * 
 */
final class XmlElementRefQuick
    extends Quick
    implements XmlElementRef
{

    private final XmlElementRef core;

    public XmlElementRefQuick(Locatable upstream, XmlElementRef core) {
        super(upstream);
        this.core = core;
    }

    @Override
    protected Annotation getAnnotation() {
        return core;
    }

    @Override
    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlElementRefQuick(upstream, ((XmlElementRef) core));
    }

    @Override
    public Class<XmlElementRef> annotationType() {
        return XmlElementRef.class;
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
    public boolean required() {
        return core.required();
    }

}
