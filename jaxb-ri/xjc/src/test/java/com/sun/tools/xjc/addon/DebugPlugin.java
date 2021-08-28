/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.addon;

import java.util.Arrays;
import java.util.List;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.Outline;

import org.xml.sax.ErrorHandler;

/**
 * @author Kohsuke Kawaguchi
 */
public class DebugPlugin extends Plugin {
    @Override
    public String getOptionName() {
        return "Xdebug";
    }

    @Override
    public String getUsage() {
        return "  -Xdebug            :  test various plug-in functionalities";
    }

    @Override
    public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
        return true;
    }

    @Override
    public List<String> getCustomizationURIs() {
        return Arrays.asList("http://jaxb.dev.java.net/test");
    }

    @Override
    public boolean isCustomizationTagName(String nsUri, String localName) {
        return true;
    }
}
