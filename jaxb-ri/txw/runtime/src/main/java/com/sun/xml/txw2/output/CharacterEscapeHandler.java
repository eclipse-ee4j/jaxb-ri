/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2.output;

import java.io.IOException;
import java.io.Writer;

/**
 * Performs character escaping and write the result
 * to the output.
 * 
 * @since 1.0.1
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface CharacterEscapeHandler {
    
    /**
     * @param ch The array of characters.
     * @param start The starting position.
     * @param length The number of characters to use.
     * @param isAttVal true if this is an attribute value literal.
     */
    void escape( char ch[], int start, int length, boolean isAttVal, Writer out ) throws IOException; 

}
