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

import com.sun.tools.txw2.model.NodeSet;
import com.sun.tools.rngom.parse.IllegalSchemaException;
import org.xml.sax.SAXException;

/**
 * Encapsulation of the schema file and the builder.
 * 
 * @author Kohsuke Kawaguchi
 */
public interface SchemaBuilder {
    NodeSet build(TxwOptions options) throws IllegalSchemaException, SAXException;
}
