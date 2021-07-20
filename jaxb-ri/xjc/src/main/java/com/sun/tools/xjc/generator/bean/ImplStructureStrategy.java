/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * Use is subject to the license terms.
 */
package com.sun.tools.xjc.generator.bean;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.annotation.spec.XmlAccessorTypeWriter;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;

/**
 * Decides how a bean token is mapped to the generated classes.
 *
 * <p>
 * The actual implementations of this interface is tightly coupled with
 * the backend, but the front-end gets to choose which strategy to be used.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
@XmlEnum(Boolean.class)
public enum ImplStructureStrategy {
    /**
     * Generates beans only. The simplest code generation.
     */
    @XmlEnumValue("true")
    BEAN_ONLY() {
        protected Result createClasses(Outline outline, CClassInfo bean) {
            JClassContainer parent = outline.getContainer( bean.parent(), Aspect.EXPOSED );

            JDefinedClass impl = outline.getClassFactory().createClass(
                parent,
                JMod.PUBLIC|(parent.isPackage()?0:JMod.STATIC)|(bean.isAbstract()?JMod.ABSTRACT:0),
                bean.shortName, bean.getLocator() );
            impl.annotate2(XmlAccessorTypeWriter.class).value(XmlAccessType.FIELD);

            return new Result(impl,impl);
        }

        protected JPackage getPackage(JPackage pkg, Aspect a) {
            return pkg;
        }

        protected MethodWriter createMethodWriter(final ClassOutlineImpl target) {
            assert target.ref==target.implClass;

            return new MethodWriter(target) {
                private final JDefinedClass impl = target.implClass;

                private JMethod implMethod;

                public JVar addParameter(JType type, String name) {
                    return implMethod.param(type,name);
                }

                public JMethod declareMethod(JType returnType, String methodName) {
                    implMethod = impl.method( JMod.PUBLIC, returnType, methodName );
                    return implMethod;
                }

                public JDocComment javadoc() {
                    return implMethod.javadoc();
                }
            };
        }

        protected void _extends(ClassOutlineImpl derived, ClassOutlineImpl base) {
            derived.implClass._extends(base.implRef);
        }
    },

    /**
     * Generates the interfaces to describe beans (content interfaces)
     * and then the beans themselves in a hidden impl package.
     *
     * Similar to JAXB 1.0.
     */
    @XmlEnumValue("false")
    INTF_AND_IMPL() {
        protected Result createClasses( Outline outline, CClassInfo bean ) {
            JClassContainer parent = outline.getContainer( bean.parent(), Aspect.EXPOSED );

            JDefinedClass intf = outline.getClassFactory().createInterface(
                parent, bean.shortName, bean.getLocator() );

            parent = outline.getContainer(bean.parent(), Aspect.IMPLEMENTATION);
            JDefinedClass impl = outline.getClassFactory().createClass(
                parent,
                JMod.PUBLIC|(parent.isPackage()?0:JMod.STATIC)|(bean.isAbstract()?JMod.ABSTRACT:0),
                bean.shortName+"Impl", bean.getLocator() );
            impl.annotate2(XmlAccessorTypeWriter.class).value(XmlAccessType.FIELD);

            impl._implements(intf);

            return new Result(intf,impl);
        }

        protected JPackage getPackage(JPackage pkg, Aspect a) {
            switch(a) {
            case EXPOSED:
                return pkg;
            case IMPLEMENTATION:
                return pkg.subPackage("impl");
            default:
                assert false;
                throw new IllegalStateException();
            }
        }

        protected MethodWriter createMethodWriter(final ClassOutlineImpl target) {
            return new MethodWriter(target) {
                private final JDefinedClass intf = target.ref;
                private final JDefinedClass impl = target.implClass;

                private JMethod intfMethod;
                private JMethod implMethod;

                public JVar addParameter(JType type, String name) {
                    // TODO: do we still need to deal with the case where intf is null?
                    if(intf!=null)
                        intfMethod.param(type,name);
                    return implMethod.param(type,name);
                }

                public JMethod declareMethod(JType returnType, String methodName) {
                    if(intf!=null)
                        intfMethod = intf.method( 0, returnType, methodName );
                    implMethod = impl.method( JMod.PUBLIC, returnType, methodName );
                    return implMethod;
                }

                public JDocComment javadoc() {
                    if(intf!=null)
                        return intfMethod.javadoc();
                    else
                        return implMethod.javadoc();
                }
            };
        }

        protected void _extends(ClassOutlineImpl derived, ClassOutlineImpl base) {
            derived.implClass._extends(base.implRef);
            derived.ref._implements(base.ref);
        }
    };


    /**
     * Creates class(es) for the given bean.
     */
    protected abstract Result createClasses( Outline outline, CClassInfo bean );

    /**
     * Gets the specified aspect of the given package.
     */
    protected abstract JPackage getPackage( JPackage pkg, Aspect a );

    protected abstract MethodWriter createMethodWriter( ClassOutlineImpl target );

    /**
     * Sets up an inheritance relationship.
     */
    protected abstract void _extends( ClassOutlineImpl derived, ClassOutlineImpl base );

    public static final class Result {
        /**
         * Corresponds to {@link Aspect#EXPOSED}
         */
        public final JDefinedClass exposed;
        /**
         * Corresponds to {@link Aspect#IMPLEMENTATION}
         */
        public final JDefinedClass implementation;

        public Result(JDefinedClass exposed, JDefinedClass implementation) {
            this.exposed = exposed;
            this.implementation = implementation;
        }
    }
}
