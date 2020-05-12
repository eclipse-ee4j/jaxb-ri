/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import jakarta.activation.MimeType;

import com.sun.codemodel.JExpression;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.outline.Outline;
import org.glassfish.jaxb.core.v2.model.core.ID;
import com.sun.xml.xsom.XmlString;

/**
 * Information about how another type is referenced.
 *
 * <p>
 * In practice it is often easier to use {@link CTypeInfo}
 * instead of {@link NType}, so this interface defines {@link #getInfo()}.
 *
 * @author Kohsuke Kawaguchi
 * @see TypeUseImpl
 */
public interface TypeUse {
    /**
     * If the use can hold multiple values of the specified type.
     */
    boolean isCollection();

    /**
     * If this type use is adapting the type, returns the adapter.
     * Otherwise return null.
     */
    CAdapter getAdapterUse();

    /**
     * Individual item type.
     */
    CNonElement getInfo();

    /**
     * Whether the referenced type (individual item type in case of collection)
     * is ID/IDREF.
     *
     * <p>
     * ID is a property of a relationship. When a bean Foo has an ID property
     * called 'bar' whose type is String, Foo isn't an ID, String isn't an ID,
     * but this relationship is an ID (in the sense that Foo uses this String
     * as an ID.)
     *
     * <p>
     * The same thing can be said with IDREF. When Foo refers to Bar by means of
     * IDREF, neither Foo nor Bar is IDREF.
     *
     * <p>
     * That's why we have this method in {@link TypeUse}.
     */
    ID idUse();

    /**
     * A {@link TypeUse} can have an associated MIME type.
     */
    MimeType getExpectedMimeType();

    /**
     * Creates a constant for the given lexical value.
     *
     * <p>
     * For example, to create a constant 1 for {@code xs:int}, you'd do:
     * <pre>
     * CBuiltinLeafInfo.INT.createConstant( codeModel, "1", null );
     * </pre>
     *
     * <p>
     * This method is invoked at the backend as a part of the code generation process.
     *
     * @throws IllegalStateException
     *      if the type isn't bound to a text in XML.
     *
     * @return null
     *      if the constant cannot be created for this {@link TypeUse}
     *      (such as when it's a collection)
     */
    JExpression createConstant(Outline outline, XmlString lexical);
}
