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
 * The in-memory model of the JAXB-bound beans.
 *
 * <h2>Parameterizations</h2>
 * <p>
 * Interfaces in this package are parameterized to work with arbitrary Java reflection library.
 * This is necessary because the RI needs to work with both the runtime reflection library
 * ({@link java.lang.reflect}) and the Annotation Processing.
 *
 * <p>
 * The meaning of parameterizations are as follows:
 *
 * <dl>
 *  <dt><b>T</b></dt>
 *  <dd>Represents an use of type, such as {@code int}, {@code Foo[]}, or {@code List<Foo>}.
 *      Corresponds to {@link java.lang.reflect.Type}.</dd>
*
 *  <dt><b>C</b></dt>
 *  <dd>Represents a declaration of a type (that is, class, interface, enum, or annotation.)
 *      This doesn't include {@code int}, {@code Foo[]}, or {@code List<Foo>}, because
 *      they don't have corresponding declarations.
 *      Corresponds to {@link java.lang.Class} (roughly).</dd>
 *
 *  <dt><b>F</b></dt>
 *  <dd>Represents a field.
 *      Corresponds to {@link java.lang.reflect.Field}.</dd>
 *
 *  <dt><b>M</b></dt>
 *  <dd>Represents a method.
 *      Corresponds to {@link java.lang.reflect.Method}.</dd>
 *
 * </dl>
 */ 
@XmlSchema(namespace="http://jaxb.dev.java.net/xjc/model",elementFormDefault=QUALIFIED)
package org.glassfish.jaxb.core.v2.model.core;

import jakarta.xml.bind.annotation.XmlSchema;

import static jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED;



