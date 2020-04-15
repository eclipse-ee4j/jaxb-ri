/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.runtime.IDResolver;
import jakarta.xml.bind.ValidationEventHandler;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Default implementation of {@link IDResolver}.
 *
 * @author Kohsuke Kawaguchi
 */
final class DefaultIDResolver extends IDResolver {
    /** Records ID->Object map. */
    private HashMap<String,Object> idmap = null;

    @Override
    public void startDocument(ValidationEventHandler eventHandler) throws SAXException {
        if(idmap!=null)
            idmap.clear();
    }

    @Override
    public void bind(String id, Object obj) {
        if(idmap==null)     idmap = new HashMap<String,Object>();
        idmap.put(id,obj);
    }

    @Override
    public Callable resolve(final String id, Class targetType) {
        return new Callable() {
            public Object call() throws Exception {
                if(idmap==null)     return null;
                return idmap.get(id);
            }
        };
    }
}
