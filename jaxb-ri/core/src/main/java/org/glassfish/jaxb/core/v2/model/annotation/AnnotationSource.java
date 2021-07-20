/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.annotation;

import java.lang.annotation.Annotation;

/**
 * Implemented by objects that can have annotations.
 *
 * @author Kohsuke Kawaguchi
 */
public interface AnnotationSource {
    /**
     * Gets the value of the specified annotation from the given property.
     *
     * <p>
     * When this method is used for a property that consists of a getter and setter,
     * it returns the annotation on either of those methods. If both methods have
     * the same annotation, it is an error.
     *
     * @return
     *      null if the annotation is not present.
     */
    <A extends Annotation> A readAnnotation(Class<A> annotationType);

    /**
     * Returns true if the property has the specified annotation.
     * <p>
     * Short for <code>readAnnotation(annotationType)!=null</code>,
     * but this method is typically faster.
     */
    boolean hasAnnotation(Class<? extends Annotation> annotationType);
}
