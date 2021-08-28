/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import java.io.PrintStream;

import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.outline.Outline;

/**
 * Call-back interface that can be implemented by the caller of {@link Driver}
 * to receive output from XJC.
 *
 * <p>
 * Most of the messages XJC produce once the real work starts is structured
 * as (message,source). Those outputs will be reported to various methods on
 * {@link ErrorListener}, which is inherited by this interface.
 *
 * <p>
 * The other messages (such as the usage screen when there was an error in
 * the command line option) will go to the {@link #message(String)} method.
 *
 * @author Kohsuke Kawaguchi
 * @since JAXB 2.0 EA
 */
public abstract class XJCListener implements ErrorListener {

    /**
     * @deprecated
     *      Override {@link #generatedFile(String, int, int)}.
     *      Deprecated in 2.0.1.
     */
    @Deprecated
    public void generatedFile(String fileName) {}

    /**
     * Called for each file generated by XJC.
     *
     * <p>
     * XJC may generate not only source files but also resources files.
     * The file name includes the path portions that correspond with the package name.
     *
     * <p>
     * When generating files into a directory, file names will be relative to the
     * output directory. When generating files into a zip file, file names will be
     * those in the zip file.
     *
     * @param fileName
     *      file names like "org/acme/foo/Foo.java" or "org/acme/foo/jaxb.properties".
     *
     * @since 2.0.1
     */
    public void generatedFile(String fileName, int current, int total ) {
        generatedFile(fileName);    // backward compatibility
    }

    /**
     * Other miscellenous messages that do not have structures
     * will be reported through this method.
     *
     * This method is used like {@link PrintStream#println(String)}.
     * The callee is expected to add '\n'. 
     */
    public void message(String msg) {}

    /**
     * Called after the schema is compiled and the code generation strategy is determined,
     * but before any code is actually generated as files.
     *
     * @param outline
     *      never null. this is the root object that represents the code generation strategy.
     */
    public void compiled(Outline outline) {}

    /**
     * XJC will periodically invoke this method to see if it should cancel a compilation.
     *
     * <p>
     * As long as this method returns false, XJC will keep going. If this method ever returns
     * true, XJC will abort the processing right away and
     * returns non-zero from {@link Driver#run(String[], XJCListener)}.
     * Note that XJC will not report an abortion through the {@link #message(String)} method.
     *
     * <p>
     * Note that despite all the efforts to check this method frequently, XJC may still fail to
     * invoke this method for a long time. Such scenario would include network related problems
     * or other I/O block (you can't even interrupt the thread while I/O is blocking.)
     * So just beware that this is not a cure-all. 
     *
     * @return
     *      true if the {@link XJCListener} wants to abort the processing.
     * @since 2.1
     */
    public boolean isCanceled() {
        return false;
    }
}
