/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * CLI entry point to schemagen that checks for JDK 5.0
 * @author Kohsuke Kawaguchi
 */
public class SchemaGeneratorFacade {

    public static void main(String[] args) throws Throwable {
        try {
            ClassLoader cl = SecureLoader.getClassClassLoader(SchemaGeneratorFacade.class);
            if(cl==null)    cl = SecureLoader.getSystemClassLoader();

            Class driver = cl.loadClass("com.sun.tools.jxc.SchemaGenerator");
            Method mainMethod = driver.getDeclaredMethod("main", new Class[]{String[].class});
            try {
                mainMethod.invoke(null,new Object[]{args});
            } catch (IllegalAccessException e) {
                throw e;
            } catch (InvocationTargetException e) {
                if(e.getTargetException()!=null)
                    throw e.getTargetException();
            }
        } catch (UnsupportedClassVersionError e) {
            System.err.println("schemagen requires JDK 6.0 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads");
        }
    }
}
