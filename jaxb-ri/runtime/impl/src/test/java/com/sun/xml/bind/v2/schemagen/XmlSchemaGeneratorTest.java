/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.schemagen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class XmlSchemaGeneratorTest extends TestCase {
    public static void main(String[] args) {
        TestRunner.run(XmlSchemaGeneratorTest.class);
    }
    
    public void test2() throws Exception {
        try {
            JAXBContext context = JAXBContext.newInstance(NSParent.class);
            MySchemaOutputResolver resolver = new MySchemaOutputResolver();
            context.generateSchema(resolver);
            assertTrue(checkNamespaces());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private static boolean checkNamespaces() throws Exception {
        FileInputStream schema1 = new FileInputStream("schema1.xsd");
        BufferedReader reader = new BufferedReader(new InputStreamReader(schema1));
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("ref=\"ns1:myRef\"")) {
                    return true;
                }
            }
        } finally {
            reader.close();
        }
        return false;
    }

    public void test1() throws Exception {
        String[] uris = {
            "http://foo.org/a/b/c", "http://bar.org/d/e/f", "http://foo.org/a/b/c", "true",
            "http://foo.org/a/b/c", "http://foo.org/a/d/e/f", "../../b/c", "true",
            "http://foo.org/a/b/c", "http://foo.org/d/e/f", "../../a/b/c", "true",
            "http://www.sun.com/abc/def", "http://www.sun.com/pqr/stu", "../abc/def", "true",
            "http://foo/bar", "http://foo/baz/zot", "../bar", "true",
            "http://foo/bar", "http://foo/bar/zot", "../bar", "true",
            "file:///path/with space/foo", "file:///path/with space/foo/bar", "../foo", "false",
            "file://c:/path/with space/foo", "file://c:/path/with space/foo/bar", "../foo", "false",
            "file:///path/with space/a/b/c", "file:///path/with space/d/e/f", "../../a/b/c", "false",
            "file://c:/path/with space/a/b/c", "file://c:/path/with space/a/d/e/f", "../../b/c", "false",
            "file:/path/with space/foo", "file:/path/with space/foo/bar", "../foo", "false",
            "file:/c:/path/with space/foo", "file:/c:/path/with space/foo/bar", "../foo", "false",
            "file:/path/with space/a/b/c", "file:/path/with space/d/e/f", "../../a/b/c", "false",
            "file:/c:/path/with space/a/b/c", "file:/c:/path/with space/a/d/e/f", "../../b/c", "false",
            "http://foo.org/foo/bar/", "http://foo.org/foo/bar/zot", ".", "true",
            "http://foo.org/foo/bar", "http://foo.org/foo/bar/zot/", "../../bar", "true",
            "http://foo.org/foo/bar/", "http://foo.org/foo/bar/zot/", "../", "true",
        };

        boolean failures = false;

        PrintWriter writer = new PrintWriter(System.out);
        writer.printf( "%-40s%-40s%-30s%-30s%-10s%-10s\n", "uri", "base uri", "relativized", "expected result", "match?", "resolve?" );

        for( int i = 0; i < uris.length; i+=4 ) {
            String result = XmlSchemaGenerator.relativize(uris[i], uris[i+1]);

            boolean match = result.equals(uris[i+2]);

            // is this particular test case expected to resolve?
            boolean resolvable = Boolean.valueOf(uris[i+3]);

            boolean resolve = resolve(uris[i], uris[i+1], result);

            if ((!match) || (( resolvable && !resolve)) ) {
                // the relative uri doesn't:
                //     a). match what we expect
                //     b). doesn't resolve but is expected to
                failures = true;
            }

            writer.printf( "%-40s%-40s%-30s%-30s%-10b%-10b\n",
                    uris[i],
                    uris[i+1],
                    result,
                    uris[i+2],
                    match,
                    resolve);
            writer.flush();
        }

        writer.close();

        // assert at the end so all of the output shows up in the log
        assertFalse(failures);
    }

    /*
     * resolve the relative against the base and see if it equals the original uri
     */
    private static boolean resolve(String uriPath, String basePath, String relativePath) {
        URI uri, base, relative;

        try {
            uri = new URI(uriPath);
            base = new URI(basePath);
            relative = new URI(relativePath);
        } catch (URISyntaxException e) {
            return false;
        }

        return (base.resolve(relative)).equals(uri);
    }

    static class MySchemaOutputResolver extends SchemaOutputResolver {

        @Override
        public Result createOutput(String arg0, String arg1) throws IOException {
            return new StreamResult(new File(arg1).toURI().getPath());
        }
    }
}
