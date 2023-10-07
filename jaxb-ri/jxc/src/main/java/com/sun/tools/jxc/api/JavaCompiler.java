/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.api;

import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;

import javax.annotation.processing.ProcessingEnvironment;


/**
 * Java-to-Schema compiler.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
@SuppressWarnings({"removal"})
public interface JavaCompiler extends com.sun.tools.xjc.api.JavaCompiler {

    /**
     * Compiles the given annotated Java source code.
     *
     * <p>
     * This operation takes a set of "root types", then compute the list of
     * all the types that need to be bound by forming a transitive reflexive
     * closure of types that are referenced by the root types.
     *
     * <p>
     * Errors will be sent to {@link javax.annotation.processing.ProcessingEnvironment#getMessager()}.
     *
     * @param rootTypes
     *      The list of types that needs to be bound to XML.
     *      "root references" from JAX-RPC to JAXB is always in the form of (type,annotations) pair.
     *
     * @param additionalElementDecls
     *      Add element declarations for the specified element names to
     *      the XML types mapped from the corresponding {@link Reference}s.
     *      Those {@link Reference}s must be included in the {@code rootTypes} parameter.
     *      In this map, a {@link Reference} can be null, in which case the element name is
     *      declared to have an empty complex type.
     *      ({@code <xs:element name='foo'><xs:complexType/></xs:element>})
     *      This parameter can be null, in which case the method behaves as if the empty map is given.
     *
     * @param defaultNamespaceRemap
     *      If not-null, all the uses of the empty default namespace ("") will
     *      be replaced by this namespace URI.
     *
     * @param source
     *      The caller supplied view to the annotated source code that JAXB is going to process.
     *
     * @return
     *      Non-null if no error was reported. Otherwise null.
     */
    J2SJAXBModel bind(
            Collection<Reference> rootTypes,
            Map<QName, Reference> additionalElementDecls,
            ProcessingEnvironment source,
            String defaultNamespaceRemap);
}
