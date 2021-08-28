/**
 * Copyright (c) 2001, Thai Open Source Software Center Ltd. All rights reserved.
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.tools.txw2;

import com.sun.tools.txw2.builder.relaxng.SchemaBuilderImpl;
import com.sun.tools.txw2.model.Leaf;
import com.sun.tools.txw2.model.NodeSet;
import com.sun.tools.rngom.ast.util.CheckingSchemaBuilder;
import com.sun.tools.rngom.dt.CascadingDatatypeLibraryFactory;
import com.sun.tools.rngom.dt.builtin.BuiltinDatatypeLibraryFactory;
import com.sun.tools.rngom.parse.IllegalSchemaException;
import com.sun.tools.rngom.parse.Parseable;
import com.sun.tools.rngdatatype.DatatypeLibrary;
import com.sun.tools.rngdatatype.DatatypeLibraryFactory;

import java.util.ServiceLoader;

/**
 * @author Kohsuke Kawaguchi
 */
class RELAXNGLoader implements SchemaBuilder {
    private final Parseable parseable;

    public RELAXNGLoader(Parseable parseable) {
        this.parseable = parseable;
    }

    @Override
    public NodeSet build(TxwOptions options) throws IllegalSchemaException {
        SchemaBuilderImpl stage1 = new SchemaBuilderImpl(options.codeModel);
        DatatypeLibraryLoader loader = new DatatypeLibraryLoader(getClass().getClassLoader());
        @SuppressWarnings("unchecked")
        Leaf pattern = (Leaf)parseable.parse(new CheckingSchemaBuilder(stage1,options.errorListener,
            new CascadingDatatypeLibraryFactory(
                new BuiltinDatatypeLibraryFactory(loader),loader)));

        return new NodeSet(options,pattern);
    }

    private static final class DatatypeLibraryLoader implements DatatypeLibraryFactory {
        private final ServiceLoader<DatatypeLibraryFactory> service;

        private DatatypeLibraryLoader(ClassLoader cl) {
            service = ServiceLoader.load(DatatypeLibraryFactory.class, cl);
        }

        @Override
        public DatatypeLibrary createDatatypeLibrary(String uri) {
            for (DatatypeLibraryFactory factory : service) {
                DatatypeLibrary library = factory.createDatatypeLibrary(uri);
                if (library != null)
                    return library;
            }
            return null;
        }

    }
}
