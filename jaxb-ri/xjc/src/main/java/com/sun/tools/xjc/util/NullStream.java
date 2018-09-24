/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Just consumes the byte stream. Kind of like /dev/null.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class NullStream extends OutputStream {

    public void write(int b) throws IOException {
    }
    
    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    public void write(byte[] b, int off, int len) throws IOException {
    }

    public void write(byte[] b) throws IOException {
    }
}
