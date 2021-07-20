/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * Alias of {@link com.sun.tools.jxc.SchemaGenerator}, just to make testing easier.
 *
 * @author Kohsuke Kawaguchi
 */
public class SchemaGenerator {

    public static void main( String[] args ) throws Exception, Throwable {

        String v = "3.0";      // by default, we go 3.0

        for( int i=0; i<args.length; i++ ) {
            if(args[i].equals("-source")) {
                if(i+1<args.length) {
                    v = XJCFacade.parseVersion(args[i+1]);
                }
            }
        }

        try {
            ClassLoader cl = SchemaGenerator.class.getClassLoader();
            if (cl != null) {
                cl.setPackageAssertionStatus("com.sun", true);
            }

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
            System.err.println("JXC requires Java SE 8 or later. Please download it from http://www.oracle.com/technetwork/java/javase/downloads");
        }
    }

}
