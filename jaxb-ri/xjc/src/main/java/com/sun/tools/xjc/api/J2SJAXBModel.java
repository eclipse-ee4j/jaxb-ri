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

import java.io.IOException;

import jakarta.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;

/**
 * {@link JAXBModel} that exposes additional information available
 * only for the {@code java -> schema} direction.
 *
 * @author Kohsuke Kawaguchi
 */
public interface J2SJAXBModel extends JAXBModel {
    /**
     * Returns the name of the XML Type bound to the
     * specified Java type.
     *
     * @param javaType
     *      must not be null. This must be one of the {@link Reference}s specified
     *      in the {@link JavaCompiler#bind} method.
     *
     * @return
     *      null if it is not a part of the input to {@link JavaCompiler#bind}.
     *
     * @throws IllegalArgumentException
     *      if the parameter is null
     */
    QName getXmlTypeName(Reference javaType);

    /**
     * Generates the schema documents from the model.
     *
     * @param outputResolver
     *      this object controls the output to which schemas
     *      will be sent.
     *
     * @throws IOException
     *      if {@link SchemaOutputResolver} throws an {@link IOException}.
     */
    void generateSchema(SchemaOutputResolver outputResolver, ErrorListener errorListener) throws IOException;

    /**
     * Generates the episode file from the model.
     *
     * <p>
     * The "episode file" is really just a JAXB customization file (but with vendor extensions,
     * at this point), that can be used later with a schema compilation to support separate
     * compilation.
     *
     * @param output
     *      This receives the generated episode file.
     * @since 2.1
     */
    void generateEpisodeFile(Result output);
}
