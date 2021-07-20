/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.unmarshaller;

import org.xml.sax.SAXException;

/**
 * Runs by UnmarshallingContext after all the parsing is done.
 *
 * Primarily used to resolve forward IDREFs, but it can run any action.
 *
 * @author Kohsuke Kawaguchi
 */
public interface Patcher {
    /**
     * Runs an post-action.
     *
     * @throws SAXException
     *      if an error is found during the action, it should be reporeted to the context.
     *      The context may then throw a {@link SAXException} to abort the processing,
     *      and that's when you can throw a {@link SAXException}.
     */
    void run() throws SAXException;
}
