/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletContext;

/**
 * Modified version of the {@link URLClassLoader} that tries
 * to load classes from itself before delegating to the parent.
 * 
 * This class loader is used to workaround the bug in JWSDP1.2.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class XJCClassLoader extends URLClassLoader {
    
    private XJCClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

//    private XJCClassLoader(URL[] urls) {
//        super(urls);
//    }
//
//    private XJCClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
//        super(urls, parent, factory);
//    }
    
    private static XJCClassLoader theInstance;
    
    public static ClassLoader getInstance( ServletContext context ) throws IOException {
        if( theInstance==null ) {
            theInstance = new XJCClassLoader( new URL[] {
                context.getResource("/WEB-INF/lib/boxed.jar"),
                context.getResource("/WEB-INF/lib/jaxb-xjc.jar"),
                context.getResource("/WEB-INF/lib/jaxb-impl.jar") },
                Thread.currentThread().getContextClassLoader() );
        }
        
        return theInstance;
    }
    
    

    public URL getResource(String name) {
        // try this class loader first
        URL url = findResource(name);
        
        if( url==null ) {
            // if fails, delegate to the parent
            url = getParent().getResource(name);
        }
        return url;
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // First, check if the class has already been loaded
        Class c = findLoadedClass(name);
        if (c == null) {
            
            if( shouldDelegate(name) )
                c = getParent().loadClass(name);
            else {
                try {
                    // try this class loader first.
                    c = findClass(name);
//                    System.out.println("found "+name);
                } catch( ClassNotFoundException e ) {
                    // if fail, delegate to the parent
                    c = getParent().loadClass(name);
                }
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }

    private boolean shouldDelegate(String name) {
        if( name.startsWith("org.xml")
        ||  name.startsWith("org.w3c")
        ||  name.startsWith("javax.xml.parsers")
        ||  name.startsWith("javax.xml.transform") )
            return true;
        else
            return false;
    }
}
