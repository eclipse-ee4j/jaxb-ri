/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl.parser;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Manages patchers.
 * 
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public interface PatcherManager {
    void addPatcher( Patch p );
    void addErrorChecker( Patch p );
    /**
     * Reports an error during the parsing.
     * 
     * @param source
     *      location of the error in the source file, or null if
     *      it's unavailable.
     */
    void reportError( String message, Locator source ) throws SAXException;
    
    
    public interface Patcher {
        void run() throws SAXException;
    }
}
