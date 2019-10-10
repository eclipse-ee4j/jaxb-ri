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

import com.sun.tools.txw2.model.NodeSet;
import com.sun.tools.rngom.parse.IllegalSchemaException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;

/**
 * Programmatic entry point to the TXW compiler.
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

    public static int run(String[] args) {
        TxwOptions topts = new TxwOptions();

        if (args.length == 0) {
            topts.printUsage();
            return 0;
        }

        topts.errorListener = new ConsoleErrorReporter(System.out);
        try {
            topts.parseArguments(args);
        } catch (BadCommandLineException e) {
            // there was an error in the command line.
            // print usage and abort.
            if(e.getMessage()!=null) {
                System.out.println(e.getMessage());
                System.out.println();
            }
            topts.printUsage();
            return -1;
        }

        return run(topts);
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

}
