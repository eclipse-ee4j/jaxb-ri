/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools;

import com.sun.codemodel.CodeWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JArray;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.istack.tools.MaskingClassLoader;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.Quick;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

/**
 * Ant task that generates implementations of {@link Quick} classes
 * for annotations.
 *
 * @author Kohsuke Kawaguchi
 */
public class GeneratorTask extends Task {

    /**
     * Used to load additional user-specified classes.
     */
    private final Path classpath;
    private final List<Pattern> patterns = new ArrayList<Pattern>();
    private final List<URL> endorsedJars = new ArrayList<URL>();
    
    private File licence = null;
    
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
     * Generate a {@link Quick} implementation of the specified annotation
     * in the specified package.
     *
     * @param ann
     *      annotation type to
     */
    void process(Class<? extends Annotation> ann, JDefinedClass c) {
        // [RESULT]
        // public final class XmlAttributeQuick extends Quick implements XmlAttribute {
        c._extends(Quick.class);
        c._implements(ann);

        // [RESULT]
        //     private final XmlAttribute core;
        JFieldVar $core = c.field(JMod.PRIVATE | JMod.FINAL, ann, "core");

        // [RESULT]
        // public XmlAttributeQuick(Locatable upstream, XmlAttribute core) {
        //     super(upstream);
        //     this.core = core;
        // }
        {
            JMethod m = c.constructor(JMod.PUBLIC);
            m.body().invoke("super").arg(m.param(Locatable.class, "upstream"));
            m.body().assign(JExpr._this().ref($core), m.param(ann, "core"));
        }

        // [RESULT]
        // protected Annotation getAnnotation() {
        //     return core;
        // }
        {
            JMethod m = c.method(JMod.PROTECTED, Annotation.class, "getAnnotation");
            m.body()._return($core);
        }

        // [RESULT]
        // protected Quick newInstance(Locatable upstream,Annotation core) {
        //     return new XmlAttributeQuick(upstream,(XmlAttribute)core);
        // }
        {
            JMethod m = c.method(JMod.PROTECTED, Quick.class, "newInstance");
            m.body()._return(JExpr._new(c).arg(m.param(Locatable.class, "upstream")).arg(JExpr.cast(codeModel.ref(ann), m.param(Annotation.class, "core"))));
        }

        // [RESULT]
        // public Class<XmlAttribute> annotationType() {
        //     return XmlAttribute.class;
        // }
        {
            JMethod m = c.method(JMod.PUBLIC,
                    codeModel.ref(Class.class).narrow(ann), "annotationType");
            m.body()._return(codeModel.ref(ann).dotclass());
        }


        // then for each annotation parameter just generate a delegation method
        for (Method method : ann.getDeclaredMethods()) {
            // [RESULT]
            // public String name() {
            //    return core.name();
            //}
            JMethod m = c.method(JMod.PUBLIC, method.getReturnType(), method.getName());
            m.body()._return($core.invoke(method.getName()));
        }
    }

    /**
     * Gets the short name from a fully-qualified name.
     */
    private static String getShortName(String className) {
        int idx = className.lastIndexOf('.');
        return className.substring(idx + 1);
    }
    /**
     * Map from annotation classes to their writers.
     */
    private final Map<Class, JDefinedClass> queue =
            new TreeMap<Class, JDefinedClass>(new Comparator<Object>() {

        public int compare(Object o1, Object o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            if (!(o1 instanceof Class) || !(o2 instanceof Class)) {
                throw new ClassCastException();
            }
            return ((Class) o1).getName().compareTo(((Class) o2).getName());
        }
    });

    public GeneratorTask() {
        classpath = new Path(null);
    }

    public void setLicence(File licence) {
        this.licence = licence;
    }
    
    @Override
    public void setProject(Project project) {
        super.setProject(project);
        classpath.setProject(project);
    }

    public void setPackage(String pkgName) {
        pkg = codeModel._package(pkgName);
    }

    /** Nested &lt;classpath> element. */
    public void setClasspath(Path cp) {
        classpath.createPath().append(cp);
    }

