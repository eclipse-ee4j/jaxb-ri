/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom;

/**
 * Constants that represent variety of simple types.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke,kawaguchi@sun.com)
 */
public final class XSVariety {
    public static final XSVariety ATOMIC = new XSVariety("atomic");
    public static final XSVariety UNION  = new XSVariety("union");
    public static final XSVariety LIST   = new XSVariety("list");
    
    private XSVariety(String _name) { this.name=_name; }
    private final String name;
    public String toString() { return name; }
}

