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

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.annotation.XmlAnyElement;

/**
 * Dynamically locates classes to represent elements discovered during the unmarshalling.
 *
 * <p>
 * <b>THIS INTERFACE IS SUBJECT TO CHANGE WITHOUT NOTICE.</b>
 *
 * <h2>Background</h2>
 * <p>
 * {@link JAXBContext#newInstance(Class...)} requires that application informs JAXB
 * about all the classes that it may see in the instance document. While this allows
 * JAXB to take time to optimize the unmarshalling, it is sometimes inconvenient
 * for applications.
 *
 * <p>
 * This is where {@link ClassResolver} comes to resucue.
 *
 * <p>
 * A {@link ClassResolver} instance can be specified on {@link Unmarshaller} via
 * {@link Unmarshaller#setProperty(String, Object)} as follows:
 *
 * <pre>
 * unmarshaller.setProperty( ClassResolver.class.getName(), new MyClassResolverImpl() );
 * </pre>
 *
 * <p>
 * When an {@link Unmarshaller} encounters (i) an unknown root element or (ii) unknown
 * elements where unmarshaller is trying to unmarshal into {@link XmlAnyElement} with
 * {@code lax=true}, unmarshaller calls {@link #resolveElementName(String, String)}
 * method to see if the application may be able to supply a class that corresponds
 * to that class.
 *
 * <p>
 * When a {@link Class} is returned, a new {@link JAXBContext} is created with
 * all the classes known to it so far, plus a new class returned. This operation
 * may fail (for example because of some conflicting annotations.) This failure
 * is handled just like {@link Exception}s thrown from
 * {@link ClassResolver#resolveElementName(String, String)}.
 *
 * @author Kohsuke Kawaguchi
 * @since 2.1
 */
public abstract class ClassResolver {

    /**
     * Default constructor.
     */
    protected ClassResolver() {}

    /**
     * JAXB calls this method when it sees an unknown element.
     *
     * <p>
     * See the class javadoc for details.
     *
     * @param nsUri
     *      Namespace URI of the unknown element. Can be empty but never null.
     * @param localName
     *      Local name of the unknown element. Never be empty nor null.
     *
     * @return
     *      If a non-null class is returned, it will be used to unmarshal this element.
     *      If null is returned, the resolution is assumed to be failed, and
     *      the unmarshaller will behave as if there was no
     *      to begin with (that is, to report it to {@link ValidationEventHandler},
     *      then move on.)
     *
     * @throws Exception
     *      Throwing any {@link RuntimeException} causes the unmarshaller to stop
     *      immediately. The exception will be propagated up the call stack.
     *      Throwing any other checked {@link Exception} results in the error
     *      reproted to {@link ValidationEventHandler} (just like any other error
     *      during the unmarshalling.)
     */
    public abstract @Nullable Class<?> resolveElementName(@NotNull String nsUri, @NotNull String localName) throws Exception;
}
