/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.namespace.QName;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.annotation.spec.XmlElementDeclWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlRegistryWriter;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.Constructor;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.FieldAccessor;
import com.sun.tools.xjc.outline.FieldOutline;
import org.glassfish.jaxb.core.v2.TODO;

/**
 * Generates <code>ObjectFactory</code> then wraps it and provides
 * access to it.
 *
 * <p>
 * The ObjectFactory contains
 * factory methods for each schema derived content class
 *
 * @author
 *      Ryan Shoemaker
 */
abstract class ObjectFactoryGeneratorImpl extends ObjectFactoryGenerator {

    private final BeanGenerator outline;
    private final Model model;
    private final JCodeModel codeModel;
    /**
     * Ref to {@link Class}.
     */
    private final JClass classRef;

    /**
     * Reference to the generated ObjectFactory class.
     */
    private final JDefinedClass objectFactory;

    /** map of qname to the QName constant field. */
    private final HashMap<QName,JFieldVar> qnameMap = new HashMap<QName,JFieldVar>();

    /**
     * Names of the element factory methods that are created.
     * Used to detect collisions.
     *
     * The value is used for reporting error locations.
     */
    private final Map<String,CElementInfo> elementFactoryNames = new HashMap<String,CElementInfo>();

    /**
     * Names of the value factory methods that are created.
     * Used to detect collisions.
     *
     * The value is used for reporting error locations.
     */
    private final Map<String,ClassOutlineImpl> valueFactoryNames = new HashMap<String,ClassOutlineImpl>();

    /**
     * Returns a reference to the generated (public) ObjectFactory
     */
    public JDefinedClass getObjectFactory() {
        return objectFactory;
    }




    public ObjectFactoryGeneratorImpl( BeanGenerator outline, Model model, JPackage targetPackage ) {
        this.outline = outline;
        this.model = model;
        this.codeModel = this.model.codeModel;
        this.classRef = codeModel.ref(Class.class);

        // create the ObjectFactory class skeleton
        objectFactory = this.outline.getClassFactory().createClass(
                targetPackage, "ObjectFactory", null );
        objectFactory.annotate2(XmlRegistryWriter.class);

        // generate the default constructor
        //
        // m1 result:
        //        public ObjectFactory() {}
        JMethod m1 = objectFactory.constructor(JMod.PUBLIC);
        m1.javadoc().append("Create a new ObjectFactory that can be used to " +
                         "create new instances of schema derived classes " +
                         "for package: " + targetPackage.name());

        // add some class javadoc
        objectFactory.javadoc().append(
            "This object contains factory methods for each \n" +
            "Java content interface and Java element interface \n" +
            "generated in the " + targetPackage.name() + " package. \n" +
            "<p>An ObjectFactory allows you to programatically \n" +
            "construct new instances of the Java representation \n" +
            "for XML content. The Java representation of XML \n" +
            "content can consist of schema derived interfaces \n" +
            "and classes representing the binding of schema \n" +
            "type definitions, element declarations and model \n" +
            "groups.  Factory methods for each of these are \n" +
            "provided in this class." );

    }

