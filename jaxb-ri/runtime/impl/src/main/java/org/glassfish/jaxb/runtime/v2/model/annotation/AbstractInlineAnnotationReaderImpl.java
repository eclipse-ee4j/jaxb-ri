/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.annotation;

import org.glassfish.jaxb.core.v2.model.annotation.AnnotationReader;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.ErrorHandler;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;

import java.lang.annotation.Annotation;

/**
 * {@link AnnotationReader} that reads annotation from classes,
 * not from external binding files.
 *
 * This is meant to be used as a convenient partial implementation.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public abstract class AbstractInlineAnnotationReaderImpl<T,C,F,M>
    implements AnnotationReader<T,C,F,M> {

    private ErrorHandler errorHandler;

    public void setErrorHandler(ErrorHandler errorHandler) {
        if(errorHandler==null)
            throw new IllegalArgumentException();
        this.errorHandler = errorHandler;
    }

    /**
     * Always return a non-null valid {@link ErrorHandler}
     */
    public final ErrorHandler getErrorHandler() {
        assert errorHandler!=null : "error handler must be set before use";
        return errorHandler;
    }

    public final <A extends Annotation> A getMethodAnnotation(Class<A> annotation, M getter, M setter, Locatable srcPos) {
        A a1 = getter==null?null:getMethodAnnotation(annotation,getter,srcPos);
        A a2 = setter==null?null:getMethodAnnotation(annotation,setter,srcPos);

        if(a1==null) {
            if(a2==null)
                return null;
            else
                return a2;
        } else {
            if(a2==null)
                return a1;
            else {
                // both are present
                getErrorHandler().error(new IllegalAnnotationException(
                    Messages.DUPLICATE_ANNOTATIONS.format(
                        annotation.getName(), fullName(getter),fullName(setter)),
                    a1, a2 ));
                // recover by ignoring one of them
                return a1;
            }
        }
    }

    public boolean hasMethodAnnotation(Class<? extends Annotation> annotation, String propertyName, M getter, M setter, Locatable srcPos) {
        boolean x = ( getter != null && hasMethodAnnotation(annotation, getter) );
        boolean y = ( setter != null && hasMethodAnnotation(annotation, setter) );

        if(x && y) {
            // both are present. have getMethodAnnotation report an error
            getMethodAnnotation(annotation,getter,setter,srcPos);
        }

        return x||y;
    }

    /**
     * Gets the fully-qualified name of the method.
     *
     * Used for error messages.
     */
    protected abstract String fullName(M m);
}
