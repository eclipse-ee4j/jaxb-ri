/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import java.util.Iterator;

import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.visitor.XSWildcardFunction;

import com.sun.tools.rngom.nc.AnyNameExceptNameClass;
import com.sun.tools.rngom.nc.ChoiceNameClass;
import com.sun.tools.rngom.nc.NameClass;
import com.sun.tools.rngom.nc.NsNameClass;

/**
 * Builds a name class representation of a wildcard.
 *
 * <p>
 * Singleton. Use the build method to create a NameClass.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class WildcardNameClassBuilder implements XSWildcardFunction<NameClass> {
    private WildcardNameClassBuilder() {}

    private static final XSWildcardFunction<NameClass> theInstance =
        new WildcardNameClassBuilder();

    public static NameClass build( XSWildcard wc ) {
        return wc.apply(theInstance);
    }

    public NameClass any(XSWildcard.Any wc) {
        return NameClass.ANY;
    }

    public NameClass other(XSWildcard.Other wc) {
        return new AnyNameExceptNameClass(
            new ChoiceNameClass(
                new NsNameClass(""),
                new NsNameClass(wc.getOtherNamespace())));
    }

    public NameClass union(XSWildcard.Union wc) {
        NameClass nc = null;
        for (Iterator itr = wc.iterateNamespaces(); itr.hasNext();) {
            String ns = (String) itr.next();

            if(nc==null)    nc = new NsNameClass(ns);
            else
                nc = new ChoiceNameClass(nc,new NsNameClass(ns));
        }

        // there should be at least one.
        assert nc!=null;

        return nc;
    }

}
