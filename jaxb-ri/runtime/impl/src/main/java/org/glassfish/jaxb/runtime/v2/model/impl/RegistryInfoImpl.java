/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.runtime.v2.ContextFactory;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.runtime.v2.model.annotation.MethodLocatable;
import org.glassfish.jaxb.core.v2.model.core.RegistryInfo;
import org.glassfish.jaxb.core.v2.model.core.TypeInfo;
import org.glassfish.jaxb.core.v2.model.nav.Navigator;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.core.v2.runtime.Location;
import jakarta.xml.bind.annotation.XmlElementDecl;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementation of {@link RegistryInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
// experimenting with shorter type parameters for <T,C,F,M> quadruple.
// the idea is that they show so often that you'd understand the meaning
// without relying on the whole name.
final class RegistryInfoImpl<T,C,F,M> implements Locatable, RegistryInfo<T,C> {

    final C registryClass;
    private final Locatable upstream;
    private final Navigator<T,C,F,M> nav;

    /**
     * Types that are referenced from this registry.
     */
    private final Set<TypeInfo<T,C>> references = new LinkedHashSet<TypeInfo<T,C>>();

    /**
     * Picks up references in this registry to other types.
     */
    RegistryInfoImpl(ModelBuilder<T,C,F,M> builder, Locatable upstream, C registryClass) {
        this.nav = builder.nav;
        this.registryClass = registryClass;
        this.upstream = upstream;
        builder.registries.put(getPackageName(),this);

        if(nav.getDeclaredField(registryClass,ContextFactory.USE_JAXB_PROPERTIES)!=null) {
            // the user is trying to use ObjectFactory that we generate for interfaces,
            // that means he's missing jaxb.properties
            builder.reportError(new IllegalAnnotationException(
                Messages.MISSING_JAXB_PROPERTIES.format(getPackageName()),
                this
            ));
            // looking at members will only add more errors, so just abort now
            return;
        }

        for( M m : nav.getDeclaredMethods(registryClass) ) {
            XmlElementDecl em = builder.reader.getMethodAnnotation(
                XmlElementDecl.class, m, this );

            if(em==null) {
                if(nav.getMethodName(m).startsWith("create")) {
                    // this is a factory method. visit this class
                    references.add(
                        builder.getTypeInfo(nav.getReturnType(m),
                            new MethodLocatable<M>(this,m,nav)));
                }

                continue;
            }

            ElementInfoImpl<T,C,F,M> ei;
            try {
                ei = builder.createElementInfo(this,m);
            } catch (IllegalAnnotationException e) {
                builder.reportError(e);
                continue;   // recover by ignoring this element
            }

            // register this mapping
            // TODO: any chance this could cause a stack overflow (by recursively visiting classes)?
            builder.typeInfoSet.add(ei,builder);
            references.add(ei);
        }
    }

    public Locatable getUpstream() {
        return upstream;
    }

    public Location getLocation() {
        return nav.getClassLocation(registryClass);
    }

    public Set<TypeInfo<T,C>> getReferences() {
        return references;
    }

    /**
     * Gets the name of the package that this registry governs.
     */
    public String getPackageName() {
        return nav.getPackageName(registryClass);
    }

    public C getClazz() {
        return registryClass;
    }
}
