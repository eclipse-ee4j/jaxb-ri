/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom;

import com.sun.xml.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.xsom.visitor.XSContentTypeVisitor;

/**
 * Content of a complex type.
 * 
 * @author
 *  Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface XSContentType extends XSComponent
{
    /**
     * Equivalent of <code>(this instanceof XSSimpleType)?this:null</code>
     */
    XSSimpleType asSimpleType();
    /**
     * Equivalent of <code>(this instanceof XSParticle)?this:null</code>
     */
    XSParticle asParticle();
    /**
     * If this content type represents the empty content, return <code>this</code>,
     * otherwise null.
     */
    XSContentType asEmpty();

    <T> T apply( XSContentTypeFunction<T> function );
    void visit( XSContentTypeVisitor visitor );
}
