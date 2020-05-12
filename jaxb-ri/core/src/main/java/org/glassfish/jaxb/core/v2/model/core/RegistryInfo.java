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

import java.util.Set;

import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * Represents the information in a class with {@link XmlRegistry} annotaion.
 *
 * <p>
 * This interface is only meant to be used as a return type from
 * {@link org.glassfish.jaxb.core.v2.model.impl.ModelBuilderI}.
 *
 * @author Kohsuke Kawaguchi
 * @param <T>
 * @param <C>
 */
public interface RegistryInfo<T,C> {
    /**
     * Returns all the references to other types in this registry.
     * @return 
     */
    Set<TypeInfo<T,C>> getReferences();

    /**
     * Returns the class with {@link XmlRegistry}.
     * @return 
     */
    C getClazz();
}
