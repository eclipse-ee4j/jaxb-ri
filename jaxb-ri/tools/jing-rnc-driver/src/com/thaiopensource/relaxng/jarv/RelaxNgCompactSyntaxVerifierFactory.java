/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.thaiopensource.relaxng.jarv;

import java.io.IOException;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import com.sun.tools.rngdatatype.DatatypeLibraryFactory;
import com.sun.tools.rngdatatype.helpers.DatatypeLibraryLoader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.impl.SchemaBuilderImpl;
import com.thaiopensource.relaxng.impl.SchemaPatternBuilder;
import com.thaiopensource.relaxng.parse.Parseable;
import com.thaiopensource.relaxng.parse.compact.CompactParseable;
import com.thaiopensource.validate.IncorrectSchemaException;
import com.thaiopensource.xml.sax.DraconianErrorHandler;

/**
 * {@link org.iso_relax.verifier.VerifierFactory} implementation
 * for RELAX NG Compact Syntax.
 * 
 * <p>
 * The reason why this class is in this package is to access
 * some of the package-private classes.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class RelaxNgCompactSyntaxVerifierFactory extends VerifierFactory {
    private final DatatypeLibraryFactory dlf = new DatatypeLibraryLoader();
    private final ErrorHandler eh = new DraconianErrorHandler();

    public Schema compileSchema(InputSource is) throws VerifierConfigurationException, SAXException, IOException {
        SchemaPatternBuilder spb = new SchemaPatternBuilder();
        Parseable parseable = new CompactParseable(is, eh);
        try {
          return new SchemaImpl(SchemaBuilderImpl.parse(parseable, eh, dlf, spb, false), spb);
        }
        catch (IncorrectSchemaException e) {
          throw new SAXException("unreported schema error");
        }
    }

}
