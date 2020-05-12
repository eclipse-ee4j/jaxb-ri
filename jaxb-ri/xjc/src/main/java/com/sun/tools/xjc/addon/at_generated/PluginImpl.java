/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.addon.at_generated;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Driver;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;
import java.io.IOException;

import org.xml.sax.ErrorHandler;

/**
 * {@link Plugin} that marks the generated code by using JSR-250's '@Generated'.
 *
 * @author Kohsuke Kawaguchi
 * @author Lukas Jungmann
 */
public class PluginImpl extends Plugin {

    @Override
    public String getOptionName() {
        return "mark-generated";
    }

    @Override
    public String getUsage() {
        return "  -mark-generated     :  mark the generated code as @jakarta.annotation.Generated\n"
             + "                      -noDate            : do not add date\n"
             + "                      -Xann <annotation> : generate <annotation> instead of @jakarta.annotation.Generated";
    }

    @Override
    public int parseArgument( Options opt, String[] args, int i ) throws BadCommandLineException, IOException {
        if ("-noDate".equals(args[i])) {
            noDate = true;
            return 1;
        }
        if ("-Xann".equals(args[i])) {
            genAnnotation = opt.requireArgument("-Xann", args, ++i);
            return 2;
        }
        return 0;
    }

    private JClass annotation;
    private boolean noDate = false;
    private String genAnnotation = "jakarta.annotation.Generated";

    @Override
    public boolean run( Outline model, Options opt, ErrorHandler errorHandler ) {
        // we want this to work without requiring Jakarta annotations jar.
        annotation = model.getCodeModel().ref(genAnnotation);

        for (ClassOutline co : model.getClasses()) {
            augument(co);
        }
        for (EnumOutline eo : model.getEnums()) {
            augument(eo);
        }
        for (PackageOutline po : model.getAllPackageContexts()) {
            augument(po);
        }
        return true;
    }

    private void augument(EnumOutline eo) {
        annotate(eo.clazz);
    }

    /**
     * Adds "@Generated" to the classes, methods, and fields.
     */
    private void augument(ClassOutline co) {
        annotate(co.implClass);
        for (JMethod m : co.implClass.methods())
            annotate(m);
        for (JFieldVar f : co.implClass.fields().values())
            annotate(f);
    }

    private void augument(PackageOutline po) {
        annotate(po.objectFactory());
    }

    private void annotate(JAnnotatable m) {
        JAnnotationUse au = m.annotate(annotation)
                .param("value",Driver.class.getName())
                .param("comments", "JAXB RI v" + Options.getBuildID());
        if (!noDate) {
                au.param("date", getISO8601Date());
        }
    }

    // cache the timestamp so that all the @Generated annotations match
    private String date = null;

    /**
     * calculate the date value in ISO8601 format for the @Generated annotation
     * @return the date value
     */
    private String getISO8601Date() {
        if(date==null) {
            StringBuffer tstamp = new StringBuffer();
            tstamp.append((new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ")).format(new Date()));
            // hack to get ISO 8601 style timezone - is there a better way that doesn't require
            // a bunch of timezone offset calculations?
            tstamp.insert(tstamp.length()-2, ':');
            date = tstamp.toString();
        }
        return date;
    }
}
