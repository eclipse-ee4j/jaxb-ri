/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * @(#)$Id: EncoderFactory.java,v 1.3 2005/09/10 19:07:33 kohsuke Exp $
 */
package com.sun.codemodel.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 * Creates {@link CharsetEncoder} from a charset name.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class EncoderFactory {

    public static CharsetEncoder createEncoder( String encoding ) {
        Charset cs = Charset.forName(encoding == null
                ? System.getProperty("file.encoding")
                : encoding);
        CharsetEncoder encoder = cs.newEncoder();
        return encoder;
    }
}
