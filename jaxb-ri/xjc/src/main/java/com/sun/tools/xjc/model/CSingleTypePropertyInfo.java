/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import java.util.Collections;
import java.util.List;

import jakarta.activation.MimeType;
import javax.xml.namespace.QName;

import org.glassfish.jaxb.core.v2.model.core.ID;
import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * {@link CPropertyInfo} backed by a single {@link TypeUse}.
 *
 * @author Kohsuke Kawaguchi
 */
abstract class CSingleTypePropertyInfo extends CPropertyInfo {
    protected final TypeUse type;

    private final QName schemaType;

    /**
     *
     * @param typeName
     *      XML Schema type name of this property's single value. Optional
     *      for other schema languages. This is used to determine if we should
     *      generate {@link @XmlSchemaType} annotation to improve the roundtrip.
     */
    protected CSingleTypePropertyInfo(String name, TypeUse type, QName typeName, XSComponent source, CCustomizations customizations, Locator locator) {
        super(name, type.isCollection(), source, customizations, locator);
        this.type = type;

        if(needsExplicitTypeName(type,typeName))
            schemaType = typeName;
        else
            schemaType = null;
    }

    public QName getSchemaType() {
        return schemaType;
    }

    public final ID id() {
        return type.idUse();
    }

    public final MimeType getExpectedMimeType() {
        return type.getExpectedMimeType();
    }

    public final List<? extends CTypeInfo> ref() {
        return Collections.singletonList(getTarget());
    }

    public final CNonElement getTarget() {
        CNonElement r = type.getInfo();
        assert r!=null;
        return r;
    }

    public final CAdapter getAdapter() {
        return type.getAdapterUse();
    }

    public final CSingleTypePropertyInfo getSource() {
        return this;
    }
}
