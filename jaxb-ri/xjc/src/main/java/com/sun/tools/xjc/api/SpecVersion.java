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
    V2_0, V2_1, V2_2;

    /**
     * Returns true if this version is equal or later than the given one.
     */
    public boolean isLaterThan(SpecVersion t) {
        return this.ordinal()>=t.ordinal();
    }

    /**
     * Parses "2.0", "2.1", and "2.2" into the {@link SpecVersion} object.
     *
     * @return null for parsing failure.
     */
    public static SpecVersion parse(String token) {
        if(token.equals("2.0"))
            return V2_0;
        if(token.equals("2.1"))
            return V2_1;
        if(token.equals("2.2"))
            return V2_2;
        return null;
    }

    /**
     * Gives the String representation of the {@link SpecVersion}
     */
    public String getVersion(){
        switch(this){
            case V2_0:
                return "2.0";
            case V2_1:
                return "2.1";
            case V2_2:
                return "2.2";
            default:
                return null;
        }
    }

    public static final SpecVersion LATEST = V2_2;
}
