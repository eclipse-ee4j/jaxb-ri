/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api;

/**
 * Represents the spec version constant.
 *
 * @author Kohsuke Kawaguchi
 */
public enum SpecVersion {
    V2_3("2.3"), V3_0("3.0");

    private final String version;

    private SpecVersion(String version) {
        this.version = version;
    }

    /**
     * Returns true if this version is equal or later than the given one.
     */
    public boolean isLaterThan(SpecVersion t) {
        return this.ordinal()>=t.ordinal();
    }

    /**
     * Parses "3.0" into the {@link SpecVersion} object.
     *
     * @return null for parsing failure.
     */
    public static SpecVersion parse(String token) {
        for (SpecVersion version : SpecVersion.values()) {
            if (version.getVersion().equals(token)) {
                return version;
            }
        }
        return null;
    }

    /**
     * Gives the String representation of the {@link SpecVersion}
     */
    public String getVersion(){
        return version;
    }

    public static final SpecVersion LATEST = V3_0;
}
