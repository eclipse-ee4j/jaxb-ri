/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import static mockit.Deencapsulation.invoke;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.integration.junit4.JMockit;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author aefimov
 */

@RunWith(JMockit.class)
public final class SchemaGeneratorTest {

    @Test
    public void setClassPathTest() throws Exception {

        // Mocked URL instance that returns incorrect path
        // similar to behaviour on Windows platform
        final URL cUrl = new MockUp<URL>() {
            String path = "C:";

            @Mock
            public String getPath() {
                return "/" + path;
            }

            @Mock
            public URI toURI() {
                return new File(path).toURI();
            }
        }.getMockInstance();

        // Mocked URLClassLoder will return mocked URL
        new MockUp<URLClassLoader>() {
            @Mock
            URL[] getURLs() {
                URL[] urls = {
                    cUrl
                };
                return urls;
            }
        };

        //Mock the 'findJaxbApiJar' in SchemaGenerator class to avoid
        //additional calls to URL class
        new Expectations(SchemaGenerator.class) {{
                invoke(SchemaGenerator.class, "findJaxbApiJar"); result = "";
        }};

        //Invoke the method under test
        String result = invoke(SchemaGenerator.class, "setClasspath", "");
        String sepChar = File.pathSeparator;

        // When the URL path problem is fixed the following behaviour is expected:
        // On *nix plarforms the C: path will converted to "test dir path + path separator + C:"
        // On Windows "path separator + C:" will be returned
        // "path separator + /C:" should never be returned on any platform
        assertFalse("Result classpath contains incorrect drive path", result.contains(sepChar+"/C:"));
    }
}
