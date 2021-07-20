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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;


/**
 * Variables and fields.
 */

public class JVar extends JExpressionImpl implements JDeclaration, JAssignmentTarget, JAnnotatable {

    /**
     * Modifiers.
     */
    private JMods mods;

    /**
     * JType of the variable
     */
    private JType type;

    /**
     * Name of the variable
     */
    private String name;

    /**
     * Initialization of the variable in its declaration
     */
    private JExpression init;

    /**
     * Annotations on this variable. Lazily created.
     */
    private List<JAnnotationUse> annotations = null;



    /**
     * JVar constructor
     *
     * @param type
     *        Datatype of this variable
     *
     * @param name
     *        Name of this variable
     *
     * @param init
     *        Value to initialize this variable to
     */
    JVar(JMods mods, JType type, String name, JExpression init) {
        this.mods = mods;
        this.type = type;
        this.name = name;
        this.init = init;
    }


    /**
     * Initialize this variable
     *
     * @param init
     *        JExpression to be used to initialize this field
     */
    public JVar init(JExpression init) {
        this.init = init;
        return this;
    }

    /**
     * Get the name of this variable
     *
     * @return Name of the variable
     */
    public String name() {
        return name;
    }

    /**
     * Changes the name of this variable.
     */
    public void name(String name) {
        if(!JJavaName.isJavaIdentifier(name))
            throw new IllegalArgumentException();
        this.name = name;
    }

    /**
     * Return the type of this variable.
     * @return
     *      always non-null.
     */
    public JType type() {
        return type;
    }

    /**
     * @return
     *      the current modifiers of this method.
     *      Always return non-null valid object.
     */
    public JMods mods() {
        return mods;
    }

    /**
     * Sets the type of this variable.
     *
     * @param newType
     *      must not be null.
     *
     * @return
     *      the old type value. always non-null.
     */
    public JType type(JType newType) {
        JType r = type;
        if(newType==null)
            throw new IllegalArgumentException();
        type = newType;
        return r;
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

    @Override
    public Collection<JAnnotationUse> annotations() {
        if (annotations == null)
            annotations = new ArrayList<>();
        return Collections.unmodifiableList(annotations);
    }

    protected boolean isAnnotated() {
        return annotations!=null;
    }

    public void bind(JFormatter f) {
        if (annotations != null){
            for( int i=0; i<annotations.size(); i++ )
                f.g(annotations.get(i)).nl();
        }
        f.g(mods).g(type).id(name);
        if (init != null)
            f.p('=').g(init);
    }

    @Override
    public void declare(JFormatter f) {
        f.b(this).p(';').nl();
    }

    @Override
    public void generate(JFormatter f) {
        f.id(name);
    }

	
    @Override
    public JExpression assign(JExpression rhs) {
		return JExpr.assign(this,rhs);
    }
    @Override
    public JExpression assignPlus(JExpression rhs) {
		return JExpr.assignPlus(this,rhs);
    }
	
}
