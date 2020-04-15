/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.model.core;

import java.util.Collection;

import jakarta.activation.MimeType;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import javax.xml.namespace.QName;

import com.sun.istack.Nullable;
import com.sun.xml.bind.v2.model.annotation.AnnotationSource;

/**
 * Information about a JAXB-bound property.
 *
 * <p>
 * All the JAXB annotations are already incorporated into the model so that
 * the caller doesn't have to worry about reading them. For this reason, you
 * cannot access annotations on properties directly.
 *
 * TODO: don't we need a visitor?
 *
 * @author Kohsuke Kawaguchi
 */
public interface PropertyInfo<T,C> extends AnnotationSource {

    /**
     * Gets the {@link ClassInfo} or {@link ElementInfo} to which this property belongs.
     */
    TypeInfo<T,C> parent();

    /**
     * Gets the name of the property.
     *
     * <p>
     * For example, "foo" or "bar".
     * Generally, a property name is different from XML,
     * (although they are often related, as a property name is often
     * computed from tag names / attribute names.)
     * In fact, <b>property names do not directly affect XML</b>.
     * The property name uniquely identifies a property within a class.
     *
     * @see XmlType#propOrder()
     */
    String getName();

    /**
     * Gets the display name of the property.
     *
     * <p>
     * This is a convenience method for
     * {@code parent().getName()+'#'+getName()}.
     */
    String displayName();

    /**
     * Returns true if this is a multi-valued collection property.
     * Otherwise false, in which case the property is a single value.
     */
    boolean isCollection();

    /**
     * List of {@link TypeInfo}s that this property references.
     *
     * This allows the caller to traverse the reference graph without
     * getting into the details of each different property type.
     *
     * @return
     *      non-null read-only collection.
     */
    Collection<? extends TypeInfo<T,C>> ref();

    /**
     * Gets the kind of this property.
     *
     * @return
     *      always non-null.
     */
    PropertyKind kind();

    /**
     * @return
     *      null if the property is not adapted.
     */
    Adapter<T,C> getAdapter();

    /**
     * Returns the IDness of the value of this element.
     *
     * @see XmlID
     * @see XmlIDREF
     *
     * @return
     *      always non-null
     */
    ID id();

    /**
     * Expected MIME type, if any.
     */
    MimeType getExpectedMimeType();

    /**
     * If this is true and this property indeed represents a binary data,
     * it should be always inlined.
     */
    boolean inlineBinaryData();

    /**
     * The effective value of {@link XmlSchemaType} annotation, if any.
     *
     * <p>
     * If the property doesn't have {@link XmlSchemaType} annotation,
     * this method returns null.
     *
     * <p>
     * Since a type name is a property of a Java type, not a Java property,
     * A schema type name of a Java type should be primarily obtained
     * by using {@link NonElement#getTypeName()}. This method is to correctly
     * implement the ugly semantics of {@link XmlSchemaType} (namely
     * when this returns non-null, it overrides the type names of all types
     * that are in this property.)
     */
    @Nullable QName getSchemaType();
}
