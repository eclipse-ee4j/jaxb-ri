/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Util {

    /**
     * Gets the whole contents of a file into a string by
     * using the system default encoding.
     */
    public static String getFileAsString(InputStream stream) {
        StringWriter sw = new StringWriter();
        try {
            copyStream(new InputStreamReader(stream),sw);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return sw.toString();
    }

    private static void copyStream(Reader in, Writer out) throws IOException {
        char[] buf = new char[256];
        int len;
        while((len=in.read(buf))>0) {
            out.write(buf,0,len);
        }
        in.close();
        out.close();
    }
}
