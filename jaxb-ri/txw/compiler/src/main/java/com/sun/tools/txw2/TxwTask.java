/*
 * Copyright (c) 2005, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.FilterCodeWriter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import com.sun.tools.rngom.parse.compact.CompactParseable;
import com.sun.tools.rngom.parse.xml.SAXParseable;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ant task interface for txw compiler.
 *
 * @author ryan_shoemaker@dev.java.net
 */
public class TxwTask extends org.apache.tools.ant.Task {

    // txw options - reuse command line options from the main driver
    private final TxwOptions options = new TxwOptions();

    // schema file
    private File schemaFile;

    // syntax style of RELAX NG source schema - "xml" or "compact"
    private enum Style {
        COMPACT, XML, XMLSCHEMA, AUTO_DETECT
    }
    private Style style = Style.AUTO_DETECT;

    private File license;

    private String encoding;

    private File destDir;

    public TxwTask() {
        // default package
        options._package = options.codeModel.rootPackage();

        // default codewriter
        try {
            options.codeWriter = new FileCodeWriter(new File("."));
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * Parse @package
     *
     * @param pkg name of the package to generate the java classes into
     */
    public void setPackage( String pkg ) {
        options._package = options.codeModel._package( pkg );
    }

    /**
     * Parse @syntax
     *
     * @param style either "compact" for RELAX NG compact syntax or "XML"
     * for RELAX NG xml syntax
     */
    public void setSyntax( String style ) {
        this.style = Style.valueOf(style.toUpperCase());
    }

    /**
     * parse @schema
     *
     * @param schema the schema file to be processed by txw
     */
    public void setSchema( File schema ) {
        schemaFile = schema;
    }

    /**
     * parse @destdir
     *
     * @param dir the directory to produce generated source code in
     */
    public void setDestdir( File dir ) {
        destDir = dir;
    }

    /**
     * parse @methodChaining
     *
     * @param flg true if the txw should generate api's that allow
     * method chaining (when possible, false otherwise
     */
    public void setMethodChaining( boolean flg ) {
        options.chainMethod = flg;
    }

    /**
     * parse @license
     *
     * @param license the license to append to the generated source code
     */
    public void setLicense( File license ) {
        this.license = license;
    }

    /**
     * parse @encoding
     *
     * @param encoding the encoding for the generated source code
     */
    public void setEncoding( String encoding ) {
        this.encoding = encoding;
    }

    /**
     * launch txw
     */
    @Override
    public void execute() throws BuildException {
        try {
            options.codeWriter = new FileCodeWriter(destDir, encoding != null ? encoding : StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            throw new BuildException(e);
        }
        if (license != null) {
            options.codeWriter = new LicenseCodeWriter(options.codeWriter, license, encoding != null ? encoding : StandardCharsets.UTF_8.name());
        }

        options.errorListener = new AntErrorListener(getProject());

        // when we run in Mustang, relaxng datatype gets loaded from tools.jar
        // and thus without setting the context classloader, they won't find
        // any datatype libraries
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        try {
            InputSource in = new InputSource(schemaFile.toURI().toURL().toExternalForm());

            String msg = "Compiling: " + in.getSystemId();
            log( msg, Project.MSG_INFO );

            if(style==Style.AUTO_DETECT) {
                String fileName = schemaFile.getPath().toLowerCase();
                if(fileName.endsWith("rnc"))
                    style = Style.COMPACT;
                else
                if(fileName.endsWith("xsd"))
                    style = Style.XMLSCHEMA;
                else
                    style = Style.XML;
            }

            switch(style) {
            case COMPACT:
                options.source = new RELAXNGLoader(new CompactParseable(in,options.errorListener));
                break;
            case XML:
                options.source = new RELAXNGLoader(new SAXParseable(in,options.errorListener));
                break;
            case XMLSCHEMA:
                options.source = new XmlSchemaLoader(in);
                break;
            }
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }

        // kick off the compiler
        Main.run(options);
        log( "Compilation complete.", Project.MSG_INFO );
    }

    /**
     * Writes all the source files under the specified file folder and inserts a
     * license file each java source file.
     */
    private static final class LicenseCodeWriter extends FilterCodeWriter {

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
        LicenseCodeWriter(CodeWriter core, File license, String encoding) {
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
}
