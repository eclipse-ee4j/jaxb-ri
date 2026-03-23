/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.ap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sun.tools.xjc.BadCommandLineException;

/**
 * This stores the invocation configuration for
 * SchemaGenerator
 *
 * @author Bhakti Mehta
 */
public class Options  {

    public static final String DISABLE_XML_SECURITY = "-disableXmlSecurity";
    
    // honor CLASSPATH environment variable, but it will be overrided by -cp
    public String classpath = System.getenv("CLASSPATH");

    public File targetDir = null;

    public File episodeFile = null;
    
    private boolean disableXmlSecurity = false;

    // encoding is not required for JDK5, 6, but JDK 7 javac is much more strict - see issue 6859289
    public String encoding = null;

    public final List<String> arguments = new ArrayList<>();

    /**
     * Default constructor.
     */
    public Options() {}

    public void parseArguments(String[] args) throws BadCommandLineException {
        for (int i = 0 ; i <args.length; i++) {
            if (args[i].charAt(0)== '-') {
                i += parseArgument(args, i);
            } else {
                arguments.add(args[i]);
            }
        }
    }

    private int parseArgument( String[] args, int i ) throws BadCommandLineException {
        switch (args[i]) {
            case "-d" -> {
                if (i == args.length - 1)
                    throw new BadCommandLineException(
                            (Messages.OPERAND_MISSING.format(args[i])));
                targetDir = new File(args[++i]);
                if (!targetDir.exists())
                    throw new BadCommandLineException(
                            Messages.NON_EXISTENT_FILE.format(targetDir));
                return 1;
            }
            case "-episode" -> {
                if (i == args.length - 1)
                    throw new BadCommandLineException(
                            (Messages.OPERAND_MISSING.format(args[i])));
                episodeFile = new File(args[++i]);
                return 1;
            }
            case DISABLE_XML_SECURITY -> {
                disableXmlSecurity = true;
                return 0;
            }
            case "-encoding" -> {
                if (i == args.length - 1)
                    throw new BadCommandLineException(
                            (Messages.OPERAND_MISSING.format(args[i])));
                encoding = args[++i];
                return 1;
            }
            case "-cp", "-classpath" -> {
                if (i == args.length - 1)
                    throw new BadCommandLineException(
                            (Messages.OPERAND_MISSING.format(args[i])));
                classpath = args[++i];

                return 1;
            }
        }

        throw new BadCommandLineException(
                Messages.UNRECOGNIZED_PARAMETER.format(args[i]));
    }

    /**
     * @return the disableXmlSecurity
     */
    public boolean isDisableXmlSecurity() {
        return disableXmlSecurity;
    }

    

}



