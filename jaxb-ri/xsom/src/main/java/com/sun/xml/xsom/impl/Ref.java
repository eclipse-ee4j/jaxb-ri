/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;

/**
 * Reference to other schema components.
 * 
 * <p>
 * There are mainly two different types of references. One is
 * the direct reference, which is only possible when schema components
 * are already available when references are made.
 * The other is the lazy reference, which keeps references by names
 * and later look for the component by name.
 * 
 * <p>
 * This class defines interfaces that define the behavior of such
 * references and classes that implement direct reference semantics.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class Ref {

    /**
     * Default constructor.
     */
    protected Ref() {}

    public interface Term {
        /** Obtains a reference as a term. */
        XSTerm getTerm();
    }

    public interface Type {
        /** Obtains a reference as a type. */
        XSType getType();
    }

    public interface ContentType {
        XSContentType getContentType();
    }
    
    public interface SimpleType extends Ref.Type {
        XSSimpleType getType();
    }
    
    public interface ComplexType extends Ref.Type {
        XSComplexType getType();
    }
    
    public interface Attribute {
        XSAttributeDecl getAttribute();
    }

    public interface AttGroup {
        XSAttGroupDecl get();
    }
    
    public interface Element extends Term {
        XSElementDecl get();
    }

    public interface IdentityConstraint {
        XSIdentityConstraint get();
    }
//
//    
//    private static void _assert( boolean b ) {
//        if(!b)
//            throw new InternalError("assertion failed");
//    }
}
