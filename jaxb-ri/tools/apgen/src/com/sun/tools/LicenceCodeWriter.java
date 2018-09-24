/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FilterCodeWriter;

/**
 * Writes all the source files under the specified file folder and 
 * inserts a licence prolog comment in each java source file.
 * 
 * @author
 *  Martin Grebac (martin.grebac@oracle.com)
 */
public class LicenceCodeWriter extends FilterCodeWriter {
    
    /** prolog comment */
    private final String prolog;
    
    /**
     * @param core
     *      This CodeWriter will be used to actually create a storage for files.
     *      LicenceCodeWriter simply decorates this underlying CodeWriter by
     *      adding licence header.
     * @param prolog
     *      String that will be added as a header.
     */
    public LicenceCodeWriter( CodeWriter core, String prolog ) {
        super(core);
        this.prolog = prolog;
    }
    
    
    @Override
    public Writer openSource(JPackage pkg, String fileName) throws IOException {
        Writer w = super.openSource(pkg,fileName);
        
        PrintWriter out = new PrintWriter(w);        
        if( prolog != null ) {
            out.print(prolog);
        }
        out.flush();    // we can't close the stream for that would close the undelying stream.

        return w;
    }
}
