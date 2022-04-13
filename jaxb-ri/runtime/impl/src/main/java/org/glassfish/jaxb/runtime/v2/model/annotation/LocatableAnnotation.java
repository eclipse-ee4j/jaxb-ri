/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.annotation;

import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.runtime.Location;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link Annotation} that also implements {@link Locatable}.
 *
 * @author Kohsuke Kawaguchi
 */
public class LocatableAnnotation implements InvocationHandler, Locatable, Location {
    private final Annotation core;

    private final Locatable upstream;

    /**
     * Wraps the annotation into a proxy so that the returned object will also implement
     * {@link Locatable}.
     */
    @SuppressWarnings({"unchecked"})
    public static <A extends Annotation> A create( A annotation, Locatable parentSourcePos ) {
        if(annotation==null)    return null;
        Class<? extends Annotation> type = annotation.annotationType();
        if(quicks.containsKey(type)) {
            // use the existing proxy implementation if available
            return (A)quicks.get(type).newInstance(parentSourcePos,annotation);
        }

        // otherwise take the slow route

        ClassLoader cl = SecureLoader.getClassClassLoader(LocatableAnnotation.class);

        try {
            Class loadableT = Class.forName(type.getName(), false, cl);
            if(loadableT !=type)
                return annotation;  // annotation type not loadable from this class loader

            return (A)Proxy.newProxyInstance(cl,
                    new Class[]{ type, Locatable.class },
                    new LocatableAnnotation(annotation,parentSourcePos));
        } catch (ClassNotFoundException e) {
            // annotation not loadable
            return annotation;
        } catch (IllegalArgumentException e) {
            // Proxy.newProxyInstance throws this if it cannot resolve this annotation
            // in this classloader
            return annotation;
        }

    }

    LocatableAnnotation(Annotation core, Locatable upstream) {
        this.core = core;
        this.upstream = upstream;
    }

    @Override
    public Locatable getUpstream() {
        return upstream;
    }

    @Override
    public Location getLocation() {
        return this;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if(method.getDeclaringClass()==Locatable.class)
                return method.invoke(this,args);
            if(Modifier.isStatic(method.getModifiers()))
                // malicious code can pass in a static Method object.
                // doing method.invoke() would end up executing it,
                // so we need to protect against it.
                throw new IllegalArgumentException();

            return method.invoke(core,args);
        } catch (InvocationTargetException e) {
            if(e.getTargetException()!=null)
                throw e.getTargetException();
            throw e;
        }
    }

    @Override
    public String toString() {
        return core.toString();
    }


    /**
     * List of {@link Quick} implementations keyed by their annotation type.
     */
    private static final Map<Class,Quick> quicks = new HashMap<>();

    static {
        for( Quick q : Init.getAll() ) {
            quicks.put(q.annotationType(),q);
        }
    }
}
