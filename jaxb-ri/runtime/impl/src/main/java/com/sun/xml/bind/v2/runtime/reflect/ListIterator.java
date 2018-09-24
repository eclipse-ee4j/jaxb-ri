/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.reflect;

import java.util.Iterator;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

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
