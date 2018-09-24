/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom;

import org.xml.sax.Locator;
import com.sun.xml.xsom.parser.AnnotationParser;

/**
 * <a href="http://www.w3.org/TR/xmlschema-1/#Annotation_details">
 * XML Schema annotation</a>.
 * 
 * 
 */
public interface XSAnnotation
{
    /**
     * Obtains the application-parsed annotation.
     * <p>
     * annotations are parsed by the user-specified
     * {@link AnnotationParser}.
     * 
     * @return may return null
     */
    Object getAnnotation();

    /**
     * Sets the value to be returned by {@link #getAnnotation()}.
     *
     * @param o
     *      can be null.
     * @return
     *      old value that was replaced by the {@code o}.
     */
    Object setAnnotation(Object o);

    /**
     * Returns a location information of the annotation.
     */
    Locator getLocator();
}
