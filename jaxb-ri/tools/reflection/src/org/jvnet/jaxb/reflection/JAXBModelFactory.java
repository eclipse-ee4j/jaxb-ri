/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.jvnet.jaxb.reflection;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.bind.v2.model.annotation.RuntimeAnnotationReader;
import com.sun.xml.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.bind.v2.model.core.ErrorHandler;
import com.sun.xml.bind.v2.model.core.Ref;
import com.sun.xml.bind.v2.model.core.TypeInfoSet;
import com.sun.xml.bind.v2.model.impl.ModelBuilder;
import com.sun.xml.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.bind.v2.model.nav.Navigator;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationsException;

/**
 * Factory methods to build JAXB models.
 *
 * @author Kohsuke Kawaguchi
 */
// this is a facade to ModelBuilder
public abstract class JAXBModelFactory {
    private JAXBModelFactory() {} // no instanciation please

    /**
     * Creates a new JAXB model from
     * classes represented in arbitrary reflection library.
     *
     * @param reader
     *      used to read annotations from classes. must not be null.
     * @param navigator
     *      abstraction layer of the underlying Java reflection library.
     *      must not be null.
     * @param errorHandler
     *      Receives errors found during the processing.
     *
     * @return
     *      null if any error was reported during the processing.
     *      If no error is reported, a non-null valid object.
     */
    public static <T,C,F,M> TypeInfoSet<T,C,F,M> create(
        AnnotationReader<T,C,F,M> reader,
        Navigator<T,C,F,M> navigator,
        ErrorHandler errorHandler,
        Collection<C> classes ) {

        ModelBuilder<T,C,F,M> builder = new ModelBuilder<T,C,F,M>(reader,navigator,Collections.<C,C>emptyMap(),null);
        builder.setErrorHandler(errorHandler);
        for( C c : classes )
            builder.getTypeInfo(new Ref<T,C>(navigator.use(c)));

        return builder.link();
    }

    /**
     * Creates a new JAXB model from
     * classes represented in <tt>java.lang.reflect</tt>.
     *
     * @param reader
     *      used to read annotations from classes. must not be null.
     * @param errorHandler
     *      Receives errors found during the processing.
     *
     * @return
     *      null if any error was reported during the processing.
     *      If no error is reported, a non-null valid object.
     */
    public static RuntimeTypeInfoSet create(
        RuntimeAnnotationReader reader,
        ErrorHandler errorHandler,
        Class... classes ) {

        RuntimeModelBuilder builder = new RuntimeModelBuilder(null,reader,Collections.<Class,Class>emptyMap(),null);
        builder.setErrorHandler(errorHandler);
        for( Class c : classes )
            builder.getTypeInfo(new Ref<Type,Class>(c));

        return builder.link();
    }

    /**
     * Creates a new JAXB model from
     * classes represented in <tt>java.lang.reflect</tt>.
     *
     * <p>
     * This version reads annotations from the classes directly.
     *
     * @param errorHandler
     *      Receives errors found during the processing.
     *
     * @return
     *      null if any error was reported during the processing.
     *      If no error is reported, a non-null valid object.
     */
    public static RuntimeTypeInfoSet create(
        ErrorHandler errorHandler,
        Class... classes ) {

        return create( new RuntimeInlineAnnotationReader(), errorHandler, classes );
    }

    /**
     * Creates a new JAXB model from
     * classes represented in <tt>java.lang.reflect</tt>.
     *
     * <p>
     * This version reads annotations from the classes directly,
     * and throw any error reported as an exception
     *
     * @return
     *      null if any error was reported during the processing.
     *      If no error is reported, a non-null valid object.
     * @throws IllegalAnnotationsException
     *      if there was any incorrect use of annotations in the specified set of classes.
     */
    public static RuntimeTypeInfoSet create(Class... classes ) throws IllegalAnnotationsException {
        IllegalAnnotationsException.Builder errorListener = new IllegalAnnotationsException.Builder();
        RuntimeTypeInfoSet r = create(errorListener, classes);
        errorListener.check();
        return r;
    }
}
