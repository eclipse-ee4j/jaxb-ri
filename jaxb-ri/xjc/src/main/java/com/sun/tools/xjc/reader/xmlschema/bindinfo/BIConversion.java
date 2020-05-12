/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.DatatypeConverter;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.codemodel.JConditional;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.model.CAdapter;
import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.TypeUse;
import com.sun.tools.xjc.model.TypeUseFactory;
import com.sun.tools.xjc.reader.Const;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.TypeUtil;
import com.sun.tools.xjc.reader.xmlschema.ClassSelector;
import org.glassfish.jaxb.core.v2.WellKnownNamespace;
import com.sun.xml.xsom.XSSimpleType;

import org.xml.sax.Locator;

/**
 * Conversion declaration.
 * 
 * <p>
 * A conversion declaration specifies how an XML type gets mapped
 * to a Java type.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class BIConversion extends AbstractDeclarationImpl {
    @Deprecated
    public BIConversion( Locator loc ) {
        super(loc);
    }

    protected BIConversion() {
    }

    /**
     * Gets the {@link TypeUse} object that this conversion represents.
     * <p>
     * The returned {@link TypeUse} object is properly adapted.
     *
     * @param owner
     *      A {@link BIConversion} is always associated with one
     *      {@link XSSimpleType}, but that's not always available
     *      when a {@link BIConversion} is built. So we pass this
     *      as a parameter to this method.
     */
    public abstract TypeUse getTypeUse( XSSimpleType owner );

    public QName getName() { return NAME; }
    
    /** Name of the conversion declaration. */
    public static final QName NAME = new QName(
        Const.JAXB_NSURI, "conversion" );

    /**
     * Implementation that returns a statically-determined constant {@link TypeUse}.
     */
    public static final class Static extends BIConversion {
        /**
         * Always non-null.
         */
        private final TypeUse transducer;

        public Static(Locator loc, TypeUse transducer) {
            super(loc);
            this.transducer = transducer;
        }

        public TypeUse getTypeUse(XSSimpleType owner) {
            return transducer;
        }
    }

    /**
     * User-specified {@code <javaType>} customization.
     *
     * The parse/print methods are allowed to be null,
     * and their default values are determined based on the
     * owner of the token.
     */
    @XmlRootElement(name="javaType")
    public static class User extends BIConversion {
        @XmlAttribute
        private String parseMethod;
        @XmlAttribute
        private String printMethod;
        @XmlAttribute(name="name")
        private String type = "java.lang.String";

        /**
         * If null, computed from {@link #type}.
         * Sometimes this can be set instead of {@link #type}.
         */
        private JType inMemoryType;

        public User(Locator loc, String parseMethod, String printMethod, JType inMemoryType) {
            super(loc);
            this.parseMethod = parseMethod;
            this.printMethod = printMethod;
            this.inMemoryType = inMemoryType;
        }

        public User() {
        }

        /**
         * Cache used by {@link #getTypeUse(XSSimpleType)} to improve the performance.
         */
        private TypeUse typeUse;

        public TypeUse getTypeUse(XSSimpleType owner) {
            if(typeUse!=null)
                return typeUse;

            JCodeModel cm = getCodeModel();

            if(inMemoryType==null)
                inMemoryType = TypeUtil.getType(cm,type,Ring.get(ErrorReceiver.class),getLocation());

            JDefinedClass adapter = generateAdapter(parseMethodFor(owner),printMethodFor(owner),owner);

            // XmlJavaType customization always converts between string and an user-defined type.
            typeUse = TypeUseFactory.adapt(CBuiltinLeafInfo.STRING,new CAdapter(adapter));

            return typeUse;
        }

        /**
         * generate the adapter class.
         */
        private JDefinedClass generateAdapter(String parseMethod, String printMethod,XSSimpleType owner) {
            JDefinedClass adapter = null;

            int id = 1;
            while(adapter==null) {
                try {
                    JPackage pkg = Ring.get(ClassSelector.class).getClassScope().getOwnerPackage();
                    adapter = pkg._class("Adapter"+id);
                } catch (JClassAlreadyExistsException e) {
                    // try another name in search for an unique name.
                    // this isn't too efficient, but we expect people to usually use
                    // a very small number of adapters.
                    id++;
                }
            }

            JClass bim = inMemoryType.boxify();

            adapter._extends(getCodeModel().ref(XmlAdapter.class).narrow(String.class).narrow(bim));

            JMethod unmarshal = adapter.method(JMod.PUBLIC, bim, "unmarshal");
            JVar $value = unmarshal.param(String.class, "value");

            JExpression inv;

            if( parseMethod.equals("new") ) {
                // "new" indicates that the constructor of the target type
                // will do the unmarshalling.

                // RESULT: new <type>()
                inv = JExpr._new(bim).arg($value);
            } else {
                int idx = parseMethod.lastIndexOf('.');
                if(idx<0) {
                    // parseMethod specifies the static method of the target type
                    // which will do the unmarshalling.

                    // because of an error check at the constructor,
                    // we can safely assume that this cast works.
                    inv = bim.staticInvoke(parseMethod).arg($value);
                } else {
                    inv = JExpr.direct(parseMethod+"(value)");
                }
            }
            unmarshal.body()._return(inv);


            JMethod marshal = adapter.method(JMod.PUBLIC, String.class, "marshal");
            $value = marshal.param(bim,"value");

            if(printMethod.startsWith("jakarta.xml.bind.DatatypeConverter.")) {
                // UGLY: if this conversion is the system-driven conversion,
                // check for null
                marshal.body()._if($value.eq(JExpr._null()))._then()._return(JExpr._null());
            }

            int idx = printMethod.lastIndexOf('.');
            if(idx<0) {
                // printMethod specifies a method in the target type
                // which performs the serialization.

                // RESULT: <value>.<method>()
                inv = $value.invoke(printMethod);
                
                // check value is not null ... if(value == null) return null;
                JConditional jcon = marshal.body()._if($value.eq(JExpr._null()));
                jcon._then()._return(JExpr._null());
            } else {
                // RESULT: <className>.<method>(<value>)
                if(this.printMethod==null) {
                    // HACK HACK HACK
                    JType t = inMemoryType.unboxify();
                    inv = JExpr.direct(printMethod+"(("+findBaseConversion(owner).toLowerCase()+")("+t.fullName()+")value)");
                } else
                    inv = JExpr.direct(printMethod+"(value)");
            }
            marshal.body()._return(inv);

            return adapter;
        }

        private String printMethodFor(XSSimpleType owner) {
            if(printMethod!=null)   return printMethod;

            if(inMemoryType.unboxify().isPrimitive()) {
                String method = getConversionMethod("print",owner);
                if(method!=null)
                    return method;
            }

            return "toString";
        }

        private String parseMethodFor(XSSimpleType owner) {
            if(parseMethod!=null)   return parseMethod;

            if(inMemoryType.unboxify().isPrimitive()) {
                String method = getConversionMethod("parse", owner);
                if(method!=null) {
                    // this cast is necessary for conversion between primitive Java types
                    return '('+inMemoryType.unboxify().fullName()+')'+method;
                }
            }

            return "new";
        }

        private static final String[] knownBases = new String[]{
            "Float", "Double", "Byte", "Short", "Int", "Long", "Boolean"
        };

        private String getConversionMethod(String methodPrefix, XSSimpleType owner) {
            String bc = findBaseConversion(owner);
            if(bc==null)    return null;

            return DatatypeConverter.class.getName()+'.'+methodPrefix+bc;
        }

        private String findBaseConversion(XSSimpleType owner) {
            // find the base simple type mapping.
            for( XSSimpleType st=owner; st!=null; st = st.getSimpleBaseType() ) {
                if( !WellKnownNamespace.XML_SCHEMA.equals(st.getTargetNamespace()) )
                    continue;   // user-defined type

                String name = st.getName().intern();
                for( String s : knownBases )
                    if(name.equalsIgnoreCase(s))
                        return s;
            }

            return null;
        }

        public QName getName() { return NAME; }

        /** Name of the conversion declaration. */
        public static final QName NAME = new QName(
            Const.JAXB_NSURI, "javaType" );
    }

    @XmlRootElement(name="javaType",namespace=Const.XJC_EXTENSION_URI)
    public static class UserAdapter extends BIConversion {
        @XmlAttribute(name="name")
        private String type = null;

        @XmlAttribute
        private String adapter = null;

        private TypeUse typeUse;

        public TypeUse getTypeUse(XSSimpleType owner) {
            if(typeUse!=null)
                return typeUse;

            JCodeModel cm = getCodeModel();

            JDefinedClass a;
            try {
                a = cm._class(adapter);
                a.hide();   // we assume this is given by the user
                a._extends(cm.ref(XmlAdapter.class).narrow(String.class).narrow(
                        cm.ref(type)));
            } catch (JClassAlreadyExistsException e) {
                a = e.getExistingClass();
            }

            // TODO: it's not correct to say that it adapts from String,
            // but OTOH I don't think we can compute that.
            typeUse = TypeUseFactory.adapt(
                    CBuiltinLeafInfo.STRING,
                    new CAdapter(a));

            return typeUse;
        }
    }
}

