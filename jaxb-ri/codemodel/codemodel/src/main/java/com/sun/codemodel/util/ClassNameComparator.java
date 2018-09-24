/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.util;

import java.util.Comparator;
import com.sun.codemodel.JClass;

/**
 * Comparator object that sorts {@link JClass}es in the order
 * of their names.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ClassNameComparator implements Comparator<JClass> {
    private ClassNameComparator() {}
    
    public int compare(JClass l, JClass r) {
        return l.fullName().compareTo(r.fullName());
    }

    public static final Comparator<JClass> theInstance = new ClassNameComparator();
}
