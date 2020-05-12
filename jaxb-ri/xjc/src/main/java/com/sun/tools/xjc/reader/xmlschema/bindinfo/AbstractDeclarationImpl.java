/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import java.util.Collection;
import java.util.Collections;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import org.glassfish.jaxb.core.annotation.XmlLocation;
import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * Abstract partial implementation of {@link BIDeclaration}
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
abstract class AbstractDeclarationImpl implements BIDeclaration {

    @Deprecated // eventually delete this in favor of using JAXB    
    protected AbstractDeclarationImpl(Locator loc) {
        this.loc = loc;
    }

    protected AbstractDeclarationImpl() {}


    @XmlLocation
    Locator loc;    // set by JAXB
    public Locator getLocation() { return loc; }
    
    protected BindInfo parent;
    public void setParent(BindInfo p) { this.parent=p; }

    protected final XSComponent getOwner() {
        return parent.getOwner();
    }
    protected final BGMBuilder getBuilder() {
        return parent.getBuilder();
    }
    protected final JCodeModel getCodeModel() {
        return Ring.get(JCodeModel.class);
    }


    private boolean isAcknowledged = false;
    
    public final boolean isAcknowledged() { return isAcknowledged; }

    public void onSetOwner() {
    }

    public Collection<BIDeclaration> getChildren() {
        return Collections.emptyList();
    }

    public void markAsAcknowledged() {
        isAcknowledged = true;
    }
}
