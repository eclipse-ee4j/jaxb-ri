/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.api;



/**
 * Accesses a particular property of a bean.
 *
 * <p>
 * This interface allows JAX-RPC to access an element property of a JAXB bean.
 *
 * <p>
 * <b>Subject to change without notice</b>.
 *
 * @author Kohsuke Kawaguchi
 *
 * @since 2.0 EA1
 */
public abstract class RawAccessor<B,V> {

    /**
     * Default constructor.
     */
    protected RawAccessor() {}

    /**
     * Gets the value of the property of the given bean object.
     *
     * @param bean
     *      must not be null.
     * @throws AccessorException
     *      if failed to set a value. For example, the getter method
     *      may throw an exception.
     *
     * @since 2.0 EA1
     */
    public abstract V get(B bean) throws AccessorException;

    /**
     * Sets the value of the property of the given bean object.
     *
     * @param bean
     *      must not be null.
     * @param value
     *      the value to be set. Setting value to null means resetting
     *      to the VM default value (even for primitive properties.)
     * @throws AccessorException
     *      if failed to set a value. For example, the setter method
     *      may throw an exception.
     *
     * @since 2.0 EA1
     */
    public abstract void set(B bean,V value) throws AccessorException;
}
