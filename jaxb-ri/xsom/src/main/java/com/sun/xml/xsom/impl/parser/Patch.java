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

import org.xml.sax.SAXException;

/**
 * Patch program that runs later to "fix" references among components.
 * 
 * The only difference from the Runnable interface is that this interface
 * allows the program to throw a SAXException.
 */
public interface Patch {
    void run() throws SAXException;
}

