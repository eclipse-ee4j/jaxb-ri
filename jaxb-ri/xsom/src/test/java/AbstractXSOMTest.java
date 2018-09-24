/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import junit.framework.TestCase;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;
import org.xml.sax.SAXException;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractXSOMTest extends TestCase {
    /**
     * Loads a schema set from XSDs in the resource.
     */
    protected final XSSchemaSet load(String... resourceNames) throws SAXException {
        XSOMParser p = new XSOMParser();
        for (String n : resourceNames) {
            p.parse(getClass().getResource(n));
        }
        return p.getResult();
    }
}
