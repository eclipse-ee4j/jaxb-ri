/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import jakarta.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import java.util.Iterator;

/**
 * Almost like {@link Iterator} but can throw JAXB specific exceptions.
 * @author Kohsuke Kawaguchi
 */
public interface ListIterator<E> {
    /**
     * Works like {@link Iterator#hasNext()}.
     */
    boolean hasNext();

    /**
     * Works like {@link Iterator#next()}.
     *
     * @throws SAXException
     *      if an error is found, reported, and we were told to abort
     * @throws JAXBException
     *      if an error is found, reported, and we were told to abort
     */
    E next() throws SAXException, JAXBException;
}