    /**
     * Adds code for the given {@link CElementInfo} to ObjectFactory.
     */
    protected final void populate( CElementInfo ei, Aspect impl, Aspect exposed ) {
        JType exposedElementType = ei.toType(outline,exposed);
        JType exposedType = ei.getContentInMemoryType().toType(outline,exposed);
        JType implType = ei.getContentInMemoryType().toType(outline,impl);
        String namespaceURI = ei.getElementName().getNamespaceURI();
        String localPart = ei.getElementName().getLocalPart();

        JClass scope=null;
        if(ei.getScope()!=null)
            scope = outline.getClazz(ei.getScope()).implClass;


        JMethod m;

        if(ei.isAbstract()) {
            // TODO: see the "Abstract elements and mighty IXmlElement" e-mail
            // that I sent to jaxb-tech
            TODO.checkSpec();
        }

        {// collision check
            CElementInfo existing = elementFactoryNames.put(ei.getSqueezedName(),ei);
            if( existing!=null ) {
                outline.getErrorReceiver().error(existing.getLocator(),
                    Messages.OBJECT_FACTORY_CONFLICT.format(ei.getSqueezedName()));
                outline.getErrorReceiver().error(ei.getLocator(),
                    Messages.OBJECT_FACTORY_CONFLICT_RELATED.format());
                return;
            }
        }

        // no arg constructor
        // [RESULT] if the element doesn't have its own class, something like:
        //
        //        @XmlElementMapping(uri = "", name = "foo")
        //        public JAXBElement<Foo> createFoo( Foo value ) {
        //            return new JAXBElement<Foo>(
        //                new QName("","foo"),(Class)FooImpl.class,scope,(FooImpl)value);
        //        }
        //        NOTE: when we generate value classes Foo==FooImpl
        //
        // [RESULT] otherwise
        //
        //        @XmlElementMapping(uri = "", name = "foo")
        //        public Foo createFoo( FooType value ) {
        //            return new Foo((FooTypeImpl)value);
        //        }
        //        NOTE: when we generate value classes FooType==FooTypeImpl
        //
        // to deal with
        //  new JAXBElement<List<String>>( ..., List.class, ... );
        // we sometimes have to produce (Class)List.class instead of just List.class

        m = objectFactory.method( JMod.PUBLIC, exposedElementType, "create" + ei.getSqueezedName() );
        JVar $value = m.param(exposedType,"value");

        JExpression declaredType;
        if(implType.boxify().isParameterized() || !exposedType.equals(implType))
            declaredType = JExpr.cast(classRef,implType.boxify().dotclass());
        else
            declaredType = implType.boxify().dotclass();
        JExpression scopeClass = scope==null?JExpr._null():scope.dotclass();

        // build up the return extpression
        JInvocation exp = JExpr._new(exposedElementType);
        if(!ei.hasClass()) {
            exp.arg(getQNameInvocation(ei));
            exp.arg(declaredType);
            exp.arg(scopeClass);
        }
        if(implType==exposedType)
            exp.arg($value);
        else
            exp.arg(JExpr.cast(implType,$value));

        m.body()._return( exp );

        m.javadoc()
            .append("Create an instance of ")
            .append(exposedElementType);
        m.javadoc().addParam($value)
            .append("Java instance representing xml element's value.");
        m.javadoc().addReturn()
            .append("the new instance of ")
            .append(exposedElementType);

        XmlElementDeclWriter xemw = m.annotate2(XmlElementDeclWriter.class);
        xemw.namespace(namespaceURI).name(localPart);
        if(scope!=null)
            xemw.scope(scope);

        if(ei.getSubstitutionHead()!=null) {
            QName n = ei.getSubstitutionHead().getElementName();
            xemw.substitutionHeadNamespace(n.getNamespaceURI());
            xemw.substitutionHeadName(n.getLocalPart());
        }

        if(ei.getDefaultValue()!=null)
            xemw.defaultValue(ei.getDefaultValue());

        if(ei.getProperty().inlineBinaryData())
            m.annotate(XmlInlineBinaryData.class);

                    // if the element is adapter, put that annotation on the factory method
        outline.generateAdapterIfNecessary(ei.getProperty(),m);
    }

    /**
     * return a JFieldVar that represents the QName field for the given information.
     *
     * if it doesn't exist, create a static field in the class and store a new JFieldVar.
     */
    private JExpression getQNameInvocation(CElementInfo ei) {
        QName name = ei.getElementName();
        if(qnameMap.containsKey(name)) {
            return qnameMap.get(name);
        }

        if(qnameMap.size()>1024)
            // stop gap measure to avoid 'code too large' error in javac.
            return createQName(name);

        // [RESULT]
        // private static final QName _XYZ_NAME = new QName("uri", "local");
        JFieldVar qnameField = objectFactory.field(
            JMod.PRIVATE | JMod.STATIC | JMod.FINAL,
            QName.class,
            '_' + ei.getSqueezedName() + "_QNAME", createQName(name));

        qnameMap.put(name, qnameField);

        return qnameField;
    }

