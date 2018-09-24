/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind;

import com.sun.xml.bind.annotation.XmlLocation;

import org.xml.sax.Locator;

/**
 * Optional interface implemented by JAXB objects to expose
 * location information from which an object is unmarshalled.
 *
 * <p>
 * This is used during JAXB RI 1.0.x.
 * In JAXB 2.0, use {@link XmlLocation}.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 *
 * @since JAXB RI 1.0
 */
public interface Locatable {
    /**
     * @return
     *      null if the location information is unavaiable,
     *      or otherwise return a immutable valid {@link Locator}
     *      object.
     */
    Locator sourceLocation();
}
