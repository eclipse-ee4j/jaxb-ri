/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Util {
    /**
     * Copy all the bytes from input to output, but don't close the streams.
     */
    public static void copyStream( OutputStream out, InputStream in ) throws IOException {
        byte[] buf = new byte[256];
        int len;
        while((len=in.read(buf))!=-1)
            out.write(buf,0,len);
        out.flush();
    }
}
