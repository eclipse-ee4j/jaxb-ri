/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * <h2>Runtime API for the JAX-WS RI</h2>.
 *
 * This API is designed for the use by the JAX-WS RI runtime. The API is is subject to
 * change without notice.
 *
 * <p>
 * In an container environment, such as in J2SE/J2EE, if a new version with
 * a modified runtime API is loaded into a child class loader, it will still be bound
 * against the old runtime API in the base class loader.
 *
 * <p>
 * So the compatibility of this API has to be managed carefully.
 */
package org.glassfish.jaxb.runtime.api;
