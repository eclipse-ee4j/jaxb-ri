/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;

/**
 * A shabby driver to invoke XJC1 or XJC2 depending on the command line switch.
 *
 * <p>
 * This class is compiled with -source 1.2 so that we can report a nice
 * user-friendly "you require Tiger" error message.
 *
 * @author Kohsuke Kawaguchi
 */
public class XJCFacade {

    private static final String JDK_REQUIRED = "XJC requires Java SE 8 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads";

    public static void main(String[] args) throws Throwable {
        String v = "3.0";      // by default, we go 3.0

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-source")) {
                if (i + 1 < args.length) {
                    v = parseVersion(args[i + 1]);
                }
            }
        }

        ClassLoader oldContextCl = SecureLoader.getContextClassLoader();
        try {
            ClassLoader cl = SecureLoader.getClassClassLoader(XJCFacade.class);
            SecureLoader.setContextClassLoader(cl);
            Class<?> driver = cl.loadClass("com.sun.tools.xjc.Driver");
            Method mainMethod = driver.getDeclaredMethod("main", new Class[]{String[].class});
            try {
                mainMethod.invoke(null, new Object[]{args});
            } catch (InvocationTargetException e) {
                if (e.getTargetException() != null) {
                    throw e.getTargetException();
                }
            }
        } catch (UnsupportedClassVersionError e) {
            System.err.println(JDK_REQUIRED);
        } finally {
            ClassLoader cl = SecureLoader.getContextClassLoader();
            SecureLoader.setContextClassLoader(oldContextCl);

            //close/cleanup all classLoaders but the one which loaded this class
            while (cl != null && !oldContextCl.equals(cl)) {
                if (cl instanceof Closeable) {
                    //JDK7+, ParallelWorldClassLoader
                    ((Closeable) cl).close();
                } else {
                    if (cl instanceof URLClassLoader) {
                        //JDK6 - API jars are loaded by instance of URLClassLoader
                        //so use proprietary API to release holded resources
                        try {
                            Class<?> clUtil = oldContextCl.loadClass("sun.misc.ClassLoaderUtil");
                            Method release = clUtil.getDeclaredMethod("releaseLoader", URLClassLoader.class);
                            release.invoke(null, cl);
                        } catch (ClassNotFoundException ex) {
                            //not Sun JDK 6, ignore
                            System.err.println(JDK_REQUIRED);
                        }
                    }
                }
                cl = SecureLoader.getParentClassLoader(cl);
            }
        }
    }

    public static String parseVersion(String version) {
        // no other versions supported as of now
        return "3.0";
    }
}
