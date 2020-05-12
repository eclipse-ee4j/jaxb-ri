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

import org.glassfish.jaxb.core.v2.model.core.WildcardMode;
import jakarta.xml.bind.annotation.DomHandler;
import org.xml.sax.SAXException;

/**
 * Feed incoming events to {@link DomHandler} and builds a DOM tree.
 * 
 * <p>
 * Note that the SAXException returned by the ContentHandler is
 * unreported. So we have to catch them and report it, then rethrow
 * it if necessary.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class WildcardLoader extends ProxyLoader {

    private final DomLoader dom;

    private final WildcardMode mode;

    public WildcardLoader(DomHandler dom, WildcardMode mode) {
        this.dom = new DomLoader(dom);
        this.mode = mode;
    }

    protected Loader selectLoader(UnmarshallingContext.State state, TagName tag) throws SAXException {
        UnmarshallingContext context = state.getContext();

        if(mode.allowTypedObject) {
            Loader l = context.selectRootLoader(state,tag);
            if(l!=null)
                return l;
        }
        if(mode.allowDom)
            return dom;

        // simply discard.
        return Discarder.INSTANCE;
    }

}
