/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import java.util.HashSet;
import java.util.Set;

/**
 * Post-init action for {@link MarshallerImpl} that incorporate the in-scope namespace bindings
 * from a DOM node.
 *
 * TODO: do we really need this? think about a better way to put this logic back into marshaller.
 *
 * @author Kohsuke Kawaguchi
 */
final class DomPostInitAction implements Runnable {

    private final Node node;
    private final XMLSerializer serializer;

    DomPostInitAction(Node node, XMLSerializer serializer) {
        this.node = node;
        this.serializer = serializer;
    }

    // declare the currently in-scope namespace bindings
    @Override
    public void run() {
        Set<String> declaredPrefixes = new HashSet<>();
        for( Node n=node; n!=null && n.getNodeType()==Node.ELEMENT_NODE; n=n.getParentNode() ) {
            NamedNodeMap atts = n.getAttributes();
            if(atts==null)      continue; // broken DOM. but be graceful.
            for( int i=0; i<atts.getLength(); i++ ) {
                Attr a = (Attr)atts.item(i);
                String nsUri = a.getNamespaceURI();
                if(nsUri==null || !nsUri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI))
                    continue;   // not a namespace declaration
                String prefix = a.getLocalName();
                if(prefix==null)
                    continue;   // broken DOM. skip to be safe
                if(prefix.equals("xmlns")) {
                    prefix = "";
                }
                String value = a.getValue();
                if(value==null)
                    continue;   // broken DOM. skip to be safe
                if(declaredPrefixes.add(prefix)) {
                    serializer.addInscopeBinding(value,prefix);
                }
            }
        }
    }
}
