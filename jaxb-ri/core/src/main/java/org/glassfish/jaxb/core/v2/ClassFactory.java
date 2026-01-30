/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jaxb.core.Utils;

/**
 * Creates new instances of classes.
 *
 * <p>
 * This code handles the case where the class is not public or the constructor is
 * not public.
 *
 * @since 2.0
 * @author Kohsuke Kawaguchi
 */
public final class ClassFactory {
    private static final Class[] emptyClass = new Class[0];
    private static final Object[] emptyObject = new Object[0];

    private static final Logger logger = Utils.getClassLogger();

    /**
     * Cache from a class to its default constructor.
     */
    private static final ClassValue<Constructor> DECLARED_CTORS =
            new ClassValue<>() {
                @Override
                protected Constructor computeValue(Class<?> clazz) {
                    Constructor cons;
                    if (System.getSecurityManager() == null) {
                        cons = tryGetDeclaredConstructor(clazz);
                    } else {
                        cons = AccessController.doPrivileged(
                                (PrivilegedAction<Constructor<?>>) () -> tryGetDeclaredConstructor(clazz)
                        );
                    }

                    int classMod = clazz.getModifiers();

                    if(!Modifier.isPublic(classMod) || !Modifier.isPublic(cons.getModifiers())) {
                        // attempt to make it work even if the constructor is not accessible
                        try {
                            cons.setAccessible(true);
                        } catch(SecurityException e) {
                            // but if we don't have a permission to do so, work gracefully.
                            logger.log(Level.FINE, e, () -> "Unable to make the constructor of "+clazz+" accessible");
                            throw e;
                        }
                    }
                    return cons;
                }
            };

    private ClassFactory() {}

    /**
     * @deprecated this method is empty by now and will be removed in next major version
     * @see <a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/978">Issue 978</a>
     */
    @Deprecated(forRemoval = true)
    public static void cleanCache() {
    }

    /**
     * Creates a new instance of the class but throw exceptions without catching it.
     */
    public static <T> T create0( final Class<T> clazz ) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> cons = DECLARED_CTORS.get(clazz);
        return cons.newInstance(emptyObject);
    }

    private static <T> Constructor<T> tryGetDeclaredConstructor(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor(emptyClass);
        } catch (NoSuchMethodException e) {
            logger.log(Level.INFO,e, () -> "No default constructor found on "+clazz);
            NoSuchMethodError exp;
            if(clazz.getDeclaringClass()!=null && !Modifier.isStatic(clazz.getModifiers())) {
                exp = new NoSuchMethodError(Messages.NO_DEFAULT_CONSTRUCTOR_IN_INNER_CLASS
                                                    .format(clazz.getName()));
            } else {
                exp = new NoSuchMethodError(e.getMessage());
            }
            exp.initCause(e);
            throw exp;
        }
    }

    /**
     * The same as {@link #create0} but with an error handling to make
     * the instantiation error fatal.
     */
    public static <T> T create( Class<T> clazz ) {
        try {
            return create0(clazz);
        } catch (InstantiationException e) {
            logger.log(Level.INFO, e, () -> "failed to create a new instance of "+clazz);
            throw new InstantiationError(e.toString());
        } catch (IllegalAccessException e) {
            logger.log(Level.INFO, e, () -> "failed to create a new instance of "+clazz);
            throw new IllegalAccessError(e.toString());
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();

            // most likely an error on the user's code.
            // just let it through for the ease of debugging
            if(target instanceof RuntimeException)
                throw (RuntimeException)target;

            // error. just forward it for the ease of debugging
            if(target instanceof Error)
                throw (Error)target;

            // a checked exception.
            // not sure how we should report this error,
            // but for now we just forward it by wrapping it into a runtime exception
            throw new IllegalStateException(target);
        }
    }

    /**
     *  Call a method in the factory class to get the object.
     */
    public static Object create(Method method) {
        Throwable errorMsg;
        try {
            return method.invoke(null, emptyObject);
        } catch (InvocationTargetException ive) {
            Throwable target = ive.getTargetException();

            if(target instanceof RuntimeException)
                throw (RuntimeException)target;

            if(target instanceof Error)
                throw (Error)target;

            throw new IllegalStateException(target);
        } catch (IllegalAccessException e) {
            logger.log(Level.INFO, e, () -> "failed to create a new instance of "+method.getReturnType().getName());
            throw new IllegalAccessError(e.toString());
        } catch (IllegalArgumentException | NullPointerException | ExceptionInInitializerError iae){
            logger.log(Level.INFO, iae, () -> "failed to create a new instance of "+method.getReturnType().getName());
            errorMsg = iae;
        }

        NoSuchMethodError exp;
        exp = new NoSuchMethodError(errorMsg.getMessage());
        exp.initCause(errorMsg);
        throw exp;
    }

    /**
     * Infers the instantiable implementation class that can be assigned to the given field type.
     *
     * @return null
     *      if inference fails.
     */
    public static <T> Class<? extends T> inferImplClass(Class<T> fieldType, Class[] knownImplClasses) {
        if(!fieldType.isInterface())
            return fieldType;

        for( Class<?> impl : knownImplClasses ) {
            if(fieldType.isAssignableFrom(impl))
                return impl.asSubclass(fieldType);
        }

        // if we can't find an implementation class,
        // let's just hope that we will never need to create a new object,
        // and returns null
        return null;
    }
}
