/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect.opt;

import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link ClassLoader} used to "inject" optimized accessor classes
 * into the VM.
 *
 * <p>
 * Its parent class loader needs to be set to the one that can see the user
 * class.
 *
 * @author Kohsuke Kawaguchi
 */
final class Injector {

    /**
     * {@link Injector}s keyed by their parent {@link ClassLoader}.
     *
     * We only need one injector per one user class loader.
     */
    private static final ReentrantReadWriteLock irwl = new ReentrantReadWriteLock();
    private static final Lock ir = irwl.readLock();
    private static final Lock iw = irwl.writeLock();
    private static final Map<ClassLoader, WeakReference<Injector>> injectors =
            new WeakHashMap<>();
    private static final Logger logger = Logger.getLogger(Injector.class.getName());
    /**
     * Injects a new class into the given class loader.
     *
     * @return null
     *      if it fails to inject.
     */
    static Class inject(ClassLoader cl, String className, byte[] image) {
        Injector injector = get(cl);
        if (injector != null) {
            return injector.inject(className, image);
        } else {
            return null;
        }
    }

    /**
     * Returns the already injected class, or null.
     */
    static Class find(ClassLoader cl, String className) {
        Injector injector = get(cl);
        if (injector != null) {
            return injector.find(className);
        } else {
            return null;
        }
    }

    /**
     * Gets or creates an  for the given class loader.
     *
     * @return null
     *      if it fails.
     */
    private static Injector get(ClassLoader cl) {
        Injector injector = null;
        WeakReference<Injector> wr;
        ir.lock();
        try {
            wr = injectors.get(cl);
        } finally {
            ir.unlock();
        }
        if (wr != null) {
            injector = wr.get();
        }
        if (injector == null) {
            try {
                wr = new WeakReference<>(injector = new Injector(cl));
                iw.lock();
                try {
                    if (!injectors.containsKey(cl)) {
                        injectors.put(cl, wr);
                    }
                } finally {
                    iw.unlock();
                }
            } catch (SecurityException e) {
                logger.log(Level.FINE, "Unable to set up a back-door for the injector", e);
                return null;
            }
        }
        return injector;
    }
    /**
     * Injected classes keyed by their names.
     */
    private final Map<String, Class> classes = new HashMap<>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();
    private final ClassLoader parent;
    /**
     * True if this injector is capable of injecting accessors.
     * False otherwise, which happens if this classloader can't see {@link Accessor}.
     */
    private final boolean loadable;
    private static Method defineClass;
    private static Method resolveClass;
    private static Method findLoadedClass;
    private static Object U;

    static {
        try {
            Method[] m = AccessController.doPrivileged(
                    new PrivilegedAction<>() {
                        @Override
                        public Method[] run() {
                            return new Method[]{
                                    getMethod(ClassLoader.class, "defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE),
                                    getMethod(ClassLoader.class, "resolveClass", Class.class),
                                    getMethod(ClassLoader.class, "findLoadedClass", String.class)
                            };
                        }
                    }
            );
            defineClass = m[0];
            resolveClass = m[1];
            findLoadedClass = m[2];
        } catch (Throwable t) {
            try {
                U = AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    @Override
                    public Object run() throws Exception {
                        Class u = classForNames("sun.misc.Unsafe", "jdk.internal.misc.Unsafe");
                        Field theUnsafe = u.getDeclaredField("theUnsafe");
                        theUnsafe.setAccessible(true);
                        return theUnsafe.get(null);
                    }
                });
                defineClass = AccessController.doPrivileged(new PrivilegedExceptionAction<>() {
                    @Override
                    public Method run() throws Exception {
                        try {
                            return U.getClass().getMethod("defineClass",
                                    String.class,
                                    byte[].class,
                                    Integer.TYPE,
                                    Integer.TYPE,
                                    ClassLoader.class,
                                    ProtectionDomain.class);
                        } catch (NoSuchMethodException | SecurityException ex) {
                            throw ex;
                        }
                    }
                });
            } catch (SecurityException | PrivilegedActionException ex) {
                Logger.getLogger(Injector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static Class classForNames(String... names) throws ClassNotFoundException {
        for (String name : names) {
            try {
                return Class.forName(name);
            } catch (ClassNotFoundException e) {
            }
        }

        throw new ClassNotFoundException(
                String.format("No class found for supplied FQDNs %s",
                        Arrays.toString(names)));
    }

    private static Method getMethod(final Class<?> c, final String methodname, final Class<?>... params) {
        try {
            Method m = c.getDeclaredMethod(methodname, params);
            m.setAccessible(true);
            return m;
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    private Injector(ClassLoader parent) {
        this.parent = parent;
        assert parent != null;

        boolean loadableCheck = false;

        try {
            loadableCheck = parent.loadClass(Accessor.class.getName()) == Accessor.class;
        } catch (ClassNotFoundException e) {
            // not loadable
        }

        this.loadable = loadableCheck;
    }

    @SuppressWarnings("LockAcquiredButNotSafelyReleased")
    private Class inject(String className, byte[] image) {
        if (!loadable) // this injector cannot inject anything
        {
            return null;
        }

        boolean wlocked = false;
        boolean rlocked = false;
        try {

            r.lock();
            rlocked = true;

            Class c = classes.get(className);

            // Unlock now during the findLoadedClass process to avoid
            // deadlocks
            r.unlock();
            rlocked = false;

            //find loaded class from classloader
            if (c == null && findLoadedClass != null) {

                try {
                    c = (Class) findLoadedClass.invoke(parent, className.replace('/', '.'));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.log(Level.FINE, "Unable to find " + className, e);
                } catch (InvocationTargetException e) {
                    Throwable t = e.getTargetException();
                    logger.log(Level.FINE, "Unable to find " + className, t);
                }

                if (c != null) {

                    w.lock();
                    wlocked = true;

                    classes.put(className, c);

                    w.unlock();
                    wlocked = false;

                    return c;
                }
            }

            if (c == null) {

                r.lock();
                rlocked = true;

                c = classes.get(className);

                // Unlock now during the define/resolve process to avoid
                // deadlocks
                r.unlock();
                rlocked = false;

                if (c == null) {

                    // we need to inject a class into the
                    try {
                        if (resolveClass != null) {
                            c = (Class) defineClass.invoke(parent, className.replace('/', '.'), image, 0, image.length);
                            resolveClass.invoke(parent, c);
                        } else {
                            c = (Class) defineClass.invoke(U, className.replace('/', '.'), image, 0, image.length, parent, Injector.class.getProtectionDomain());
                        }
                    } catch (IllegalAccessException | LinkageError | SecurityException e) {
                        logger.log(Level.FINE, "Unable to inject " + className, e);
                        return null;
                    } catch (InvocationTargetException e) {
                        Throwable t = e.getTargetException();
                        if (t instanceof LinkageError) {
                            logger.log(Level.FINE, "duplicate class definition bug occured? Please report this : " + className, t);
                        } else {
                            logger.log(Level.FINE, "Unable to inject " + className, t);
                        }
                        return null;
                    }

                    w.lock();
                    wlocked = true;

                    // During the time we were unlocked, we could have tried to
                    // load the class from more than one thread. Check now to see
                    // if someone else beat us to registering this class
                    if (!classes.containsKey(className)) {
                        classes.put(className, c);
                    }

                    w.unlock();
                    wlocked = false;
                }
            }
            return c;
        } finally {
            if (rlocked) {
                r.unlock();
            }
            if (wlocked) {
                w.unlock();
            }
        }
    }

    private Class find(String className) {
        r.lock();
        try {
            return classes.get(className);
        } finally {
            r.unlock();
        }
    }
}
