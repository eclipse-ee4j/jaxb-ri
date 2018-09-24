/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model;

import com.sun.codemodel.JDefinedClass;
import com.sun.tools.txw2.model.prop.Prop;
import org.xml.sax.Locator;

import java.util.Set;

/**
 * A reference to a named pattern.
 *
 * @author Kohsuke Kawaguchi
 */
public final class Ref extends Leaf {
    public final Define def;

    public Ref(Locator location, Grammar scope, String name) {
        super(location);
        this.def = scope.get(name);
    }

    public Ref(Locator location, Define def) {
        super(location);
        this.def = def;
    }

    public boolean isInline() {
        return def.isInline();
    }

    void generate(JDefinedClass clazz, NodeSet nset, Set<Prop> props) {
        def.generate(clazz,nset,props);
    }
}
