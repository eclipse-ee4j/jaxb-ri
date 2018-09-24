/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

/**
 * XJC task for Ant.
 *
 * See the accompanied document for the usage.
 *
 * @author Yan Gao (gaoyan.gao@oracle.com)
 */
public class XJC2Task extends XJCBase {

    @Override
    public void execute() throws BuildException {
        super.execute();
    }

    @Override
    protected void setupForkCommand(String className) {
        ClassLoader loader = this.getClass().getClassLoader();
        while (loader != null && !(loader instanceof AntClassLoader)) {
            loader = loader.getParent();
        }

        String antcp = loader != null
            //taskedef cp
            ? ((AntClassLoader) loader).getClasspath()
            //system classloader, ie. env CLASSPATH=...
            : System.getProperty("java.class.path");

        getCommandline().createClasspath(getProject()).append(new Path(getProject(), antcp));

        if (getModulepath() != null && getModulepath().size() > 0) {
            getCommandline().createModulepath(getProject()).add(getModulepath());
        }
        if (getUpgrademodulepath() != null && getUpgrademodulepath().size() > 0) {
            getCommandline().createUpgrademodulepath(getProject()).add(getUpgrademodulepath());
        }
        if (getAddmodules() != null && getAddmodules().length() > 0) {
            getCommandline().createVmArgument().setLine("--add-modules " + getAddmodules());
        }
        if (getAddreads() != null && getAddreads().length() > 0) {
            getCommandline().createVmArgument().setLine("--add-reads " + getAddreads());
        }
        if (getAddexports() != null && getAddexports().length() > 0) {
            getCommandline().createVmArgument().setLine("--add-exports " + getAddexports());
        }
        if (getAddopens() != null && getAddopens().length() > 0) {
            getCommandline().createVmArgument().setLine("--add-opens " + getAddopens());
        }
        if (getPatchmodule() != null && getPatchmodule().length() > 0) {
            getCommandline().createVmArgument().setLine("--patch-module " + getPatchmodule());
        }
        if (getLimitmodules() != null && getLimitmodules().length() > 0) {
            getCommandline().createVmArgument().setLine("--limit-modules " + getLimitmodules());
        }

        getCommandline().setClassname(className);
    }
}