    /** Nested &lt;classpath> element. */
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
     * Nested &lt;classes> elements.
     */
    public static class Classes {

        Pattern p;

        public void setIncludes(String pattern) {
            try {
                p = Pattern.compile(convertToRegex(pattern));
            } catch (PatternSyntaxException e) {
                throw new BuildException(e);
            }
        }

        private String convertToRegex(String pattern) {
            StringBuilder regex = new StringBuilder();
            char nc = ' ';
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
     * Nested &lt;endorse> elements.
     */
    public static class Endorse {

        URL endorsedJar;

        public void setPath(String jar) throws MalformedURLException {
            endorsedJar = new File(jar).toURI().toURL();
        }
    }

    /**
     * List of classes to be handled
     */
    public void addConfiguredClasses(Classes c) {
        patterns.add(c.p);
    }

    /**
     * List of endorsed jars
     */
    public void addConfiguredEndorse(Endorse e) {
        endorsedJars.add(e.endorsedJar);
    }

    @Override
    public void execute() throws BuildException {
        if ((endorsedJars != null) && (!endorsedJars.isEmpty())) {
            ClassLoader maskedLoader = new MaskingClassLoader(new AntClassLoader(project, classpath), "javax.xml.bind");
            URL[] jars = new URL[endorsedJars.size()];
            userLoader = new URLClassLoader(endorsedJars.toArray(jars), maskedLoader);
        } else {
            userLoader = new AntClassLoader(project, classpath);
        }
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

            // [RESULT]
            // class Init {
            //    static Quick[] getAll() {
            //       ... return all Quick classes ...
            //    }
            // }
            try {
                JArray all = JExpr.newArray(codeModel.ref(Quick.class));
                JDefinedClass init = pkg._class(0, "Init");
                init.method(JMod.STATIC, Quick[].class, "getAll").body()._return(all);


                for (Map.Entry<Class, JDefinedClass> e : queue.entrySet()) {
                    process(e.getKey(), e.getValue());
                    all.add(JExpr._new(e.getValue()).arg(JExpr._null()).arg(JExpr._null()));
                }
            } catch (JClassAlreadyExistsException e) {
                throw new BuildException(e);
            }

            CodeWriter fcw = new FileCodeWriter(output);
            if (licence != null) {
                String licenceHeader = null;
                LineNumberReader lnr = null;
                FileReader fr = null;
                try {

                    fr = new FileReader(licence);
                    lnr = new LineNumberReader(fr);

                    licenceHeader = lnr.readLine();
                    do {
                        licenceHeader += "\n";
                        String line = lnr.readLine();
                        if (line == null) {
                            break;
                        } else {
                            licenceHeader += line;
                        }
                    } while (true);
                } catch (IOException ex) {
                    if (lnr != null) {
                        try {
                            lnr.close();
                        } catch (IOException ex1) {
                            Logger.getLogger(GeneratorTask.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    if (fr != null) {
                        try {
                            fr.close();
                        } catch (IOException ex1) {
                            Logger.getLogger(GeneratorTask.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    Logger.getLogger(GeneratorTask.class.getName()).log(Level.SEVERE, null, ex);
                }
                fcw = new LicenceCodeWriter(fcw, licenceHeader);
            } 
            codeModel.build(fcw);
        } catch (IOException ex) {
            Logger.getLogger(GeneratorTask.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            userLoader = null;
        }
    }

    /**
     * Visits a jar file and looks for classes that match the specified pattern.
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
     * Visits a directory and looks for classes that match the specified pattern.
     *
     * @param prefix
     *      the package name prefix like "" or "foo/bar/"
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
        for (Pattern p : patterns) {
            if (p.matcher(name).matches()) {
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
            w = pkg._class(JMod.FINAL, getShortName(ann.getName()) + "Quick");
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

        File dst = new File(output, name.replace('.', File.separatorChar) + "Quick.java");
        if (dst.exists() && dst.lastModified() > timestamp) {
            log("Skipping " + className + ". Up to date.", Project.MSG_VERBOSE);
            w.hide();
        }

        queue.put(ann, w);
    }
}
