/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.ac;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationWriter;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Annotation compiler ant task.
 *
 * <p>
 * This task reads annotation classes and generate strongly-typed writers.
 *
 * @author Kohsuke Kawaguchi
 */
public class ACTask extends Task {

    /**
     * Used to load additional user-specified classes.
     */
    private final Path classpath;

    private final List<URL> endorsedJars = new ArrayList<URL>();

    private final List<Classes> patterns = new ArrayList<Classes>();

    /**
     * Used during the build to load annotation classes.
     */
    private ClassLoader userLoader;

    /**
     * Generated interfaces go into this codeModel.
     */
    private JCodeModel codeModel = new JCodeModel();

    /**
     * The writers will be generated into this package.
     */
    private JPackage pkg = codeModel.rootPackage();

    /**
     * Output directory
     */
    private File output = new File(".");

    /**
     * Map from annotation classes to their writers.
     */
    private final Map<Class, JDefinedClass> queue = new HashMap<Class, JDefinedClass>();

    public ACTask() {
        classpath = new Path(null);
    }

    @Override
    public void setProject(Project project) {
        super.setProject(project);
        classpath.setProject(project);
    }

    public void setPackage(String pkgName) {
        pkg = codeModel._package(pkgName);
    }

    /**
     * Nested {@code <classpath>} element.
     */
    public void setClasspath(Path cp) {
        classpath.createPath().append(cp);
    }

    /**
     * Nested {@code <classpath>} element.
     */
    public Path createClasspath() {
        return classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        classpath.createPath().setRefid(r);
    }

    public void setDestdir(File output) {
        this.output = output;
    }

    /**
     * Nested {@code <classes>} elements.
     */
    public static class Classes {

        Pattern include;
        Pattern exclude;

        public void setIncludes(String pattern) {
            try {
                include = Pattern.compile(convertToRegex(pattern));
            } catch (PatternSyntaxException e) {
                throw new BuildException(e);
            }
        }

        public void setExcludes(String pattern) {
            try {
                exclude = Pattern.compile(convertToRegex(pattern));
            } catch (PatternSyntaxException e) {
                throw new BuildException(e);
            }
        }

        private String convertToRegex(String pattern) {
            StringBuilder regex = new StringBuilder();
            char nc;
            if (pattern.length() > 0) {

                for (int i = 0; i < pattern.length(); i++) {
                    char c = pattern.charAt(i);
                    int j = i;
                    nc = ' ';
                    if ((j + 1) != pattern.length()) {
                        nc = pattern.charAt(j + 1);
                    }
                    //escape single '.'
                    if ((c == '.') && (nc != '.')) {
                        regex.append('\\');
                        regex.append('.');
                        //do not allow patterns like a..b
                    } else if ((c == '.') && (nc == '.')) {
                        continue;
                        // "**" gets replaced by ".*"
                    } else if ((c == '*') && (nc == '*')) {
                        regex.append(".*");
                        break;
                        //'*' replaced by anything but '.' i.e [^\\.]+
                    } else if (c == '*') {
                        regex.append("[^\\.]+");
                        continue;
                        //'?' replaced by anything but '.' i.e [^\\.]
                    } else if (c == '?') {
                        regex.append("[^\\.]");
                        //else leave the chars as they occur in the pattern
                    } else {
                        regex.append(c);
                    }
                }

            }

            return regex.toString();
        }
    }

    /**
     * List of classes to be handled
     */
    public void addConfiguredClasses(Classes c) {
        patterns.add(c);
    }

    /**
     * Nested {@code <endorse>} elements.
     */
    public static class Endorse {

        URL endorsedJar;

        public void setPath(String jar) throws MalformedURLException {
            endorsedJar = new File(jar).toURI().toURL();
        }
    }

    /**
     * List of endorsed jars
     */
    public void addConfiguredEndorse(Endorse e) {
        endorsedJars.add(e.endorsedJar);
    }

