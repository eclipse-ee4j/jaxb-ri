/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.xml.sax.InputSource;

public class Util
{
    /**
     * Parses the specified string either as an {@link URL} or as a {@link File}.
     *
     * @throws IOException
     *      if the parameter is neither.
     */
    public static Object getFileOrURL(String fileOrURL) throws IOException {
        try {
            return new URL(fileOrURL);
        } catch (MalformedURLException e) {
            return new File(fileOrURL).getCanonicalFile();
        }
    }
    /**
     * Gets an InputSource from a string, which contains either
     * a file name or an URL.
     */
    public static InputSource getInputSource(String fileOrURL) {
        try {
            Object o = getFileOrURL(fileOrURL);
            if(o instanceof URL) {
                return new InputSource(escapeSpace(((URL)o).toExternalForm()));
            } else {
                String url = ((File)o).toURI().toURL().toExternalForm();
                return new InputSource(escapeSpace(url));
            }
        } catch (IOException e) {
            return new InputSource(fileOrURL);
        }
    }

    public static String escapeSpace( String url ) {
        // URLEncoder didn't work.
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < url.length(); i++) {
            // TODO: not sure if this is the only character that needs to be escaped.
            if (url.charAt(i) == ' ')
                buf.append("%20");
            else
                buf.append(url.charAt(i));
        }
        return buf.toString();
    }
}
