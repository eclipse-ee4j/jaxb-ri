/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FilterCodeWriter;

/**
 * {@link CodeWriter} that reports progress to {@link XJCListener}.
 */
final class ProgressCodeWriter extends FilterCodeWriter {

    private int current;
    private final int totalFileCount;

    public ProgressCodeWriter( CodeWriter output, XJCListener progress, int totalFileCount ) {
        super(output);
        this.progress = progress;
        this.totalFileCount = totalFileCount;
        if(progress==null)
            throw new IllegalArgumentException();
    }

    private final XJCListener progress;

    @Override
    public Writer openSource(JPackage pkg, String fileName) throws IOException {
        report(pkg,fileName);
        return super.openSource(pkg, fileName);
    }

    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        report(pkg,fileName);
        return super.openBinary(pkg,fileName);
    }

    /**
     * Report progress to {@link XJCListener}.
     * @param pkg The package of file being written. Value of {@code null} means that file has no package.
     * @param fileName The file name being written. Value can't be {@code null}.
     */
    private void report(final JPackage pkg, final String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("File name is null");
        }

        final String pkgName;
        final String fileNameOut;
        if (pkg != null && (pkgName = pkg.name().replace('.', File.separatorChar)).length() > 0 ) {
            final StringBuilder sb = new StringBuilder(fileName.length() + pkgName.length() + 1);
            sb.append(pkgName);
            sb.append(File.separatorChar);
            sb.append(fileName);
            fileNameOut = sb.toString();
        } else {
            fileNameOut = fileName;
        }

        if(progress.isCanceled())
            throw new AbortException();
        progress.generatedFile(fileNameOut, current++, totalFileCount);
    }
}
