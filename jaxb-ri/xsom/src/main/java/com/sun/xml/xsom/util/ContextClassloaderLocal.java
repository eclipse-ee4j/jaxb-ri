/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.WeakHashMap;

/**
 * Simple utility ensuring that the value is cached only in case it is non-internal implementation
 */
abstract class ContextClassloaderLocal<V> {

    private static final String FAILED_TO_CREATE_NEW_INSTANCE = "FAILED_TO_CREATE_NEW_INSTANCE";

    private WeakHashMap<ClassLoader, V> CACHE = new WeakHashMap<ClassLoader, V>();

    public V get() throws Error {
        ClassLoader tccl = getContextClassLoader();
        V instance = CACHE.get(tccl);
        if (instance == null) {
            instance = createNewInstance();
            CACHE.put(tccl, instance);
        }
        return instance;
    }

    public void set(V instance) {
        CACHE.put(getContextClassLoader(), instance);
    }

    protected abstract V initialValue() throws Exception;

    private V createNewInstance() {
        try {
            return initialValue();
        } catch (Exception e) {
            throw new Error(format(FAILED_TO_CREATE_NEW_INSTANCE, getClass().getName()), e);
        }
    }

    private static String format(String property, Object... args) {
        String text = ResourceBundle.getBundle(ContextClassloaderLocal.class.getName()).getString(property);
        return MessageFormat.format(text, args);
    }

    private static ClassLoader getContextClassLoader() {
        return (ClassLoader)
                AccessController.doPrivileged(new PrivilegedAction() {
                    public Object run() {
                        ClassLoader cl = null;
                        try {
                            cl = Thread.currentThread().getContextClassLoader();
                        } catch (SecurityException ex) {
                        }
                        return cl;
                    }
                });
    }
}

