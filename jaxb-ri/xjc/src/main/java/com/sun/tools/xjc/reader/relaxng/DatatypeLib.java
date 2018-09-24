/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.relaxng;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.reader.xmlschema.SimpleTypeBuilder;

import com.sun.tools.rngom.xml.util.WellKnownNamespaces;

/**
 * Data-bindable datatype library.
 *
 * @author Kohsuke Kawaguchi
 */
final class DatatypeLib {
    /**
     * Datatype library's namespace URI.
     */
    public final String nsUri;
    private final Map<String,TypeUse> types;

    public DatatypeLib(String nsUri, Map<String,TypeUse> types) {
        this.nsUri = nsUri;
        this.types = Collections.unmodifiableMap(types);
    }

    /**
     * Maps the type name to the information.
     */
    TypeUse get(String name) {
        return types.get(name);
    }

    /**
     * Datatype library for the built-in type.
     */
    public static final DatatypeLib BUILTIN;

    /**
     * Datatype library for XML Schema datatypes.
     */
    public static final DatatypeLib XMLSCHEMA =
            new DatatypeLib(
                    WellKnownNamespaces.XML_SCHEMA_DATATYPES,
                    SimpleTypeBuilder.builtinConversions);

    static {
        Map<String,TypeUse> builtinTypes = new HashMap<>();
        builtinTypes.put("token", CBuiltinLeafInfo.TOKEN);
        builtinTypes.put("string", CBuiltinLeafInfo.STRING);

        BUILTIN = new DatatypeLib("", builtinTypes);
    }
}
