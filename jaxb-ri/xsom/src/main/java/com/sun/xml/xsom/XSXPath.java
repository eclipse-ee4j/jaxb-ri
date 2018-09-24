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

/**
 * Selector or field of {@link XSIdentityConstraint}.
 * 
 * @author Kohsuke Kawaguchi
 */
public interface XSXPath extends XSComponent  {

    /**
     * Returns the {@link XSIdentityConstraint} to which
     * this XPath belongs to.
     *
     * @return
     *      never null.
     */
    XSIdentityConstraint getParent();

    /**
     * Gets the XPath as a string.
     *
     * @return
     *      never null.
     */
    XmlString getXPath();
}
