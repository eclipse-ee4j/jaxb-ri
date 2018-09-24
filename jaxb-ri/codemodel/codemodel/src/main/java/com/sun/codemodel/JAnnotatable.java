/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Annotatable program elements.
 *
 * @author Kohsuke Kawaguchi
 */
public interface JAnnotatable {
    /**
     * Adds an annotation to this program element.
     * @param clazz
     *          The annotation class to annotate the program element with
     */
    JAnnotationUse annotate(JClass clazz);

    /**
     * Adds an annotation to this program element.
     *
     * @param clazz
     *          The annotation class to annotate the program element with
     */
    JAnnotationUse annotate(Class <? extends Annotation> clazz);

    /**
     * Removes annotation from this program element.
     *
     * @param annotation
     *          The annotation to be removed from the program element
     */
    boolean removeAnnotation(JAnnotationUse annotation);

    /**
     * Adds an annotation to this program element
     * and returns a type-safe writer to fill in the values of such annotations.
     */
    <W extends JAnnotationWriter> W annotate2(Class<W> clazz);
    
    /**
     * Read-only live view of all annotations on this {@link JAnnotatable}
     * 
     * @return
     *      Can be empty but never null.
     */
    Collection<JAnnotationUse> annotations();
}
