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

/**
 * Parent of a {@link CClassInfo}/{@link CElementInfo}.
 *
 * TODO: rename
 *
 * Either {@link CClassInfo} or {@link CClassInfoParent.Package}.
 */
public interface CClassInfoParent {
    /**
     * Returns the fully-qualified name.
     */
    String fullName();

    <T> T accept( Visitor<T> visitor );

    /**
     * Gets the nearest {@link JPackage}.
     */
    JPackage getOwnerPackage();

    /**
     * Visitor of {@link CClassInfoParent}
     */
    public static interface Visitor<T> {
        T onBean( CClassInfo bean );
        T onPackage( JPackage pkg );
        T onElement( CElementInfo element );
    }

    /**
     * {@link JPackage} as a {@link CClassInfoParent}.
     *
     * Use {@link Model#getPackage} to obtain an instance.
     */
    public static final class Package implements CClassInfoParent {
        public final JPackage pkg;

        public Package(JPackage pkg) {
            this.pkg = pkg;
        }

        public String fullName() {
            return pkg.name();
        }

        public <T> T accept(Visitor<T> visitor) {
            return visitor.onPackage(pkg);
        }

        public JPackage getOwnerPackage() {
            return pkg;
        }
    }
}
