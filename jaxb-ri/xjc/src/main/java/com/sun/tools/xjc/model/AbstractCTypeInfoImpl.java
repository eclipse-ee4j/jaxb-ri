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
import com.sun.tools.xjc.outline.Outline;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.runtime.Location;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XmlString;

/**
 * Partial implementation of {@link CTypeInfo}.
 *
 * <p>
 * The inheritance of {@link TypeUse} by {@link CTypeInfo}
 * isn't a normal inheritance (see {@link CTypeInfo} for more.)
 * This class implments methods on {@link TypeUse} for {@link CTypeInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
abstract class AbstractCTypeInfoImpl implements CTypeInfo {

    private final CCustomizations customizations;

    private final XSComponent source;

    protected AbstractCTypeInfoImpl(Model model, XSComponent source, CCustomizations customizations) {
        if(customizations==null)
            customizations = CCustomizations.EMPTY;
        else
            customizations.setParent(model,this);
        this.customizations = customizations;
        this.source = source;
    }

    public final boolean isCollection() {
        return false;
    }

    public final CAdapter getAdapterUse() {
        return null;
    }

    public final ID idUse() {
        return ID.NONE;
    }

    public final XSComponent getSchemaComponent() {
        return source;
    }

    /**
     * @deprecated
     *      why are you calling an unimplemented method?
     */
    public final boolean canBeReferencedByIDREF() {
        // we aren't doing any error check in XJC, so no point in implementing this method.
        throw new UnsupportedOperationException();
    }

    /**
     * No default {@link MimeType}.
     */
    public MimeType getExpectedMimeType() {
        return null;
    }

    public CCustomizations getCustomizations() {
        return customizations;
    }

    // this is just a convenient default
    public JExpression createConstant(Outline outline, XmlString lexical) {
        return null;
    }

    public final Locatable getUpstream() {
        throw new UnsupportedOperationException();
    }

    public final Location getLocation() {
        throw new UnsupportedOperationException();
    }
}
