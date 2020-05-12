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
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import org.glassfish.jaxb.core.v2.TODO;
import org.glassfish.jaxb.core.v2.model.core.Adapter;
import org.glassfish.jaxb.core.v2.model.core.ID;

/**
 * Factory methods to create a new {@link TypeUse} from an existing one.
 *
 * @author Kohsuke Kawaguchi
 */
public final class TypeUseFactory {
    private TypeUseFactory() {}

    public static TypeUse makeID( TypeUse t, ID id ) {
        if(t.idUse()!=ID.NONE)
            // I don't think we let users tweak the idness, so
            // this error must indicate an inconsistency within the RI/spec.
            throw new IllegalStateException();
        return new TypeUseImpl( t.getInfo(), t.isCollection(), id, t.getExpectedMimeType(), t.getAdapterUse() );
    }

    public static TypeUse makeMimeTyped( TypeUse t, MimeType mt ) {
        if(t.getExpectedMimeType()!=null)
            // I don't think we let users tweak the idness, so
            // this error must indicate an inconsistency within the RI/spec.
            throw new IllegalStateException();
        return new TypeUseImpl( t.getInfo(), t.isCollection(), t.idUse(), mt, t.getAdapterUse() );
    }

    public static TypeUse makeCollection( TypeUse t ) {
        if(t.isCollection())    return t;
        CAdapter au = t.getAdapterUse();
        if(au!=null && !au.isWhitespaceAdapter()) {
            // we can't process this right now.
            // for now bind to a weaker type
            TODO.checkSpec();
            return CBuiltinLeafInfo.STRING_LIST;
        }
        return new TypeUseImpl( t.getInfo(), true, t.idUse(), t.getExpectedMimeType(), null );
    }

    public static TypeUse adapt(TypeUse t, CAdapter adapter) {
        assert t.getAdapterUse()==null;    // TODO: we don't know how to handle double adapters yet.
        return new TypeUseImpl(t.getInfo(),t.isCollection(),t.idUse(),t.getExpectedMimeType(),adapter);
    }

    /**
     * Creates a new adapter {@link TypeUse} by using the existing {@link Adapter} class.
     */
    public static TypeUse adapt( TypeUse t, Class<? extends XmlAdapter> adapter, boolean copy ) {
        return adapt( t, new CAdapter(adapter,copy) );
    }
}
