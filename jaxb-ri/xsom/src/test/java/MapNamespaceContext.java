/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import javax.xml.namespace.NamespaceContext;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Kohsuke Kawaguchi
 */
public class MapNamespaceContext implements NamespaceContext {

    private final Map<String,String> core = new HashMap<String, String>();

    public MapNamespaceContext(String... mapping) {
        for( int i=0; i<mapping.length; i+=2 )
            core.put(mapping[i],mapping[i+1]);
    }

    public String getNamespaceURI(String prefix) {
        return core.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    public Iterator getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}
