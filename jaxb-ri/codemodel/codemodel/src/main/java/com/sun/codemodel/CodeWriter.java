/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.CharsetEncoder;

import com.sun.codemodel.util.EncoderFactory;
import com.sun.codemodel.util.UnicodeEscapeWriter;

/**
 * Receives generated code and writes to the appropriate storage.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class CodeWriter {

    public CodeWriter() {}

    /**
     * Encoding to be used by the writer. Null means platform specific encoding.
     *
     * @since 2.5
     */
    protected String encoding = null;

    /**
     * Called by CodeModel to store the specified file.
     * The callee must allocate a storage to store the specified file.
     * 
     * <p>
     * The returned stream will be closed before the next file is
     * stored. So the callee can assume that only one OutputStream
     * is active at any given time.
     * 
     * @param   pkg
     *      The package of the file to be written.
     * @param   fileName
     *      File name without the path. Something like
     *      "Foo.java" or "Bar.properties"
     */
    public abstract OutputStream openBinary( JPackage pkg, String fileName ) throws IOException;

    /**
     * Called by CodeModel to store the specified file.
     * The callee must allocate a storage to store the specified file.
     *
     * <p>
     * The returned stream will be closed before the next file is
     * stored. So the callee can assume that only one OutputStream
     * is active at any given time.
     *
     * @param   pkg
     *      The package of the file to be written.
     * @param   fileName
     *      File name without the path. Something like
     *      "Foo.java" or "Bar.properties"
     */
    public Writer openSource( JPackage pkg, String fileName ) throws IOException {
        final OutputStreamWriter bw = encoding != null
                ? new OutputStreamWriter(openBinary(pkg,fileName), encoding)
                : new OutputStreamWriter(openBinary(pkg,fileName));

        // create writer
        try {
            return new UnicodeEscapeWriter(bw) {
                // can't change this signature to Encoder because
                // we can't have Encoder in method signature
                private final CharsetEncoder encoder = EncoderFactory.createEncoder(bw.getEncoding());
                @Override
                protected boolean requireEscaping(int ch) {
                    // control characters
                    if( ch<0x20 && " \t\r\n".indexOf(ch)==-1 )  return true;
                    // check ASCII chars, for better performance
                    if( ch<0x80 )       return false;

                    return !encoder.canEncode((char)ch);
                }
            };
        } catch( Throwable t ) {
            return new UnicodeEscapeWriter(bw);
        }
    }

    /**
     * Called by CodeModel at the end of the process.
     */
    public abstract void close() throws IOException;
}
