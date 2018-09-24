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

import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.api.ClassNameAllocator;

/**
 * Wraps {@link ClassNameAllocator} and provides convenience.
 *
 * @author Kohsuke Kawaguchi
 */
final class ClassNameAllocatorWrapper implements ClassNameAllocator {
    private final ClassNameAllocator core;

    ClassNameAllocatorWrapper(ClassNameAllocator core) {
        if(core==null)
            core = new ClassNameAllocator() {
                public String assignClassName(String packageName, String className) {
                    return className;
                }
            };
        this.core = core;
    }

    public String assignClassName(String packageName, String className) {
        return core.assignClassName(packageName,className);
    }

    public String assignClassName(JPackage pkg, String className) {
        return core.assignClassName(pkg.name(),className);
    }

    public String assignClassName(CClassInfoParent parent, String className) {
        if (parent instanceof CClassInfoParent.Package) {
            CClassInfoParent.Package p = (CClassInfoParent.Package) parent;
            return assignClassName(p.pkg,className);
        }
        // not a package-level class
        return className;
    }
}
