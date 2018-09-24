/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api.impl.s2j;

import javax.xml.namespace.QName;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.api.Mapping;
import com.sun.tools.xjc.api.Property;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.outline.FieldOutline;

/**
 * @author Kohsuke Kawaguchi
 */
public /*for BSH*/ final class PropertyImpl implements Property {
    protected final FieldOutline fr;
    protected final QName elementName;
    protected final Mapping parent;
    protected final JCodeModel codeModel;

    PropertyImpl( Mapping parent, FieldOutline fr, QName elementName ) {
        this.parent = parent;
        this.fr = fr;
        this.elementName = elementName;
        this.codeModel = fr.getRawType().owner();
    }

    public final String name() {
        return fr.getPropertyInfo().getName(false);
    }

    /** Returns raw schema name for simpleType property. May return null for other types. */
    public final QName rawName() {
        if (fr instanceof ElementAdapter) {
            CElementInfo eInfo = ((ElementAdapter)fr).ei;
            if ((eInfo != null) && (eInfo.getProperty() != null)) {
                return eInfo.getProperty().getTypes().get(0).getTypeName();
            }
        }
        return null;
    }

    public final QName elementName() {
        return elementName;
    }

    public final JType type() {
        return fr.getRawType();
    }
}
