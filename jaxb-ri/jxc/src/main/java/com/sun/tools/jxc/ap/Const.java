/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.ap;

import java.io.File;

/**
 * Defines constants used in the Annotation Processing driver.
 *
 * @author Kohsuke Kawaguchi
 */
public enum Const {

    /**
     * Name of the annotation processing command-line option to take user-specified config files.
     * <p>
     * <p>
     * It can take multiple file names separately by {@link File#pathSeparator}.
     */
    CONFIG_FILE_OPTION("jaxb.config"),

    DEBUG_OPTION("jaxb.debug");

    private String value;

    private Const(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
