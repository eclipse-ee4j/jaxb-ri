/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader;

import com.sun.xml.bind.api.impl.NameConverter;


public class NameConverterDriver
{
    public static void main( String[] args ) {
        for( int i=0; i<args.length; i++ )
        System.out.println( NameConverter.standard.toConstantName(args[i]) );
    }
}
