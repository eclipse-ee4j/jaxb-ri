/*
 * Copyright (C) 2004-2011
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sun.tools.rngom.dt.builtin;

import com.sun.tools.rngom.xml.util.WellKnownNamespaces;
import com.sun.tools.rngdatatype.Datatype;
import com.sun.tools.rngdatatype.DatatypeBuilder;
import com.sun.tools.rngdatatype.DatatypeException;
import com.sun.tools.rngdatatype.DatatypeLibrary;
import com.sun.tools.rngdatatype.DatatypeLibraryFactory;

class CompatibilityDatatypeLibrary implements DatatypeLibrary {
    private final DatatypeLibraryFactory factory;
    private DatatypeLibrary xsdDatatypeLibrary = null;

    CompatibilityDatatypeLibrary(DatatypeLibraryFactory factory) {
        this.factory = factory;
    }
    
    public DatatypeBuilder createDatatypeBuilder(String type)
        throws DatatypeException {
        if (type.equals("ID")
            || type.equals("IDREF")
            || type.equals("IDREFS")) {
            if (xsdDatatypeLibrary == null) {
                xsdDatatypeLibrary =
                    factory.createDatatypeLibrary(
                        WellKnownNamespaces.XML_SCHEMA_DATATYPES);
                if (xsdDatatypeLibrary == null)
                    throw new DatatypeException();
            }
            return xsdDatatypeLibrary.createDatatypeBuilder(type);
        }
        throw new DatatypeException();
    }

    public Datatype createDatatype(String type) throws DatatypeException {
        return createDatatypeBuilder(type).createDatatype();
    }
}
