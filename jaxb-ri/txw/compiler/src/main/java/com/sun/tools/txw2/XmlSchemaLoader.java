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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.sun.tools.txw2.model.NodeSet;
import com.sun.tools.txw2.builder.xsd.XmlSchemaBuilder;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * @author Kohsuke Kawaguchi
 */
class XmlSchemaLoader implements SchemaBuilder {
    private final InputSource in;

    public XmlSchemaLoader(InputSource in) {
        this.in = in;
    }

    public NodeSet build(TxwOptions options) throws SAXException {
        XSOMParser xsom = new XSOMParser();
        xsom.parse(in);
        return XmlSchemaBuilder.build(xsom.getResult(),options);
    }
}
