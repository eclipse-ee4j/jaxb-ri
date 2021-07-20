/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import java.math.BigInteger;
import com.sun.tools.xjc.model.Multiplicity;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.visitor.XSTermFunction;

import static com.sun.tools.xjc.model.Multiplicity.ONE;
import static com.sun.tools.xjc.model.Multiplicity.ZERO;

/**
 * Counts {@link Multiplicity} for a particle/term.
 *
 * @author Kohsuke Kawaguchi
 */
public final class MultiplicityCounter implements XSTermFunction<Multiplicity> {

    public static final MultiplicityCounter theInstance = new MultiplicityCounter();

    private MultiplicityCounter() {}

    public Multiplicity particle( XSParticle p ) {
        Multiplicity m = p.getTerm().apply(this);

        BigInteger max;
        if (m.max==null || (BigInteger.valueOf(XSParticle.UNBOUNDED).equals(p.getMaxOccurs())))
            max=null;
        else
            max=p.getMaxOccurs();

        return Multiplicity.multiply( m, Multiplicity.create(p.getMinOccurs(),max) );
    }

    public Multiplicity wildcard(XSWildcard wc) {
        return ONE;
    }

    public Multiplicity modelGroupDecl(XSModelGroupDecl decl) {
        return modelGroup(decl.getModelGroup());
    }

    public Multiplicity modelGroup(XSModelGroup group) {
        boolean isChoice = group.getCompositor() == XSModelGroup.CHOICE;

        Multiplicity r = ZERO;

        for( XSParticle p : group.getChildren()) {
            Multiplicity m = particle(p);

            if(r==null) {
                r=m;
                continue;
            }
            if(isChoice) {
                r = Multiplicity.choice(r,m);
            } else {
                r = Multiplicity.group(r,m);
            }
        }
        return r;
    }

    public Multiplicity elementDecl(XSElementDecl decl) {
        return ONE;
    }
}
