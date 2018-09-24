/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

/**
 * Constructor declaration.
 * 
 * <p>
 * a constructor declaration consists of a set of fields to be initialized.
 * For example, if a class is defined as:
 * 
 * <pre>
 * Class: Foo
 *   Field: String a
 *   Field: int b
 *   Field: BigInteger c
 * </pre>
 * 
 * Then a constructor declaration of {"a","c"} will conceptually
 * generate the following consturctor:
 * 
 * <pre>
 * Foo( String _a, BigInteger _c ) {
 *   a=_a; c=_c;
 * }
 * </pre>
 * 
 * (Only conceptually, because Foo will likely to become an interface
 * so we can't simply generate a constructor like this.)
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Constructor
{
    // Since Constructor is typically built when there is no FieldItem
    // nor FieldUse, we need to rely on Strings.
    public Constructor( String[] _fields ) { this.fields = _fields.clone(); }
    
    /** array of field names to be initialized. */
    public final String[] fields;
}
