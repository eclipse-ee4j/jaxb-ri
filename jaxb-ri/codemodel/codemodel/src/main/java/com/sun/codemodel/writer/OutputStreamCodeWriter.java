/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Output all source files into a single stream.
 * 
 * This is primarily for test purposes.
 * 
 * @author
 * 	Aleksei Valikov (valikov@gmx.net)
 */
package com.sun.codemodel.writer;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;

public class OutputStreamCodeWriter extends CodeWriter {
	private final PrintStream out;

	/**
	 * @param os
	 *            This stream will be closed at the end of the code generation.
	 */
	public OutputStreamCodeWriter(OutputStream os, String encoding) {
		try {
			this.out = new PrintStream(os, false, encoding);
		} catch (UnsupportedEncodingException ueex) {
			throw new IllegalArgumentException(ueex);
		}
		this.encoding = encoding;
	}

        @Override
	public OutputStream openBinary(JPackage pkg, String fileName)
			throws IOException {
		return new FilterOutputStream(out) {
                @Override
			public void close() {
				// don't let this stream close
			}
		};
	}

        @Override
	public void close() throws IOException {
		out.close();
	}
}
