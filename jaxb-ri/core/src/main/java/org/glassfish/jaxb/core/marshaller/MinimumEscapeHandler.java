/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.marshaller;

import java.io.IOException;
import java.io.Writer;

/**
 * Performs no character escaping. Usable only when the output encoding
 * is UTF, but this handler gives the maximum performance.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class MinimumEscapeHandler implements CharacterEscapeHandler {
    
    private MinimumEscapeHandler() {}  // no instanciation please
    
    public static final CharacterEscapeHandler theInstance = new MinimumEscapeHandler(); 
    
    public void escape(char[] ch, int start, int length, boolean isAttVal, Writer out) throws IOException {
        // avoid calling the Writerwrite method too much by assuming
        // that the escaping occurs rarely.
        // profiling revealed that this is faster than the naive code.
        int limit = start+length;
        for (int i = start; i < limit; i++) {
            char c = ch[i];
            if (c == '&' || c == '<' || c == '>' || c == '\r' || (c == '\n' && isAttVal) || (c == '\"' && isAttVal)) {
                if (i != start)
                    out.write(ch, start, i - start);
                start = i + 1;
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
                        out.write("&quot;");
                        break;
                    case '\n':
                    case '\r':
                        out.write("&#");
                        out.write(Integer.toString(c));
                        out.write(';');
                        break;
                    default:
                        throw new IllegalArgumentException("Cannot escape: '" + c + "'");
                }
            }
        }
        
        if( start!=limit )
            out.write(ch,start,limit-start);
    }

}
