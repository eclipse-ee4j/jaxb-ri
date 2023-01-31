/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Modifier groups.
 */
public class JMods implements JGenerable {

//
// mask
//
    private static int VAR = JMod.FINAL;
    private static int FIELD = (JMod.PUBLIC | JMod.PRIVATE | JMod.PROTECTED
            | JMod.STATIC | JMod.FINAL
            | JMod.TRANSIENT | JMod.VOLATILE);
    private static int METHOD = (JMod.PUBLIC | JMod.PRIVATE | JMod.PROTECTED | JMod.FINAL
            | JMod.ABSTRACT | JMod.STATIC | JMod.NATIVE | JMod.SYNCHRONIZED);
    private static int CLASS = (JMod.PUBLIC | JMod.PRIVATE | JMod.PROTECTED
            | JMod.STATIC | JMod.FINAL | JMod.ABSTRACT);
    private static int INTERFACE = JMod.PUBLIC;
    /** bit-packed representation of modifiers. */
    private int mods;

    JMods(int mods) {
        this.mods = mods;
    }

    /**
     * Gets the bit-packed representaion of modifiers.
     */
    public int getValue() {
        return mods;
    }

    private static void check(int mods, int legal, String what) {
        if ((mods & ~legal) != 0) {
            throw new IllegalArgumentException("Illegal modifiers for "
                    + what + ": "
                    + new JMods(mods).toString());
        }
        /* ## check for illegal combinations too */
    }

    static JMods forVar(int mods) {
        check(mods, VAR, "variable");
        return new JMods(mods);
    }

    static JMods forField(int mods) {
        check(mods, FIELD, "field");
        return new JMods(mods);
    }

    static JMods forMethod(int mods) {
        check(mods, METHOD, "method");
        return new JMods(mods);
    }

    static JMods forClass(int mods) {
        check(mods, CLASS, "class");
        return new JMods(mods);
    }

    static JMods forInterface(int mods) {
        check(mods, INTERFACE, "class");
        return new JMods(mods);
    }

    public boolean isAbstract() {
        return (mods & JMod.ABSTRACT) != 0;
    }

    public boolean isNative() {
        return (mods & JMod.NATIVE) != 0;
    }

    public boolean isSynchronized() {
        return (mods & JMod.SYNCHRONIZED) != 0;
    }

    public void setSynchronized(boolean newValue) {
        setFlag(JMod.SYNCHRONIZED, newValue);
    }

    public void setPrivate() {
        setFlag(JMod.PUBLIC, false);
        setFlag(JMod.PROTECTED, false);
        setFlag(JMod.PRIVATE, true);
    }

    public void setProtected() {
        setFlag(JMod.PUBLIC, false);
        setFlag(JMod.PROTECTED, true);
        setFlag(JMod.PRIVATE, false);
    }

    public void setPublic() {
        setFlag(JMod.PUBLIC, true);
        setFlag(JMod.PROTECTED, false);
        setFlag(JMod.PRIVATE, false);
    }

    public void setFinal(boolean newValue) {
        setFlag(JMod.FINAL, newValue);
    }

    private void setFlag(int bit, boolean newValue) {
        mods = (mods & ~bit) | (newValue ? bit : 0);
    }

    public void generate(JFormatter f) {
        if ((mods & JMod.PUBLIC) != 0)        f.p("public");
        if ((mods & JMod.PROTECTED) != 0)     f.p("protected");
        if ((mods & JMod.PRIVATE) != 0)       f.p("private");

        // Match canonical order in java.lang.reflect.Modifier::toString
        if ((mods & JMod.ABSTRACT) != 0)      f.p("abstract");
        if ((mods & JMod.STATIC) != 0)        f.p("static");
        if ((mods & JMod.FINAL) != 0)         f.p("final");
        if ((mods & JMod.TRANSIENT) != 0)     f.p("transient");
        if ((mods & JMod.VOLATILE) != 0)      f.p("volatile");
        if ((mods & JMod.SYNCHRONIZED) != 0)  f.p("synchronized");
        if ((mods & JMod.NATIVE) != 0)        f.p("native");
    }

    @Override
    public String toString() {
        StringWriter s = new StringWriter();
        JFormatter f = new JFormatter(new PrintWriter(s));
        this.generate(f);
        return s.toString();
    }
}
