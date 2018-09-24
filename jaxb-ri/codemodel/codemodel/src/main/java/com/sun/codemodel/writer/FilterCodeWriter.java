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

import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

/**
 * {@link CodeWriter} that delegates to another {@link CodeWriter}.
 *
 * @author Kohsuke Kawaguchi
 */
public class FilterCodeWriter extends CodeWriter {
    protected CodeWriter core;

    public FilterCodeWriter(CodeWriter core) {
        this.core = core;
    }

    @Override
    public OutputStream openBinary( JPackage pkg, String fileName ) throws IOException {
        return core.openBinary(pkg, fileName);
    }

    @Override
    public Writer openSource( JPackage pkg, String fileName ) throws IOException {
        return core.openSource(pkg, fileName);
    }

    @Override
    public void close() throws IOException {
        core.close();
    }
}
