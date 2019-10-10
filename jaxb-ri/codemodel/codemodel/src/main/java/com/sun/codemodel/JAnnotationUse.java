/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;


import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an annotation on a program element.
 *
 * TODO
 *    How to add enums to the annotations
 * @author
 *     Bhakti Mehta (bhakti.mehta@sun.com)
 */
public final class JAnnotationUse extends JAnnotationValue {

    /**
     * The {@link Annotation} class
     */
    private final JClass clazz;

    /**
     * Map of member values.
     */
    private Map<String,JAnnotationValue> memberValues;

    JAnnotationUse(JClass clazz){
        this.clazz = clazz;
    }

    public JClass getAnnotationClass() {
        return clazz;
    }

    public Map<String, JAnnotationValue> getAnnotationMembers() {
        if (memberValues == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(memberValues);
    }
    
    private JCodeModel owner() {
        return clazz.owner();
    }

    private void addValue(String name, JAnnotationValue annotationValue) {
        // Use ordered map to keep the code generation the same on any JVM.
        // Lazily created.
        if(memberValues==null)
            memberValues = new LinkedHashMap<String, JAnnotationValue>();
        memberValues.put(name,annotationValue);
    }

    /**
     * Adds a member value pair to this annotation
     *
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The boolean value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, boolean value){
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The byte member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, byte value){
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The char member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, char value){
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The double member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, double value){
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The float member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, float value){
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The long member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, long value){
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The short member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, short value){
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The int member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, int value){
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The String member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, String value){
        //Escape string values with quotes so that they can
        //be generated accordingly
        addValue(name, new JAnnotationStringValue(JExpr.lit(value)));
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * For adding class values as param
     * @see #param(String, Class)
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The annotation class which is member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse annotationParam(String name, Class<? extends Annotation> value) {
        JAnnotationUse annotationUse = new JAnnotationUse(owner().ref(value));
        addValue(name, annotationUse);
        return annotationUse;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The enum class which is member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, final Enum<?> value) {
        addValue(name, new JAnnotationValue() {
                    public void generate(JFormatter f) {
                        f.t(owner().ref(value.getDeclaringClass())).p('.').p(value.name());
                    }
                });
        return this;
    }

    /**
     * Adds a member value pair to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The JEnumConstant which is member value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, JEnumConstant value){
        addValue(name, new JAnnotationStringValue(value));
        return this;
    }

     /**
      * Adds a member value pair to this annotation
      *  This can be used for e.g to specify
      * <pre>
      *        {@code @XmlCollectionItem(type=Integer.class);}
      * </pre>
      * For adding a value of {@code Class<? extends Annotation>}
      * {@link #annotationParam(java.lang.String, java.lang.Class)}
      * @param name
      *        The simple name for this annotation param
      *
      * @param value
      *        The class type of the param
      * @return
      *         The JAnnotationUse. More member value pairs can
      *         be added to it using the same or the overloaded methods.
      *
      *
      *
      */
     public JAnnotationUse param(String name, final Class<?> value){
         addValue(name, new JAnnotationStringValue(
        		 new JExpressionImpl() {
        			 public void generate(JFormatter f) {
        				 f.p(value.getName().replace('$', '.'));
        				 f.p(".class");
        			}
        		 }));
         return this;
    }

    /**
     * Adds a member value pair to this annotation based on the
     * type represented by the given JType
     *
     * @param name The simple name for this annotation param
     * @param type the JType representing the actual type
     * @return The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     */
    public JAnnotationUse param(String name, JType type){
        JClass c = type.boxify();
        addValue(name, new JAnnotationStringValue ( c.dotclass() ));
        return this;
    }

    /**
     * Adds a member value pair to this annotation.
     * @param name
     *        The simple name for this annotation
     *
     * @param value
     *        The JExpression which provides the contant value for this annotation
     * @return
     *         The JAnnotationUse. More member value pairs can
     *         be added to it using the same or the overloaded methods.
     *
     */
    public JAnnotationUse param(String name, JExpression value){
        addValue(name, new JAnnotationStringValue(value));
        return this;
    }

    /**
     * Adds a member value pair which is of type array to this annotation
     * @param name
     *        The simple name for this annotation
     *
     * @return
     *         The JAnnotationArrayMember. For adding array values
     *         @see JAnnotationArrayMember
     *
     */
    public JAnnotationArrayMember paramArray(String name){
        JAnnotationArrayMember arrayMember = new JAnnotationArrayMember(owner());
        addValue(name, arrayMember);
        return arrayMember;
    }


//    /**
//     * This can be used to add annotations inside annotations
//     * for e.g  &#64;XmlCollection(values= &#64;XmlCollectionItem(type=Foo.class))
//     * @param className
//     *         The classname of the annotation to be included
//     * @return
//     *         The JAnnotationUse that can be used as a member within this JAnnotationUse
//     * @deprecated
//     *      use {@link JAnnotationArrayMember#annotate}
//     */
//    public JAnnotationUse annotate(String className) {
//        JAnnotationUse annotationUse = new JAnnotationUse(owner().ref(className));
//        return annotationUse;
//    }

    /**
     * This can be used to add annotations inside annotations
     * for e.g  &#64;XmlCollection(values= &#64;XmlCollectionItem(type=Foo.class))
     * @param clazz
     *         The annotation class to be included
     * @return
     *     The JAnnotationUse that can be used as a member within this JAnnotationUse
     * @deprecated
     *      use {@link JAnnotationArrayMember#annotate}
     */
    @Deprecated
    public JAnnotationUse annotate(Class <? extends Annotation> clazz) {
         JAnnotationUse annotationUse = new JAnnotationUse(owner().ref(clazz));
         return annotationUse;
    }

    public void generate(JFormatter f) {
        f.p('@').g(clazz);
        if(memberValues!=null) {
            f.p('(');
            boolean first = true;

            if(isOptimizable()) {
                // short form
                f.g(memberValues.get("value"));
            } else {
                for (Map.Entry<String, JAnnotationValue> mapEntry : memberValues.entrySet()) {
                    if (!first) f.p(',');
                    f.p(mapEntry.getKey()).p('=').g(mapEntry.getValue());
                    first = false;
                }
            }
            f.p(')');
        }
    }

    private boolean isOptimizable() {
        return memberValues.size()==1 && memberValues.containsKey("value");
    }
}

