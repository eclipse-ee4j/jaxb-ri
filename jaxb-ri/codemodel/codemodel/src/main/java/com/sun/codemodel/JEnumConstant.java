/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.lang.annotation.Annotation;

/**
 * Enum Constant.
 *
 * When used as an {@link JExpression}, this object represents a reference to the enum constant.
 *
 * @author
 *     Bhakti Mehta (Bhakti.Mehta@sun.com)
 */
public final class JEnumConstant extends JExpressionImpl implements JDeclaration, JAnnotatable, JDocCommentable {

    /**
     * The constant.
     */
    private final String name;
    /**
     * The enum class.
     */
    private final JClass type;
    /**
     * javadoc comments, if any.
     */
    private JDocComment jdoc = null;

    /**
     * Annotations on this variable. Lazily created.
     */
    private List<JAnnotationUse> annotations = null;


    /**
     * List of the constructor argument expressions.
     * Lazily constructed.
     */
    private List<JExpression> args = null;

    JEnumConstant(JClass type,String name) {
        this.name = name;
        this.type = type;
    }

    /**
     *  Add an expression to this constructor's argument list
     *
     * @param arg
     *        Argument to add to argument list
     */
    public JEnumConstant arg(JExpression arg) {
        if(arg==null)   throw new IllegalArgumentException();
        if(args==null)
            args = new ArrayList<>();
        args.add(arg);
        return this;
    }

    /**
     * Returns the name of this constant.
     *
     * @return never null.
     */
    public String getName() {
    	return this.type.fullName().concat(".").concat(this.name);
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Creates, if necessary, and returns the enum constant javadoc.
     *
     * @return JDocComment containing javadocs for this constant.
     */
    @Override
    public JDocComment javadoc() {
        if (jdoc == null)
            jdoc = new JDocComment(type.owner());
        return jdoc;
    }

    /**
     * Adds an annotation to this variable.
     * @param clazz
     *          The annotation class to annotate the field with
     */
    @Override
    public JAnnotationUse annotate(JClass clazz){
        if(annotations==null)
           annotations = new ArrayList<>();
        JAnnotationUse a = new JAnnotationUse(clazz);
        annotations.add(a);
        return a;
    }

    /**
     * Adds an annotation to this variable.
     *
     * @param clazz
     *          The annotation class to annotate the field with
     */
    @Override
    public JAnnotationUse annotate(Class <? extends Annotation> clazz){
        return annotate(type.owner().ref(clazz));
    }

    @Override
    public <W extends JAnnotationWriter<? extends Annotation>> W annotate2(Class<W> clazz) {
        return TypedAnnotationWriter.create(clazz,this);
    }

    @Override
    public boolean removeAnnotation(JAnnotationUse annotation) {
        return this.annotations.remove(annotation);
    }
    /**
     * {@link JAnnotatable#annotations()}
     */
    @Override
    public Collection<JAnnotationUse> annotations() {
        if (annotations == null)
            annotations = new ArrayList<>();
        return Collections.unmodifiableList(annotations);
    }

    @Override
    public void declare(JFormatter f) {
        if( jdoc != null )
            f.nl().g( jdoc );
        if (annotations != null) {
            for( int i=0; i<annotations.size(); i++ )
                f.g(annotations.get(i)).nl();
        }
        f.id(name);
        if(args!=null) {
            f.p('(').g(args).p(')');
        }
    }

    @Override
    public void generate(JFormatter f) {
    	f.t(type).p('.').p(name);
    }

    JClass type() {
        return type;
    }
}
