/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.output;

import org.glassfish.jaxb.core.marshaller.SAX2DOMEx;
import org.glassfish.jaxb.runtime.v2.runtime.AssociationMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * {@link XmlOutput} implementation that does associative marshalling to DOM.
 *
 * <p>
 * This is used for binder.
 *
 * @author Kohsuke Kawaguchi
 */
public final class DOMOutput extends SAXOutput {
    private final AssociationMap assoc;

    public DOMOutput(Node node, AssociationMap assoc) {
        super(new SAX2DOMEx(node));
        this.assoc = assoc;
        assert assoc!=null;
    }

    private SAX2DOMEx getBuilder() {
        return (SAX2DOMEx)out;
    }

    @Override
    public void endStartTag() throws SAXException {
        super.endStartTag();

        Object op = nsContext.getCurrent().getOuterPeer();
        if(op!=null)
            assoc.addOuter( getBuilder().getCurrentElement(), op );

        Object ip = nsContext.getCurrent().getInnerPeer();
        if(ip!=null)
            assoc.addInner( getBuilder().getCurrentElement(), ip );
    }
}
