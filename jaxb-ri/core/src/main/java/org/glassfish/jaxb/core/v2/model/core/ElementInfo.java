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

import java.util.Collection;

import jakarta.xml.bind.JAXBElement;

/**
 * A particular use (specialization) of {@link JAXBElement}.
 *
 * TODO: is ElementInfo adaptable?
 *
 * @author Kohsuke Kawaguchi
 */
public interface ElementInfo<T,C> extends Element<T,C> {

    /**
     * Gets the object that represents the value property.
     *
     * @return
     *      non-null.
     */
    ElementPropertyInfo<T,C> getProperty();

    /**
     * Short for <code>getProperty().ref().get(0)</code>.
     *
     * The type of the value this element holds.
     *
     * Normally, this is the T of {@code JAXBElement<T>}.
     * But if the property is adapted, this is the on-the-wire type.
     *
     * Or if the element has a list of values, then this field
     * represents the type of the individual item.
     *
     * @see #getContentInMemoryType()
     */
    NonElement<T,C> getContentType();

    /**
     * T of {@code JAXBElement<T>}.
     *
     * <p>
     * This is tied to the in-memory representation.
     *
     * @see #getContentType()
     */
    T getContentInMemoryType();

    /**
     * Returns the representation for {@link JAXBElement}<i>{@code <contentInMemoryType>}</i>.
     *
     * <p>
     * This returns the signature in Java and thus isn't affected by the adapter.
     */
    T getType();

    /**
     * {@inheritDoc}
     *
     * {@link ElementInfo} can only substitute {@link ElementInfo}.
     */
    ElementInfo<T,C> getSubstitutionHead();

    /**
     * All the {@link ElementInfo}s whose {@link #getSubstitutionHead()} points
     * to this object.
     *
     * @return
     *      can be empty but never null.
     */
    Collection<? extends ElementInfo<T,C>> getSubstitutionMembers();
}
