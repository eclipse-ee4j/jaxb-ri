/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDeclaration;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import junit.framework.TestCase;
import org.xml.sax.InputSource;

/**
 *
 * @author lukas
 */
public class CodeGenTest extends TestCase {

    public void testGh1460_Gh1064() throws Throwable {
        SchemaCompiler sc = XJC.createSchemaCompiler();
        sc.forcePackageName("ghbugs.b1460");
        sc.parseSchema(getInputSource("/schemas/ghbugs.xsd"));
        S2JJAXBModel model = sc.bind();
        JCodeModel code = model.generateCode(null, null);
        String method = toString(code._getClass("ghbugs.b1460.Gh1460Type").getMethod("setBinaryAttr", new JType[]{code.parseType("byte[][]")}));
//        com.sun.tools.xjc.api.Driver.dumpCode(code);
//        System.out.println(method);

        // wrong initialization of multi-dim array
        // https://github.com/eclipse-ee4j/jaxb-ri/issues/1460
        assertTrue(method, method.contains("new byte[len][]"));

        // null check in setter
        // https://github.com/eclipse-ee4j/jaxb-ri/issues/1064
        assertTrue(method, method.contains("if (values == null) {"));
        assertTrue(method, method.contains("this.binaryAttr = null;"));
        assertTrue(method, method.contains("return ;"));
    }

    private InputSource getInputSource(String systemId) throws FileNotFoundException, URISyntaxException {
        URL url = CodeGenTest.class.getResource(systemId);
        File f = new File(url.toURI());
        InputSource is = new InputSource(new FileInputStream(f));
        is.setSystemId(f.toURI().toString());
        return is;
    }

    private static String toString(JDeclaration generable) {
        if (generable == null) {
            throw new IllegalArgumentException("Generable must not be null.");
        }
        final StringWriter stringWriter = new StringWriter();
        final JFormatter formatter = new JFormatter(stringWriter);
        generable.declare(formatter);
        return stringWriter.toString();
    }
}
