/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Defines a set of annotations that can be used on TypedXmlWriter interfaces.
 * <h2>Package-level Annotation</h2>
 * <p>
 * {@link com.sun.xml.txw2.annotation.XmlNamespace} can be used on a package to designate the namespace URI for the
 * whole package.
 * <h2>Interface Annotation</h2>
 * <p>
 * {@link com.sun.xml.txw2.annotation.XmlElement} can be used on TypedXmlWriter-derived interfaces to associate
 * a tag name to that interface.
 * <h2>Method Annotations</h2>
 * <p>
 * {@link com.sun.xml.txw2.annotation.XmlElement}, {@link com.sun.xml.txw2.annotation.XmlAttribute}, or {@link com.sun.xml.txw2.annotation.XmlValue} can be used on a method
 * declared on a TypedXmLWriter-derived interface. Those annotations are mutually-exclusive.
 * See their javadoc for details. If none of the above three annotations are specified,
 * {@link com.sun.xml.txw2.annotation.XmlElement} is assumed.
 */
package com.sun.xml.txw2.annotation;