/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.api;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;

/**
 * A reference to a JAXB-bound type.
 *
 * <p>
 * <b>Subject to change without notice</b>.
 *
 * @since 2.0 EA1
 * @author Kohsuke Kawaguchi
 */
public final class TypeReference {

    /**
     * The associated XML element name that the JAX-RPC uses with this type reference.
     *
     * Always non-null. Strings are interned.
     */
    public final QName tagName;

    /**
     * The Java type that's being referenced.
     *
     * Always non-null.
     */
    public final Type type;

    /**
     * The annotations associated with the reference of this type.
     *
     * Always non-null.
     */
    public final Annotation[] annotations;

    public TypeReference(QName tagName, Type type, Annotation... annotations) {
        if(tagName==null || type==null || annotations==null) {
            String nullArgs = "";

            if(tagName == null)     nullArgs = "tagName";
            if(type == null)        nullArgs += (nullArgs.length() > 0 ? ", type" : "type");
            if(annotations == null) nullArgs += (nullArgs.length() > 0 ? ", annotations" : "annotations");

            Messages.ARGUMENT_CANT_BE_NULL.format(nullArgs);
            
            throw new IllegalArgumentException(Messages.ARGUMENT_CANT_BE_NULL.format(nullArgs));
        }

        this.tagName = new QName(tagName.getNamespaceURI().intern(), tagName.getLocalPart().intern(), tagName.getPrefix());
        this.type = type;
        this.annotations = annotations;
    }

    /**
     * Finds the specified annotation from the array and returns it.
     * Null if not found.
     */
    public <A extends Annotation> A get( Class<A> annotationType ) {
        for (Annotation a : annotations) {
            if(a.annotationType()==annotationType)
                return annotationType.cast(a);
        }
        return null;
    }

    /**
     * Creates a  for the item type,
     * if this  represents a collection type.
     * Otherwise returns an identical type.
     */
    public TypeReference toItemType() {
        // if we are to reinstitute this check, check JAXB annotations only 
        // assert annotations.length==0;   // not designed to work with adapters.

        Type base = Utils.REFLECTION_NAVIGATOR.getBaseClass(type, Collection.class);
        if(base==null)
            return this;    // not a collection

        return new TypeReference(tagName, Utils.REFLECTION_NAVIGATOR.getTypeArgument(base,0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeReference that = (TypeReference) o;

        if (!Arrays.equals(annotations, that.annotations)) return false;
        if (!tagName.equals(that.tagName)) return false;
        return type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = tagName.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + Arrays.hashCode(annotations);
        return result;
    }
}
