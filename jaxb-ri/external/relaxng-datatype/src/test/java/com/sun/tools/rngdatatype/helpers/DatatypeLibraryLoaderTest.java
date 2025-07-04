/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.rngdatatype.helpers;

import com.sun.tools.rngdatatype.Datatype;
import com.sun.tools.rngdatatype.DatatypeException;
import com.sun.tools.rngdatatype.DatatypeLibrary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatatypeLibraryLoaderTest {

    @Test
    public void createExternalDatatypeLibrary() {
        org.relaxng.datatype.helpers.DatatypeLibraryLoader extLibraryLoader = new org.relaxng.datatype.helpers.DatatypeLibraryLoader();
        org.relaxng.datatype.DatatypeLibrary extDatatypeLibrary = extLibraryLoader.createDatatypeLibrary("http://www.w3.org/2001/XMLSchema-datatypes");
        assertNotNull(extDatatypeLibrary);
        try {
            org.relaxng.datatype.Datatype aFloat = extDatatypeLibrary.createDatatype("float");
            assertNotNull(aFloat);
            Object value = aFloat.createValue("150", null);
            assertNotNull(value);
        } catch (org.relaxng.datatype.DatatypeException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    public void createJAXBDatatypeLibrary() {
        DatatypeLibraryLoader libraryLoader = new DatatypeLibraryLoader();
        DatatypeLibrary datatypeLibrary = libraryLoader.createDatatypeLibrary("http://www.w3.org/2001/XMLSchema-datatypes");
        assertNotNull(datatypeLibrary);
        try {
            Datatype aFloat = datatypeLibrary.createDatatype("float");
            assertNotNull(aFloat);
            Object value = aFloat.createValue("150", null);
            assertNotNull(value);
        } catch (DatatypeException e) {
            throw new RuntimeException(e);
        }
    }
}