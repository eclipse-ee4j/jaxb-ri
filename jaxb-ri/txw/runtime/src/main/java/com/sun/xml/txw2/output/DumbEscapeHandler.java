/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * Escape everything above the US-ASCII code range.
 * A fallback position.
 * 
 * Works with any JDK, any encoding.
 * 
 * @since 1.0.1
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class DumbEscapeHandler implements CharacterEscapeHandler {
    
    private DumbEscapeHandler() {}  // no instanciation please
    
    public static final CharacterEscapeHandler theInstance = new DumbEscapeHandler();
    
    @Override
    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        int limit = start+length;
        for (int i = start; i < limit; i++) {
            switch (ch[i]) {
            case '&':
                out.write("&amp;");
                break;
            case '<':
                out.write("&lt;");
                break;
            case '>':
                out.write("&gt;");
                break;
            case '\"':
                if (isAttVal) {
                    out.write("&quot;");
                } else {
                    out.write('\"');
                }
                break;
            default:
                if (ch[i] > '\u007f') {
                    out.write("&#");
                    out.write(Integer.toString(ch[i]));
                    out.write(';');
                } else {
                    out.write(ch[i]);
                }
            }
        }
    }

}
