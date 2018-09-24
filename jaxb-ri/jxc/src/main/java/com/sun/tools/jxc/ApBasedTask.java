/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
import org.apache.tools.ant.types.Commandline;

import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

/**
 * Base class for tasks that eventually invoke annotation processing.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ApBasedTask extends Javac {

    /**
     * Implemented by the derived class to set up command line switches passed to annotation processing.
     */
    protected abstract void setupCommandlineSwitches(Commandline cmd);

    private abstract class ApAdapter extends DefaultCompilerAdapter {
        protected ApAdapter() {
            setJavac(ApBasedTask.this);
        }

        protected Commandline setupModernJavacCommandlineSwitches(Commandline cmd) {
            super.setupModernJavacCommandlineSwitches(cmd);
            setupCommandlineSwitches(cmd);
            return cmd;
        }

        protected void logAndAddFilesToCompile(Commandline cmd) {
            attributes.log("Compilation " + cmd.describeArguments(),
                           Project.MSG_VERBOSE);

            StringBuilder niceSourceList = new StringBuilder("File");
            if (compileList.length != 1) {
                niceSourceList.append("s");
            }
            niceSourceList.append(" to be compiled:");

            niceSourceList.append(lSep);

            StringBuilder tempbuilder = new StringBuilder();
            for (File aCompileList : compileList) {
                String arg = aCompileList.getAbsolutePath();
                // cmd.createArgument().setValue(arg); --> we don't need compile list withing cmd arguments
                tempbuilder.append("    ").append(arg).append(lSep);
                niceSourceList.append(tempbuilder);
                tempbuilder.setLength(0);
            }

            attributes.log(niceSourceList.toString(), Project.MSG_VERBOSE);
        }
    }

    /**
     * Adapter to invoke Ap internally.
     */
    private final class InternalApAdapter extends ApAdapter {

        public boolean execute() throws BuildException {
            try {
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                DiagnosticCollector diagnostics = new DiagnosticCollector();
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
                Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(compileList));
                JavaCompiler.CompilationTask task = compiler.getTask(
                        null,
                        fileManager,
                        diagnostics,
                        Arrays.asList(setupModernJavacCommand().getArguments()),
                        null,
                        compilationUnits);
                task.setProcessors(Collections.singleton(getProcessor()));
                return task.call();
            } catch (BuildException e) {
                throw e;
            } catch (Exception ex) {
                throw new BuildException("Error starting ap", ex, location);
            }
        }
    }

    /**
     * Creates a factory that does the actual job.
     */
    protected abstract Processor getProcessor();

//    /**
//     * Adapter to invoke annotation processing externally.
//     */
//    private final class ExternalAptAdapter extends AptAdapter {
//        public boolean execute() throws BuildException {
//            Commandline cmd = setupModernJavacCommand();
//            return executeExternalCompile(cmd.getArguments(),-1)==0;
//        }
//    }

    protected void compile() {
        if (compileList.length == 0) return;

        log(getCompilationMessage() + compileList.length + " source file"
                + (compileList.length == 1 ? "" : "s"));

        if (listFiles) {
            for (File aCompileList : compileList) {
                String filename = aCompileList.getAbsolutePath();
                log(filename);
            }
        }

        ApAdapter ap = new InternalApAdapter();
//        if(isForkedJavac())
//            ap = new ExternalApAdapter();
//        else

        // compile
        if (!ap.execute()) {
            if (failOnError) {
                throw new BuildException(getFailedMessage(), getLocation());
            } else {
                log(getFailedMessage(), Project.MSG_ERR);
            }
        }
    }

    protected abstract String getCompilationMessage();
    protected abstract String getFailedMessage();
}
