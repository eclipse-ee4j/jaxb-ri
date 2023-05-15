/*
 * Copyright (c) 2022, 2023 Eclipse Foundation
 * Copyright (c) 2001, Thai Open Source Software Center Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     Neither the name of the Thai Open Source Software Center Ltd nor
 *     the names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior written
 *     permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.tools.rngdatatype.helpers;

import com.sun.tools.rngdatatype.DatatypeLibrary;
import com.sun.tools.rngdatatype.DatatypeLibraryFactory;

import java.util.ServiceLoader;

/**
 * Uses {@code java.util.ServiceLoader} to discover the datatype library implementation
 * from the module path or the classpath.
 * <p>
 * The call of the createDatatypeLibrary method finds an implementation
 * from a given datatype library URI at run-time.
 */
public class DatatypeLibraryLoader implements DatatypeLibraryFactory {

    /**
     * Default constructor.
     */
    public DatatypeLibraryLoader() {
    }

    @Override
    public DatatypeLibrary createDatatypeLibrary(String uri) {
        ServiceLoader<DatatypeLibraryFactory> loader = ServiceLoader.load(DatatypeLibraryFactory.class);
        for (DatatypeLibraryFactory factory : loader) {
            DatatypeLibrary library = factory.createDatatypeLibrary(uri);
            if (library != null) {
                return library;
            }
        }
        return null;
    }


}

