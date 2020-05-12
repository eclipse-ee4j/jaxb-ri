/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.marshaller;

import org.glassfish.jaxb.core.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Uses JDK1.4 NIO functionality to escape characters smartly.
 * 
 * @since 1.0.1
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class NioEscapeHandler implements CharacterEscapeHandler {
    
    private final CharsetEncoder encoder;
    
    // exposing those variations upset javac 1.3.1, since it needs to
    // know about those classes to determine which overloaded version
    // of the method it wants to use. So comment it out for the compatibility.
    
//    public NioEscapeHandler(CharsetEncoder _encoder) {
//        this.encoder = _encoder;
//        if(encoder==null)
//            throw new NullPointerException();
//    }
//
//    public NioEscapeHandler(Charset charset) {
//        this(charset.newEncoder());
//    }
    
    public NioEscapeHandler(String charsetName) {
//        this(Charset.forName(charsetName));
        this.encoder = Charset.forName(charsetName).newEncoder(); 
    }
    
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
                if( encoder.canEncode(ch[i]) ) {
                    out.write(ch[i]);
                } else {
                    out.write("&#");
                    out.write(Integer.toString(ch[i]));
                    out.write(';');
                }
            }
        }
    }

}
