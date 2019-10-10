/*
 * Copyright (c) 2005, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import com.sun.tools.rngom.parse.Parseable;
import com.sun.tools.rngom.parse.compact.CompactParseable;
import com.sun.tools.rngom.parse.xml.SAXParseable;
import com.sun.xml.txw2.annotation.XmlNamespace;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

/**
 * Controls the various aspects of the TXW generation.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class TxwOptions {

    public final JCodeModel codeModel = new JCodeModel();

    /**
     * The package to put the generated code into.
     */
    public JPackage _package;

    /**
     * Always non-null.
     */
    public ErrorListener errorListener;

    /**
     * The generated code will be sent to this.
     */
    CodeWriter codeWriter;

    /**
     * Schema file.
     */
    SchemaBuilder source;

    /**
     * If true, generate attribute/value methods that returns the
     * <code>this</code> object for chaining.
     */
    public boolean chainMethod;

    /**
     * If true, the generated code will not use the package-level
     * {@link XmlNamespace} annotation.
     */
    public boolean noPackageNamespace;

    /**
     * Type of input schema language. One of the {@code Language}
     * constants.
     */
    public Language language;

    /**
     * Type of the schema language.
     */
    public enum Language {
        XML,
        XMLSCHEMA,
        COMPACT
    }

    void parseArguments(String[] args) throws BadCommandLineException {
        String src = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].length() == 0) {
                throw new BadCommandLineException(getMessage("missingOperand"));
            }
            if (args[i].charAt(0) == '-') {
                int j = parseArgument(args, i);
                if (j == 0) {
                    throw new BadCommandLineException(getMessage("unrecognizedParameter", args[i]));
                }
                i += (j - 1);
            } else {
                src = args[i];
            }
        }
        if (_package == null) {
            _package = codeModel.rootPackage();
        }
        if (codeWriter == null) {
            codeWriter = new SingleStreamCodeWriter(System.out);
        }
        if (src == null) {
            throw new BadCommandLineException(getMessage("missingOperand"));
        }
        try {
            source = makeSourceSchema(src, errorListener);
        } catch (MalformedURLException mue) {
            throw new BadCommandLineException(mue.getMessage(), mue);
        }
    }

    private int parseArgument(String[] args, int i) throws BadCommandLineException {
        if (args[i].equals("-o")) {
            File targetDir = new File(requireArgument("-o", args, ++i));
            try {
                codeWriter = new FileCodeWriter(targetDir);
            } catch (IOException ex) {
                throw new BadCommandLineException(ex.getMessage(), ex);
            }
            return 2;
        }
        if (args[i].equals("-p")) {
            _package = codeModel._package(requireArgument("-p", args, ++i));
            return 2;
        }
        if (args[i].equals("-c")) {
            language = Language.COMPACT;
            return 1;
        }
        if (args[i].equals("-x")) {
            language = Language.XML;
            return 1;
        }
        if (args[i].equals("-xsd")) {
            language = Language.XMLSCHEMA;
            return 1;
        }
        if (args[i].equals("-h")) {
            chainMethod = true;
            return 1;
        }
        return 0;   // unrecognized
    }

    private String requireArgument(String d, String[] args, int i) throws BadCommandLineException {
        if (i == args.length || args[i].startsWith("-")) {
            throw new BadCommandLineException(getMessage("missingOperand"));
        }
        return args[i];
    }

    public void printUsage() {
        System.out.println(getMessage("usage"));
    }

    /**
     * Parses the command line and makes a {@link Parseable} object out of the
     * specified schema file.
     */
    private SchemaBuilder makeSourceSchema(String src, ErrorHandler eh) throws BadCommandLineException, MalformedURLException {
        File f = new File(src);
        final InputSource in = new InputSource(f.toURI().toURL().toExternalForm());

        if (language == null) {
            // auto detect
            if (in.getSystemId().endsWith(".rnc")) {
                language = Language.COMPACT;
            } else if (in.getSystemId().endsWith(".rng")) {
                language = Language.XML;
            } else {
                language = Language.XMLSCHEMA;
            }
        }

        SchemaBuilder sb = null;
        switch (language) {
            case XMLSCHEMA:
                sb = new XmlSchemaLoader(in);
                break;
            case COMPACT:
                sb = new RELAXNGLoader(new CompactParseable(in, eh));
                break;
            case XML:
                sb = new RELAXNGLoader(new SAXParseable(in, eh));
                break;
            default:
                // should not happen
                throw new BadCommandLineException(getMessage("unknownGrammar"));
        }
        return sb;
    }

    /**
     * Gets the version number of TXW.
     *
     * @return TXW version
     */
    public static String getVersion() {
        try {
            Properties p = new Properties();
            p.load(TxwOptions.class.getResourceAsStream("version.properties"));
            return p.get("version").toString();
        } catch (Throwable t) {
            return "unknown";
        }
    }

    private static String getMessage(String key, Object... args) {
        String msg = ResourceBundle.getBundle(TxwOptions.class.getPackage().getName() + ".messages").getString(key);
        return MessageFormat.format(msg, args);
    }
}
