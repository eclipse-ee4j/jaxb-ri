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
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import com.sun.tools.txw2.model.NodeSet;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.opts.BooleanOption;
import org.kohsuke.args4j.opts.StringOption;
import com.sun.tools.rngom.parse.IllegalSchemaException;
import com.sun.tools.rngom.parse.Parseable;
import com.sun.tools.rngom.parse.compact.CompactParseable;
import com.sun.tools.rngom.parse.xml.SAXParseable;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * Programatic entry point to the TXW compiler.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Main {
    private final TxwOptions opts;

    public Main(TxwOptions opts) {
        this.opts = opts;
    }

    public static void main(String[] args) {
        System.exit(run(args));
    }

    public static class Options {
        public StringOption output = new StringOption("-o");
        public StringOption pkg = new StringOption("-p");
        public BooleanOption compact = new BooleanOption("-c");
        public BooleanOption xml = new BooleanOption("-x");
        public BooleanOption xsd = new BooleanOption("-xsd");
        public BooleanOption chain = new BooleanOption("-h");
    }

    public static int run(String[] args) {
        Options opts = new Options();
        CmdLineParser parser = new CmdLineParser();
        parser.addOptionClass(opts);

        try {
            parser.parse(args);
        } catch (CmdLineException e) {
            System.out.println(e.getMessage());
            printUsage();
            return 1;
        }

        TxwOptions topts = new TxwOptions();
        topts.errorListener = new ConsoleErrorReporter(System.out);

        if(opts.output.value!=null) {
            try {
                topts.codeWriter = new FileCodeWriter(new File(opts.output.value));
            } catch( IOException e ) {
                System.out.println(e.getMessage());
                printUsage();
                return 1;
            }
        } else {
            topts.codeWriter = new SingleStreamCodeWriter(System.out);
        }

        if(opts.chain.isOn()) {
            topts.chainMethod = true;
        }

        if(opts.pkg.value!=null) {
            topts._package = topts.codeModel._package(opts.pkg.value);
        } else {
            topts._package = topts.codeModel.rootPackage();
        }

        // make sure that there's only one argument (namely the schema)
        if(parser.getArguments().size()!=1) {
            printUsage();
            return 1;
        }

        try {
            topts.source = makeSourceSchema(parser,opts,topts.errorListener);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            printUsage();
            return 1;
        }

        return run(topts);
    }

    /**
     * Parses the command line and makes a {@link Parseable} object
     * out of the specified schema file.
     */
    private static SchemaBuilder makeSourceSchema(CmdLineParser parser, Options opts, ErrorHandler eh) throws MalformedURLException {
        File f = new File((String)parser.getArguments().get(0));
        final InputSource in = new InputSource(f.toURL().toExternalForm());

        if(opts.xsd.isOff() && opts.xml.isOff() && opts.compact.isOff()) {
            // auto detect
            if(in.getSystemId().endsWith(".rnc"))
                opts.compact.value=true;
            else
            if(in.getSystemId().endsWith(".rng"))
                opts.xml.value=true;
            else
                opts.xsd.value=true;
        }

        if(opts.xsd.isOn())
            return new XmlSchemaLoader(in);

        final Parseable parseable = makeRELAXNGSource(opts, in, eh, f);

        return new RELAXNGLoader(parseable);
    }

    private static Parseable makeRELAXNGSource(Options opts, final InputSource in, ErrorHandler eh, File f) {
        if(opts.compact.isOn())
            return new CompactParseable(in,eh);

        if(opts.xml.isOn())
            return new SAXParseable(in,eh);

        // otherwise sniff from the file extension
        if(f.getPath().toLowerCase().endsWith("rnc"))
            return new CompactParseable(in,eh);
        else
            return new SAXParseable(in,eh);
    }

    private static void printUsage() {
        System.out.println("Typed Xml Writer ver."+getVersion());
        System.out.println(
            "txw <schema file>\n"+
            " -o <dir>   : Specify the directory to place generated source files\n"+
            " -p <pkg>   : Specify the Java package to put the generated classes into\n"+
            " -c         : The input schema is written in the RELAX NG compact syntax\n"+
            " -x         : The input schema is written in the RELAX NG XML syntax\n"+
            " -xsd       : The input schema is written in the XML SChema\n"+
            " -h         : Generate code that allows method invocation chaining\n"
        );
    }

    public static int run(TxwOptions opts) {
        return new Main(opts).run();
    }

    private int run() {
        try {
            NodeSet ns = opts.source.build(opts);
            ns.write(opts);
            opts.codeModel.build(opts.codeWriter);
            return 0;
        } catch (SAXParseException e) {
            opts.errorListener.error(e);
            return 1;
        } catch (IOException | IllegalSchemaException | SAXException e) {
            opts.errorListener.error(new SAXParseException(e.getMessage(),null,e));
            return 1;
        }
    }


    /**
     * Gets the version number of TXW.
     * @return TXW version
     */
    public static String getVersion() {
        try {
            Properties p = new Properties();
            p.load(Main.class.getResourceAsStream("version.properties"));
            return p.get("version").toString();
        } catch( Throwable t ) {
            return "unknown";
        }
    }

}
