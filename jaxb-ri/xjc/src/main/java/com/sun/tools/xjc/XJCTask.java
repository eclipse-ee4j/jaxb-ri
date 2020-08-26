/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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

    private String source = "3.0";

    /**
     * The version of the compiler to run
     */
    public void setSource(String version) {
        if ("3.0".equals(version)) {
            this.source = version;
            return;
        }
        throw new BuildException("Illegal version "+version);
    }


    protected ClassLoader createClassLoader() throws ClassNotFoundException, IOException {
        return SecureLoader.getClassClassLoader(XJCTask.class);
    }

    protected String getCoreClassName() {
        return "com.sun.tools.xjc.XJC2Task";
    }
}

