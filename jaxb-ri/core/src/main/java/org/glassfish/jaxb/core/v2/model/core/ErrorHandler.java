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

import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;

/**
 * listen to static errors found during building a JAXB model from a set of classes.
 * Implemented by the client of {@link org.glassfish.jaxb.core.v2.model.impl.ModelBuilderI}.
 *
 * <p>
 * All the static errors have to be reported while constructing a
 * model, not when a model is used (IOW, until the {@link org.glassfish.jaxb.core.v2.model.impl.ModelBuilderI} completes.
 * Internally, {@link org.glassfish.jaxb.core.v2.model.impl.ModelBuilderI} wraps an {@link ErrorHandler} and all the model
 * components should report errors through it.
 *
 * <p>
 * {@link IllegalAnnotationException} is a checked exception to remind
 * the model classes to report it rather than to throw it.
 *
 * @see org.glassfish.jaxb.core.v2.model.impl.ModelBuilderI
 * @author Kohsuke Kawaguchi
 */
public interface ErrorHandler {
    /**
     * Receives a notification for an error in the annotated code.
     * @param e
     */
    void error( IllegalAnnotationException e );
}