    /**
     * Generates an expression that evaluates to "new QName(...)"
     */
    private JInvocation createQName(QName name) {
        return JExpr._new(codeModel.ref(QName.class)).arg(name.getNamespaceURI()).arg(name.getLocalPart());
    }

    protected final void populate( ClassOutlineImpl cc, JClass sigType ) {
        // add static factory method for this class to JAXBContext.
        //
        // generate methods like:
        //     public static final SIGTYPE createFoo() {
        //         return new FooImpl();
        //     }

        if(!cc.target.isAbstract()) {
            JMethod m = objectFactory.method(
                JMod.PUBLIC, sigType, "create" + cc.target.getSqueezedName() );
            m.body()._return( JExpr._new(cc.implRef) );

            // add some jdoc to avoid javadoc warnings in jdk1.4
            m.javadoc()
                .append("Create an instance of ")
                .append(cc.ref);
        }


        // add static factory methods for all the other constructors.
        Collection<? extends Constructor> consl = cc.target.getConstructors();
        if(consl.size()!=0) {
            // if we are going to add constructors with parameters,
            // first we need to have a default constructor.
            cc.implClass.constructor(JMod.PUBLIC);
        }

        {// collision check
            String name = cc.target.getSqueezedName();
            ClassOutlineImpl existing = valueFactoryNames.put(name,cc);
            if( existing!=null ) {
                outline.getErrorReceiver().error(existing.target.getLocator(),
                    Messages.OBJECT_FACTORY_CONFLICT.format(name));
                outline.getErrorReceiver().error(cc.target.getLocator(),
                    Messages.OBJECT_FACTORY_CONFLICT_RELATED.format());
                return;
            }
        }

        for( Constructor cons : consl ) {
            // method on ObjectFactory
            // [RESULT]
            // Foo createFoo( T1 a, T2 b, T3 c, ... ) throws JAXBException {
            //    return new FooImpl(a,b,c,...);
            // }
            JMethod m = objectFactory.method( JMod.PUBLIC,
                cc.ref, "create" + cc.target.getSqueezedName() );
            JInvocation inv = JExpr._new(cc.implRef);
            m.body()._return(inv);

            // let's not throw this exception.
            // m._throws(codeModel.ref(JAXBException.class));

            // add some jdoc to avoid javadoc warnings in jdk1.4
            m.javadoc()
                .append( "Create an instance of " )
                .append( cc.ref )
                .addThrows(JAXBException.class).append("if an error occurs");

            // constructor
            // [RESULT]
            // FooImpl( T1 a, T2 b, T3 c, ... ) {
            // }
            JMethod c = cc.implClass.constructor(JMod.PUBLIC);

            for( String fieldName : cons.fields ) {
                CPropertyInfo field = cc.target.getProperty(fieldName);
                if(field==null) {
                    outline.getErrorReceiver().error(cc.target.getLocator(),
                        Messages.ILLEGAL_CONSTRUCTOR_PARAM.format(fieldName));
                    continue;
                }

                fieldName = camelize(fieldName);

                FieldOutline fo = outline.getField(field);
                FieldAccessor accessor = fo.create(JExpr._this());

                // declare a parameter on this factory method and set
                // it to the field
                inv.arg(m.param( fo.getRawType(), fieldName ));

                JVar $var = c.param( fo.getRawType(), fieldName );
                accessor.fromRawValue(c.body(),'_'+fieldName,$var);
            }
        }
    }


    /** Change the first character to the lower case. */
    private static String camelize( String s ) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
