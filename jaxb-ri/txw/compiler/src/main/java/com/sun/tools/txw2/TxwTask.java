/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2;

import com.sun.codemodel.writer.FileCodeWriter;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import com.sun.tools.rngom.parse.compact.CompactParseable;
import com.sun.tools.rngom.parse.xml.SAXParseable;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

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
    private static enum Style {
        COMPACT, XML, XMLSCHEMA, AUTO_DETECT
    }
    private Style style = Style.AUTO_DETECT;

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
        try {
            options.codeWriter = new FileCodeWriter(dir);
        } catch (IOException e) {
            throw new BuildException(e);
        }
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
     * launch txw
     */
    public void execute() throws BuildException {
        options.errorListener = new AntErrorListener(getProject());

        // when we run in Mustang, relaxng datatype gets loaded from tools.jar
        // and thus without setting the context classloader, they won't find
        // any datatype libraries
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        try {
            InputSource in = new InputSource(schemaFile.toURL().toExternalForm());

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
}
