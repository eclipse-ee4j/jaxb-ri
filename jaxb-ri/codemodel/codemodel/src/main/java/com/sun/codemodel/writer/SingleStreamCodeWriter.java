/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

/**
 * Output all source files into a single stream with a little
 * formatting header in front of each file.
 * 
 * This is primarily for human consumption of the generated source
 * code, such as to debug/test CodeModel or to quickly inspect the result.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SingleStreamCodeWriter extends CodeWriter {
    
    private final PrintStream out;
    
    /**
     * @param os
     *      This stream will be closed at the end of the code generation.
     */
    public SingleStreamCodeWriter( OutputStream os ) {
        out = new PrintStream(os);
    }

    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        final String name = pkg != null && pkg.name().length() > 0
                ? pkg.name() + '.' + fileName : fileName;

        out.println(
            "-----------------------------------" + name +
            "-----------------------------------");
            
        return new FilterOutputStream(out) {
            @Override
            public void close() {
                // don't let this stream close
            }
        };
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

}
