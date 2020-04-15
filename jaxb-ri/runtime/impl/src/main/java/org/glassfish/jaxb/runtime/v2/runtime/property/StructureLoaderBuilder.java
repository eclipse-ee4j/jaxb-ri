/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.property;

import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.ChildLoader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Loader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.StructureLoader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.ValuePropertyLoader;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;

import javax.xml.namespace.QName;

/**
 * Component that contributes element unmarshallers into
 * {@link StructureLoader}.
 *
 * TODO: think of a better name.
 *
 * @author Bhakti Mehta
 */
public interface StructureLoaderBuilder {
    /**
     * Every Property class has an implementation of buildChildElementUnmarshallers
     * which will fill in the specified {@link QNameMap} by elements that are expected
     * by this property.
     */
    void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers);

    /**
     * Magic {@link QName} used to store a handler for the text.
     *
     * <p>
     * To support the mixed content model, {@link StructureLoader} can have
     * at most one {@link ValuePropertyLoader} for processing text
     * found amoung elements.
     *
     * This special text handler is put into the {@link QNameMap} parameter
     * of the {@link #buildChildElementUnmarshallers} method by using
     * this magic token as the key.
     */
    public static final QName TEXT_HANDLER = new QName("\u0000","text");

    /**
     * Magic {@link QName} used to store a handler for the rest of the elements.
     *
     * <p>
     * To support the wildcard, {@link StructureLoader} can have
     * at most one {@link Loader} for processing elements
     * that didn't match any of the named elements.
     *
     * This special text handler is put into the {@link QNameMap} parameter
     * of the {@link #buildChildElementUnmarshallers} method by using
     * this magic token as the key.
     */
    public static final QName CATCH_ALL = new QName("\u0000","catchAll");
}
