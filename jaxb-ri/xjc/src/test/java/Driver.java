/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import com.sun.tools.xjc.XJCFacade;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Alias of {@link com.sun.tools.xjc.Driver}, just to make testing easier.
 */
public class Driver {

    public static void main( String[] args ) throws Exception, Throwable {

        String v = "2.0";      // by default, we go 2.0

        for( int i=0; i<args.length; i++ ) {
            if(args[i].equals("-source")) {
                if(i+1<args.length) {
                    v = ClassLoaderBuilder.parseVersion(args[i+1]);
                }
            }
        }

        try {
            ClassLoader cl = ClassLoaderBuilder.createProtectiveClassLoader(XJCFacade.class.getClassLoader(), v);
            if (cl != null) {
                cl.setPackageAssertionStatus("com.sun", true);
            }

            Class driver = cl.loadClass("com.sun.tools.xjc.Driver");
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
            System.err.println("XJC requires JDK 5.0 or later. Please download it from http://java.sun.com/j2se/1.5/");
        }
    }

}