    @Override
    public void execute() throws BuildException {
        userLoader = new AntClassLoader(project, classpath);
        try {
            // find clsses to be bound
            for (String path : classpath.list()) {
                File f = new File(path);
                if (f.isDirectory()) {
                    processDir(f, "");
                } else {
                    processJar(f);
                }
            }

            for (Map.Entry<Class, JDefinedClass> e : queue.entrySet()) {
                Class ann = e.getKey();
                JDefinedClass w = e.getValue();

                w._implements(codeModel.ref(JAnnotationWriter.class).narrow(ann));

                for (Method m : ann.getDeclaredMethods()) {
                    Class rt = m.getReturnType();

                    if (rt.isArray()) // array writers aren't distinguishable from scalar writers
                    {
                        rt = rt.getComponentType();
                    }

                    if (Annotation.class.isAssignableFrom(rt)) {
                        // annotation type
                        JDefinedClass at = queue.get(rt);
                        if (at == null) {
                            log(rt + " is not part of this compilation. ignored.", Project.MSG_INFO);
                            continue;
                        }
                        w.method(0, at, m.getName());
                    } else {
                        // other primitives
                        w.method(0, w, m.getName()).param(rt, "value");
                        if (rt == Class.class) {
                            // for Class, give it another version that takes JType
                            w.method(0, w, m.getName()).param(JType.class, "value");
                        }
                    }
                }
            }

            try {
                codeModel.build(output);
            } catch (IOException e) {
                throw new BuildException("Unable to queue code to " + output, e);
            }
        } finally {
            if (userLoader instanceof Closeable) {
                try {
                    ((Closeable) userLoader).close();
                } catch (IOException ioe) {
                    //ignore
                }
            }
            userLoader = null;
        }
    }

    /**
     * Visits a jar fil and looks for classes that match the specified pattern.
     */
    private void processJar(File jarfile) {
        try {
            JarFile jar = new JarFile(jarfile);
            for (Enumeration<JarEntry> en = jar.entries(); en.hasMoreElements();) {
                JarEntry e = en.nextElement();
                process(e.getName(), e.getTime());
            }
        } catch (IOException e) {
            throw new BuildException("Unable to process " + jarfile, e);
        }
    }

    /**
     * Visits a directory and looks for classes that match the specified
     * pattern.
     *
     * @param prefix the package name prefix like "" or "foo/bar/"
     */
    private void processDir(File dir, String prefix) {
        // look for class files
        String[] classes = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        });

        for (String c : classes) {
            process(prefix + c, new File(dir, c).lastModified());
        }

        // look for subdirectories
        File[] subdirs = dir.listFiles(new FileFilter() {
            public boolean accept(File path) {
                return path.isDirectory();
            }
        });
        for (File f : subdirs) {
            processDir(f, prefix + f.getName() + '/');
        }
    }

    /**
     * Process a file.
     *
     * @param name such as "javax/xml/bind/Abc.class"
     */
    private void process(String name, long timestamp) {
        if (!name.endsWith(".class")) {
            return; // not a class
        }
        name = name.substring(0, name.length() - 6);
        name = name.replace('/', '.'); // make it a class naem
        // find a match
        for (Classes c : patterns) {
            if (c.include.matcher(name).matches()) {
                if (c.exclude != null && c.exclude.matcher(name).matches()) {
                    continue;
                }

                queue(name, timestamp);
                return;
            }
        }
    }

    /**
     * Queues a file for generation.
     */
    private void queue(String className, long timestamp) {
        log("Processing " + className, Project.MSG_VERBOSE);
        Class ann;
        try {
            ann = userLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new BuildException(e);
        }

        if (!Annotation.class.isAssignableFrom(ann)) {
            log("Skipping " + className + ". Not an annotation", Project.MSG_WARN);
            return;
        }

        JDefinedClass w;
        try {
            w = pkg._class(JMod.PUBLIC, getShortName(className) + "Writer", ClassType.INTERFACE);
        } catch (JClassAlreadyExistsException e) {
            throw new BuildException("Class name collision on " + className, e);
        }

        // up to date check
        String name = pkg.name();
        if (name.length() == 0) {
            name = getShortName(className);
        } else {
            name += '.' + getShortName(className);
        }

        File dst = new File(output, name.replace('.', File.separatorChar) + "Writer.java");
        if (dst.exists() && dst.lastModified() > timestamp) {
            log("Skipping " + className + ". Up to date.", Project.MSG_VERBOSE);
            w.hide();
        }

        queue.put(ann, w);
    }

    /**
     * Gets the short name from a fully-qualified name.
     */
    private static String getShortName(String className) {
        int idx = className.lastIndexOf('.');
        if (idx < 0) {
            return className;
        } else {
            return className.substring(idx + 1);
        }
    }
}
