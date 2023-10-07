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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Reference to a JAXB type (from JAX-RPC.)
 *
 * <p>
 * A reference is a Java type (represented as a {@link javax.lang.model.type.TypeMirror})
 * and a set of annotations (represented as a {@link javax.lang.model.element.Element}).
 * Together they describe a root reference to a JAXB type binding.
 *
 * <p>
 * Those two values can be supplied independently, or you can use
 * other convenience constructors to supply two values at once.
 *
 *
 * @author Kohsuke Kawaguchi
 */
public final class Reference {
    /**
     * The JAXB type being referenced. Must not be null.
     */
    public final TypeMirror type;
    /**
     * The declaration from which annotations for the {@link #type} is read.
     * Must not be null.
     */
    public final Element annotations;

    /**
     * Creates a reference from the return type of the method
     * and annotations on the method.
     */
    public Reference(ExecutableElement method) {
        this(method.getReturnType(),method);
    }

    /**
     * Creates a reference from the parameter type
     * and annotations on the parameter.
     */
    public Reference(VariableElement param) {
        this(param.asType(), param);
    }

    /**
     * Creates a reference from a class declaration and its annotations.
     */
    public Reference(TypeElement type, ProcessingEnvironment env) {
        this(env.getTypeUtils().getDeclaredType(type),type);
    }

    /**
     * Creates a reference by providing two values independently.
     */
    public Reference(TypeMirror type, Element annotations) {
        if(type==null || annotations==null)
            throw new IllegalArgumentException();
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reference)) return false;

        final Reference that = (Reference) o;

        return annotations.equals(that.annotations) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return 29 * type.hashCode() + annotations.hashCode();
    }
}
