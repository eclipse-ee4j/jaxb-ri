/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api;

import com.sun.tools.xjc.api.impl.s2j.SchemaCompilerImpl;
import org.glassfish.jaxb.core.api.impl.NameConverter;

/**
 * Entry point to the programatic API to access
 * schema compiler (XJC) and schema generator (schemagen).
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class XJC {

    /**
     * Gets a fresh {@link SchemaCompiler}.
     * 
     * @return
     *      always return non-null object.
     */
    public static SchemaCompiler createSchemaCompiler() {
        return new SchemaCompilerImpl();
    }

    /**
     * Computes the namespace {@code URI -> package name} conversion
     * as specified by the JAXB spec.
     *
     * @param namespaceUri
     *      Namespace URI. Can be empty, but must not be null.
     * @return
     *      A Java package name (e.g., "foo.bar"). "" to represent the root package.
     *      This method returns null if the method fails to derive the package name
     *      (there are certain namespace URIs with which this algorithm does not
     *      work --- such as ":::" as the URI.)
     */
    public static String getDefaultPackageName( String namespaceUri ) {
        if(namespaceUri==null)   throw new IllegalArgumentException();
        return NameConverter.standard.toPackageName( namespaceUri );
    }
}
