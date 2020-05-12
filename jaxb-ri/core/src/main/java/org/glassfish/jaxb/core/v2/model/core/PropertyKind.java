/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.core;

import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlInlineBinaryData;

/**
 * An Enum that indicates if the property is
 * Element, ElementRef, Value, or Attribute.
 *
 * <p>
 * Corresponds to the four different kind of {@link PropertyInfo}.
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
public enum PropertyKind {
    VALUE(true,false,Integer.MAX_VALUE),
    ATTRIBUTE(false,false,Integer.MAX_VALUE),
    ELEMENT(true,true,0),
    REFERENCE(false,true,1),
    MAP(false,true,2),
    ;

    /**
     * This kind of property can have {@link XmlMimeType} and {@link XmlInlineBinaryData}
     * annotation with it.
     */
    public final boolean canHaveXmlMimeType;

    /**
     * This kind of properties need to show up in {@link XmlType#propOrder()}.
     */
    public final boolean isOrdered;

    /**
     * {@code org.glassfish.jaxb.core.v2.runtime.property.PropertyFactory} benefits from having index numbers assigned to
     * {@link #ELEMENT}, {@link #REFERENCE}, and {@link #MAP} in this order.
     */
    public final int propertyIndex;

    PropertyKind(boolean canHaveExpectedContentType, boolean isOrdered, int propertyIndex) {
        this.canHaveXmlMimeType = canHaveExpectedContentType;
        this.isOrdered = isOrdered;
        this.propertyIndex = propertyIndex;
    }
}
