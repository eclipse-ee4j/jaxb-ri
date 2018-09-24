/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import java.io.IOException;
import com.sun.istack.tools.ProtectedTask;
import org.apache.tools.ant.BuildException;

/**
 * Captures the properties and then delegate to XJC1 or XJC2 by looking at
 * the source attribute.
 *
 * @author Bhakti Mehta
 */
public class XJCTask extends ProtectedTask {

    private String source = "2.0";

    /**
     * The version of the compiler to run
     */
    public void setSource(String version) {
        if (version.equals("2.0")) {
            this.source = version;
            return;
        }
        throw new BuildException("Illegal version "+version);
    }


    protected ClassLoader createClassLoader() throws ClassNotFoundException, IOException {
        return ClassLoaderBuilder.createProtectiveClassLoader(SecureLoader.getClassClassLoader(XJCTask.class), source);
    }

    protected String getCoreClassName() {
        return "com.sun.tools.xjc.XJC2Task";
    }
}

