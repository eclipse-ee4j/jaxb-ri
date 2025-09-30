/*
 * Copyright (c) 2016, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

// Based on modules grammar from http://openjdk.java.net/projects/jigsaw/doc/lang-vm.html

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents a Java module.
 * @author Tomas Kraus
 */
public class JModule implements JGenerable, JAnnotatable, JDocCommentable{

    /** Java module file name. */
    private static final String FILE_NAME = "module-info.java";

    /** Name of this module. Mandatory value. Shall not be {@code null}. */
    private final String name;
    private final JCodeModel owner;

    /** {@link Set} of Java module directives. */
    private final Set<JModuleDirective> directives;

    /**
     * Annotations on this JModule. Lazily created.
     */
    private List<JAnnotationUse> annotations = null;

    /**
     * javadoc comments for this JModule
     */
    private JDocComment jdoc = null;

    /**
     * Creates an instance of Java module.
     *
     * @param name       Java module name. Value can not be {@code null}
     * @deprecated use two args constructor
     * @since 4.0.7
     */
    @Deprecated(forRemoval = true, since = "4.0.7")
    JModule(final String name) {
        this(name, new JCodeModel());
    }

    /**
     * Creates an instance of Java module.
     *
     * @param name       Java module name. Value can not be {@code null}
     * @param owner      {@link JCodeModel} instance that owns this module.
     */
    JModule(final String name, JCodeModel owner) {
        if (name == null) {
            throw new IllegalArgumentException("Value of name is null");
        }
        this.name = name;
        this.owner = owner;
        this.directives = new HashSet<>();
    }

    /**
     * Gets the name of this module.
     * @return name of this module.
     */
    public String name() {
        return name;
    }

    /**
     * Gets module directives set.
     * jUnit helper method.
     * @return Module directives set.
     */
    Set<JModuleDirective> getDirectives() {
        return directives;
    }

    /**
     * Adds a package to the list of Java module exports.
     * The package name shall not be {@code null} or empty {@code String}.
     * @param pkg Java package to be exported.
     */
    public void _exports(final JPackage pkg) {
        directives.add(new JExportsDirective(pkg.name()));
    }

    /**
     * Adds packages to the list of Java module exports.
     * @param pkgs Collection of packages to be added.
     * @param addEmpty Adds also packages without any classes when {@code true}.
     */
    public void _exports(final Collection<JPackage> pkgs, final boolean addEmpty) {
        for (Iterator<JPackage> i = pkgs.iterator(); i.hasNext();) {
            final JPackage pkg = i.next();
            if (addEmpty || pkg.hasClasses()) {
                _exports(pkg);
            }
        }
    }

    /**
     * Adds a module to the list of Java module requirements.
     * The module name shall not be {@code null} or empty {@code String}.
     * @param name Name of required Java module.
     * @param isPublic Use {@code public} modifier.
     * @param isStatic Use {@code static} modifier.
     */
    public void _requires(final String name, final boolean isPublic, final boolean isStatic) {
        directives.add(new JRequiresDirective(name, isPublic, isStatic));
    }

    /**
     * Adds a module to the list of Java module requirements without {@code public} and {@code static} modifiers.
     * The module name shall not be {@code null} or empty {@code String}.
     * @param name Name of required Java module.
     */
    public void _requires(final String name) {
        directives.add(new JRequiresDirective(name, false, false));
    }

    /**
     * Adds all modules to the list of Java module requirements.
     * The module name shall not be {@code null} or empty {@code String}.
     * @param names Names of required Java module.
     * @param isPublic Use {@code public} modifier.
     * @param isStatic Use {@code static} modifier.
     */
    public void _requires(final boolean isPublic, final boolean isStatic, final String ...names) {
        if (names != null) {
            for (final String reqName : names) {
                _requires(reqName, isPublic, isStatic);
            }
        }
    }

    /**
     * Adds all modules to the list of Java module requirements without {@code public} and {@code static} modifiers.
     * @param names Names of required Java module.
     */
    public void _requires(final String ...names) {
        _requires(false, false, names);
    }

    @Override
    public JAnnotationUse annotate(JClass clazz) {
        if (annotations == null) {
            annotations = new ArrayList<>();
        }
        JAnnotationUse a = new JAnnotationUse(clazz);
        annotations.add(a);
        return a;
    }

    @Override
    public JAnnotationUse annotate(Class<? extends Annotation> clazz) {
        return annotate(owner.ref(clazz));
    }

    @Override
    public boolean removeAnnotation(JAnnotationUse annotation) {
        return this.annotations.remove(annotation);
    }

    @Override
    public Collection<JAnnotationUse> annotations() {
        if (annotations == null) {
            annotations = new ArrayList<>();
        }
        return Collections.unmodifiableList(annotations);
    }

    @Override
    public <W extends JAnnotationWriter<? extends Annotation>> W annotate2(Class<W> clazz) {
        return TypedAnnotationWriter.create(clazz, this);
    }

    /**
     * Creates, if necessary, and returns the class javadoc for this
     * JModule
     *
     * @return JDocComment containing javadocs for this class
     */
    @Override
    public JDocComment javadoc() {
        if (jdoc == null) {
            jdoc = new JDocComment(owner);
        }
        return jdoc;
    }

    /**
     * Print source code of Java Module declaration.
     * @param f Java code formatter.
     */
    public void generate(final JFormatter f) {
        if (jdoc != null) {
            f.g(jdoc);
        }

        if (annotations != null) {
            for (JAnnotationUse a : annotations) {
                f.g(a).nl();
            }
        }

        f.p("module").p(name);
        f.p('{').nl();
        if (!directives.isEmpty()) {
            f.i();
            for (final JModuleDirective directive : directives) {
                directive.generate(f);
            }
            f.o();
        }
        f.p('}').nl();
    }

    /**
     * Create {@code module-info.java} source writer.
     * @return New instance of {@code module-info.java} source writer.
     */
    private JFormatter createModuleInfoSourceFileWriter(final CodeWriter src) throws IOException {
        Writer bw = new BufferedWriter(src.openSource(null, FILE_NAME));
        return new JFormatter(new PrintWriter(bw));
    }

    /**
     * Build {@code module-info.java} source file.
     * @param src Source code writer.
     * @throws IOException if there is any problem with writing the file.
     */
    void build(final CodeWriter src) throws IOException {
        final JFormatter f = createModuleInfoSourceFileWriter(src);
        generate(f);
        f.close();
    }

}
