/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.addon.locator;

import java.io.IOException;

import jakarta.xml.bind.annotation.XmlTransient;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.glassfish.jaxb.core.Locatable;
import org.glassfish.jaxb.core.annotation.XmlLocation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;

/**
 * Generates JAXB objects that implement {@link Locatable}.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SourceLocationAddOn extends Plugin {

    public String getOptionName() {
        return "Xlocator";
    }

    public String getUsage() {
        return "  -Xlocator           :  enable source location support for generated code";
    }

    public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
        return 0;   // no option recognized
    }

    private static final String fieldName = "locator";

    public boolean run(
        Outline outline,
        Options opt,
        ErrorHandler errorHandler ) {
        
        for( ClassOutline ci : outline.getClasses() ) {
            JDefinedClass impl = ci.implClass;
            if (ci.getSuperClass() == null) {
                JVar $loc = impl.field(JMod.PROTECTED, Locator.class, fieldName);
                $loc.annotate(XmlLocation.class);
                $loc.annotate(XmlTransient.class);

                impl._implements(Locatable.class);

                impl.method(JMod.PUBLIC, Locator.class, "sourceLocation").body()._return($loc);
                
                JMethod setter = impl.method(JMod.PUBLIC, Void.TYPE, "setSourceLocation");
                JVar $newLoc = setter.param(Locator.class, "newLocator");
                setter.body().assign($loc, $newLoc);
            }
        }
        
        return true;
    }
}
