/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet.boxed;

import java.io.File;

import com.sun.tools.xjc.servlet.Compiler;


/**
 * {@link Compiler} implementation that is used when
 * there was an error before invoking a compiler.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class FailedCompilerImpl extends Compiler {
    
    private final String errorMessage;
    
    FailedCompilerImpl( String _errorMessage ) {
        this.errorMessage = _errorMessage;
    }
    
    public File getOutDir() {
        throw new UnsupportedOperationException();
    }

    public String getStatusMessages() {
        return errorMessage;
    }

    public byte[] getZipFile() {
        return null;
    }
}
