/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.reflect.opt;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.bind.Util;
import com.sun.xml.bind.v2.bytecode.ClassTailor;

/**
 * @author Kohsuke Kawaguchi
 */
class AccessorInjector {

    private static final Logger logger = Util.getClassLogger();

    protected static final boolean noOptimize =
        Util.getSystemProperty(ClassTailor.class.getName()+".noOptimize")!=null;

    static {
        if(noOptimize)
            logger.info("The optimized code generation is disabled");
    }

    /**
     * Customizes a class file by replacing constant pools.
     *
     * @param templateClassName
     *      The resource that contains the template class file.
     * @param replacements
     *      A list of pair of strings that specify the substitution
     *      {@code String[]{search_0, replace_0, search_1, replace_1, ..., search_n, replace_n }
     *
     *      The search strings found in the constant pool will be replaced by the corresponding
     *      replacement string.
     */
    private static byte[] tailor( String templateClassName, String newClassName, String... replacements ) {
        InputStream resource;
        if(CLASS_LOADER!=null)
            resource = CLASS_LOADER.getResourceAsStream(templateClassName+".class");
        else
            resource = ClassLoader.getSystemResourceAsStream(templateClassName+".class");
        if(resource==null)
            return null;

        return ClassTailor.tailor(resource,templateClassName,newClassName,replacements);
    }

    private static final ClassLoader CLASS_LOADER = SecureLoader.getClassClassLoader(AccessorInjector.class);
    
}
