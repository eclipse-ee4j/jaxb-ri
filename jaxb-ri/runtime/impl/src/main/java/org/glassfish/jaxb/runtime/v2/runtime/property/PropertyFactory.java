/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
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
import org.glassfish.jaxb.runtime.v2.model.runtime.*;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;

import java.util.Collection;

/**
 * Create {@link Property} objects.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public abstract class PropertyFactory {
    private PropertyFactory() {}

    /**
     * Creates/obtains a properly configured {@link Property}
     * object from the given description.
     */
    public static Property create( JAXBContextImpl grammar, RuntimePropertyInfo info) {
            return info.create(grammar);
    }

    /**
     * Look for the case that can be optimized as a leaf,
     * which is a kind of type whose XML representation is just PCDATA.
     */
    public static boolean isLeaf(RuntimePropertyInfo info) {
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
