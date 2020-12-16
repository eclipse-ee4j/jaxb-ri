/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.addon.javax;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.sun.codemodel.JFormatter;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.api.SpecVersion;
import com.sun.tools.xjc.outline.Outline;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * {@link Plugin} that generate sources with javax.xml.bind
 * packages.
 *
 * @author jbescos
 */
public class PluginImpl extends Plugin {

    private static final String XML_REGISTRY_JAVAX = "javax.xml.bind.annotation.XmlRegistry";
    private String javaxLibPath;

    @Override
    public String getOptionName() {
        return "generateJavax";
    }

    @Override
    public String getUsage() {
        return "  -generateJavax         :  Replaces the generated jakarta.xml.bind by javax.xml.bind.";
    }

    @Override
    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) throws SAXException {
        SpecVersion target = options.target;
        boolean isJavax = isJavax();
        if (!isJavax && target.ordinal() < SpecVersion.V3_0.ordinal()) {
            errorHandler.warning(new SAXParseException("Jakarta does not support version 2.x version ", null));
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    System.setProperty(JFormatter.XML_CONVERSION_PROP, Boolean.TRUE.toString());
                    return null;
                }
            });
        } else if (isJavax && target.ordinal() >= SpecVersion.V3_0.ordinal()) {
            errorHandler.warning(new SAXParseException("Javax does not support version 3.x version ", null));
        }
        return true;
    }

    private boolean isJavax() {
        try {
            Class.forName(XML_REGISTRY_JAVAX);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}
