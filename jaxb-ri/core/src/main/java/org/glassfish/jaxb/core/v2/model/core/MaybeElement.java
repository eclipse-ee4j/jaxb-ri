/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.core;

import javax.xml.namespace.QName;

/**
 * Some {@link NonElement} can optionally be an {@link Element}.
 *
 * This interface captures such characteristics.
 *
 * @author Kohsuke Kawaguchi
 */
public interface MaybeElement<T,C> extends NonElement<T,C> {
    /**
     * If the class is bound to an element, return true.
     *
     * <p>
     * Note that when this is true, the class is bound to both an element
     * and a type.
     */
    boolean isElement();

    /**
     * Gets the element name of the class, if the class is bound
     * to an element.
     *
     * @return
     *      non-null iff {@link #isElement()}.
     */
    QName getElementName();

    /**
     * Returns the {@link Element} aspect of this {@link ClassInfo}.
     *
     * @return
     *      null if {@link #isElement()}==false, non-null if {@link #isElement()}==true.
     */
    Element<T,C> asElement();
}
