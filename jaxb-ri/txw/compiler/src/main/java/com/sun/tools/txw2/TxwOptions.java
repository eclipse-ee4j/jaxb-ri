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

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.xml.txw2.annotation.XmlNamespace;

/**
 * Controls the various aspects of the TXW generation.
 *
 * But this doesn't contain options for the command-line interface
 * nor any of the driver-level configuration (such as where to place
 * the generated source code, etc.)
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
     * If true, generate attribute/value methods that
     * returns the <tt>this</tt> object for chaining.
     */
    public boolean chainMethod;

    /**
     * If true, the generated code will not use the package-level
     * {@link XmlNamespace} annotation.
     */
    public boolean noPackageNamespace;
}
