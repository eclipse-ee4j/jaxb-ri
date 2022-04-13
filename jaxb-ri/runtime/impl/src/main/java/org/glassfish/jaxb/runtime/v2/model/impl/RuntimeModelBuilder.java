/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import com.sun.istack.Nullable;
import org.glassfish.jaxb.core.WhiteSpaceProcessor;
import org.glassfish.jaxb.core.v2.model.core.ArrayInfo;
import org.glassfish.jaxb.core.v2.model.core.ClassInfo;
import org.glassfish.jaxb.core.v2.model.core.ElementInfo;
import org.glassfish.jaxb.core.v2.model.core.EnumLeafInfo;
import org.glassfish.jaxb.core.v2.model.core.RegistryInfo;
import org.glassfish.jaxb.core.v2.model.core.TypeInfoSet;
import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.runtime.v2.model.annotation.RuntimeAnnotationReader;
import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeNonElement;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeNonElementRef;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimePropertyInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeTypeInfoSet;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.runtime.v2.runtime.*;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallingContext;
import jakarta.activation.MimeType;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * {@link ModelBuilder} that works at the run-time by using
 * the {@code java.lang.reflect} package.
 *
 * <p>
 * This extends {@link ModelBuilder} by providing more functionalities such
 * as accessing the fields and classes.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class RuntimeModelBuilder extends ModelBuilder<Type,Class,Field,Method> {
    /**
     * The {@link JAXBContextImpl} for which the model is built.
     * Null when created for reflection.
     */
    public final @Nullable
    JAXBContextImpl context;

    public RuntimeModelBuilder(JAXBContextImpl context, RuntimeAnnotationReader annotationReader, Map<Class, Class> subclassReplacements, String defaultNamespaceRemap) {
        super(annotationReader, Utils.REFLECTION_NAVIGATOR, subclassReplacements, defaultNamespaceRemap);
        this.context = context;
    }

    @Override
    public RuntimeNonElement getClassInfo(Class clazz, Locatable upstream ) {
        return (RuntimeNonElement)super.getClassInfo(clazz,upstream);
    }

    @Override
    public RuntimeNonElement getClassInfo( Class clazz, boolean searchForSuperClass, Locatable upstream ) {
        return (RuntimeNonElement)super.getClassInfo(clazz,searchForSuperClass,upstream);
    }

    @Override
    protected EnumLeafInfo<Type,Class> createEnumLeafInfo(Class clazz, Locatable upstream) {
        return new RuntimeEnumLeafInfoImpl<>(this,upstream,clazz);
    }

    @Override
    protected ClassInfo<Type, Class> createClassInfo(Class clazz, Locatable upstream ) {
        return new RuntimeClassInfoImpl(this,upstream,clazz);
    }

    @Override
    public ElementInfo<Type, Class> createElementInfo(RegistryInfo<Type,Class> registryInfo, Method method) throws IllegalAnnotationException {
        return new RuntimeElementInfoImpl(this,registryInfo, method);
    }

    @Override
    public ArrayInfo<Type, Class> createArrayInfo(Locatable upstream, Type arrayType) {
        return new RuntimeArrayInfoImpl(this, upstream, (Class)arrayType);
    }

    @Override
    protected TypeInfoSet<Type, Class, Field, Method> createTypeInfoSet() {
        return new RuntimeTypeInfoSetImpl(reader);
    }

    @Override
    public RuntimeTypeInfoSet link() {
        return (RuntimeTypeInfoSet)super.link();
    }

    /**
     * Creates a {@link Transducer} given a reference.
     *
     * Used to implement {@link RuntimeNonElementRef#getTransducer()}.
     * Shouldn't be called from anywhere else.
     *
     * TODO: this is not the proper place for this class to be in.
     */
    @SuppressWarnings({"unchecked"})
    public static <V> Transducer<V> createTransducer(RuntimeNonElementRef ref) {
        Transducer<V> t = ref.getTarget().getTransducer();
        RuntimePropertyInfo src = ref.getSource();
        ID id = src.id();

        if(id==ID.IDREF)
            return (Transducer<V>) RuntimeBuiltinLeafInfoImpl.STRING;

        if(id==ID.ID)
            t = new IDTransducerImpl<>(t);

        MimeType emt = src.getExpectedMimeType();
        if(emt!=null)
            t = new MimeTypedTransducer<>(t,emt);

        if(src.inlineBinaryData())
            t = new InlineBinaryTransducer<>(t);

        if(src.getSchemaType()!=null) {
            if (src.getSchemaType().equals(createXSSimpleType())) {
                return (Transducer<V>) RuntimeBuiltinLeafInfoImpl.STRING;
            }
            t = new SchemaTypeTransducer<>(t,src.getSchemaType());
        }
        
        return t;
    }

    private static QName createXSSimpleType() {
        return new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI,"anySimpleType");
    }

    /**
     * Transducer implementation for ID.
     *
     * This transducer wraps another {@link Transducer} and adds
     * handling for ID.
     */
    private static final class IDTransducerImpl<ValueT> extends FilterTransducer<ValueT> {
        public IDTransducerImpl(Transducer<ValueT> core) {
            super(core);
        }

        @Override
        public ValueT parse(CharSequence lexical) throws AccessorException, SAXException {
            String value = WhiteSpaceProcessor.trim(lexical).toString();
            UnmarshallingContext.getInstance().addToIdTable(value);
            return core.parse(value);
        }
    }
}
