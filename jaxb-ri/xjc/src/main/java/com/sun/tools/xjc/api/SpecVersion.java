/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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
    V3_0;

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
        if("3.0".equals(token)) {
            return V3_0;
        }
        return null;
    }

    /**
     * Gives the String representation of the {@link SpecVersion}
     */
    public String getVersion(){
        switch(this){
            case V3_0:
                return "3.0";
            default:
                return null;
        }
    }

    public static final SpecVersion LATEST = V3_0;
}
