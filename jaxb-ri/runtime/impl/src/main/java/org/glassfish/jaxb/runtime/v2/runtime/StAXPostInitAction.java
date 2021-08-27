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

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;

/**
 * Post-init action for {@link MarshallerImpl} that incorporate the in-scope namespace bindings
 * from a StAX writer.
 *
 * <p>
 * It's always used either with {@link XMLStreamWriter}, {@link XMLEventWriter}, or bare
 * {@link NamespaceContext},
 * but to reduce the # of classes in the runtime I wrote only one class that handles both.
 *
 * @author Kohsuke Kawaguchi
 */
final class StAXPostInitAction implements Runnable {
    private final XMLStreamWriter xsw;
    private final XMLEventWriter xew;
    private final NamespaceContext nsc;
    private final XMLSerializer serializer;

    StAXPostInitAction(XMLStreamWriter xsw,XMLSerializer serializer) {
        this.xsw = xsw;
        this.xew = null;
        this.nsc = null;
        this.serializer = serializer;
    }

    StAXPostInitAction(XMLEventWriter xew,XMLSerializer serializer) {
        this.xsw = null;
        this.xew = xew;
        this.nsc = null;
        this.serializer = serializer;
    }

    StAXPostInitAction(NamespaceContext nsc,XMLSerializer serializer) {
        this.xsw = null;
        this.xew = null;
        this.nsc = nsc;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        NamespaceContext ns = nsc;
        if(xsw!=null)   ns = xsw.getNamespaceContext();
        if(xew!=null)   ns = xew.getNamespaceContext();

        // StAX javadoc isn't very clear on the behavior,
        // so work defensively in anticipation of broken implementations.
        if(ns==null)
            return;

        // we can't enumerate all the in-scope namespace bindings in StAX,
        // so we only look for the known static namespace URIs.
        // this is less than ideal, but better than nothing.
        for( String nsUri : serializer.grammar.nameList.namespaceURIs ) {
            String p = ns.getPrefix(nsUri);
            if(p!=null)
                serializer.addInscopeBinding(nsUri,p);
        }
    }
}
