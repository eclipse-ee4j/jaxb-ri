/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.writer;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Writes all the source files under the specified file folder and inserts a
 * license file each java source file.
 *
 * @since 4.0.3
 */
public class LicenseCodeWriter extends FilterCodeWriter {

    private static final String COPYRIGHT_LINE_TEMPLATE = "^.*Copyright \\(c\\) (YYYY) (by )?([A-Za-z].*)$";
    private static final Pattern PATTERN = Pattern.compile(COPYRIGHT_LINE_TEMPLATE, Pattern.MULTILINE);
    private static final String CURRENT_YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

    private final Path license;

    /**
     * @param core     This CodeWriter will be used to actually create a storage
     *                 for files. LicenseCodeWriter simply decorates this underlying
     *                 CodeWriter by adding prolog comments.
     * @param license  license File
     * @param encoding encoding
     */
    public LicenseCodeWriter(CodeWriter core, File license, String encoding) {
        super(core);
        this.license = license.toPath();
        this.encoding = encoding;
    }

    @Override
    public Writer openSource(JPackage pkg, String fileName) throws IOException {
        Writer w = super.openSource(pkg, fileName);

        PrintWriter out = new PrintWriter(w);
        try (BufferedReader br = Files.newBufferedReader(license, Charset.forName(encoding))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = PATTERN.matcher(line);
                if (m.matches()) {
                    out.write(line, 0, m.start(1));
                    out.write(CURRENT_YEAR);
                    out.write(line, m.end(1), line.length() - m.end(1));
                } else {
                    out.write(line);
                }
                out.write(System.lineSeparator());
            }
        }

        out.flush();    // we can't close the stream for that would close the underlying stream.

        return w;
    }
}
