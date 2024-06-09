/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.property;

import org.glassfish.jaxb.core.v2.model.core.ClassInfo;
import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.runtime.v2.model.runtime.*;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Create {@link Property} objects.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public abstract class PropertyFactory {
    private PropertyFactory() {}


    /**
     * Constructors of the {@link Property} implementation.
     */
    private static final List<BiFunction<JAXBContextImpl, RuntimePropertyInfo, ? extends Property>> propImpls;

    static {
        propImpls = Collections.unmodifiableList(Arrays.asList(
            (context, prop) -> new SingleElementLeafProperty(context, (RuntimeElementPropertyInfo) prop),
            null, // single reference leaf --- but there's no such thing as "reference leaf"
            null, // no such thing as "map leaf"

            (context, prop) -> new ArrayElementLeafProperty(context, (RuntimeElementPropertyInfo) prop),
            null, // array reference leaf --- but there's no such thing as "reference leaf"
            null, // no such thing as "map leaf"

            (context, prop) -> new SingleElementNodeProperty(context, (RuntimeElementPropertyInfo) prop),
            (context, prop) -> new SingleReferenceNodeProperty(context, (RuntimeReferencePropertyInfo) prop),
            (context, prop) -> new SingleMapNodeProperty(context, (RuntimeMapPropertyInfo) prop),

            (context, prop) -> new ArrayElementNodeProperty(context, (RuntimeElementPropertyInfo) prop),
            (context, prop) -> new ArrayReferenceNodeProperty(context, (RuntimeReferencePropertyInfo) prop),
            null // map is always a single property (Map doesn't implement Collection)
        ));
    }

    /**
     * Creates/obtains a properly configured {@link Property}
     * object from the given description.
     */
    public static Property create( JAXBContextImpl grammar, RuntimePropertyInfo info ) {

        PropertyKind kind = info.kind();

        switch(kind) {
        case ATTRIBUTE:
            return new AttributeProperty(grammar,(RuntimeAttributePropertyInfo)info);
        case VALUE:
            return new ValueProperty(grammar,(RuntimeValuePropertyInfo)info);
        case ELEMENT:
            if(((RuntimeElementPropertyInfo)info).isValueList())
                return new ListElementProperty(grammar,(RuntimeElementPropertyInfo) info);
            break;
        case REFERENCE:
        case MAP:
            break;
        default:
            assert false;
        }


        boolean isCollection = info.isCollection();
        boolean isLeaf = isLeaf(info);

        return propImpls.get((isLeaf?0:6)+(isCollection?3:0)+kind.propertyIndex).apply(grammar, info);
    }

    /**
     * Look for the case that can be optimized as a leaf,
     * which is a kind of type whose XML representation is just PCDATA.
     */
    static boolean isLeaf(RuntimePropertyInfo info) {
        Collection<? extends RuntimeTypeInfo> types = info.ref();
        if(types.size()!=1)     return false;

        RuntimeTypeInfo rti = types.iterator().next();
        if(!(rti instanceof RuntimeNonElement)) return false;

        if(info.id()==ID.IDREF)
            // IDREF is always handled as leaf -- Transducer maps IDREF String back to an object
            return true;

        //if hasSubClasses it's not a leaf and we can't optimize, see #1135
        if (rti instanceof ClassInfo && ((ClassInfo) rti).hasSubClasses()) return false;

        if(((RuntimeNonElement)rti).getTransducer()==null)
            // Transducer!=null means definitely binds to PCDATA.
            // even if transducer==null, a referene might be IDREF,
            // in which case it will still produce PCDATA in this reference.
            return false;

        return info.getIndividualType().equals(rti.getType());
    }
}
