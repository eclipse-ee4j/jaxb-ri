/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.runtime.v2.model.annotation.FieldLocatable;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeEnumLeafInfo;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimeNonElement;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kohsuke Kawaguchi
 */
final class RuntimeEnumLeafInfoImpl<T extends Enum<T>,B> extends EnumLeafInfoImpl<Type,Class,Field,Method>
    implements RuntimeEnumLeafInfo, Transducer<T> {

    @Override
    public Transducer<T> getTransducer() {
        return this;
    }

    /**
     * {@link Transducer} that knows how to convert a lexical value
     * into the Java value that we can handle.
     */
    private final Transducer<B> baseXducer;

    private final Map<B,T> parseMap = new HashMap<>();
    private final Map<T,B> printMap;

    RuntimeEnumLeafInfoImpl(RuntimeModelBuilder builder, Locatable upstream, Class<T> enumType) {
        super(builder,upstream,enumType,enumType);
        this.printMap = new EnumMap<>(enumType);

        baseXducer = ((RuntimeNonElement)baseType).getTransducer();
    }

    @Override
    public RuntimeEnumConstantImpl createEnumConstant(String name, String literal, Field constant, EnumConstantImpl<Type,Class,Field,Method> last) {
        T t;
        try {
            try {
                constant.setAccessible(true);
            } catch (SecurityException e) {
                // in case the constant is already accessible, swallow this error.
                // if the constant is indeed not accessible, we will get IllegalAccessException
                // in the following line, and that is not too late.
            }
            t = (T)constant.get(null);
        } catch (IllegalAccessException e) {
            // impossible, because this is an enum constant
            throw new IllegalAccessError(e.getMessage());
        }

        B b = null;
        try {
            b = baseXducer.parse(literal);
        } catch (Exception e) {
            builder.reportError(new IllegalAnnotationException(
                Messages.INVALID_XML_ENUM_VALUE.format(literal,baseType.getType().toString()), e,
                    new FieldLocatable<>(this,constant,nav()) ));
        }

        parseMap.put(b,t);
        printMap.put(t,b);

        return new RuntimeEnumConstantImpl(this, name, literal, last);
    }

    @Override
    public QName[] getTypeNames() {
        return new QName[]{getTypeName()};
    }

    @Override
    public Class getClazz() {
        return clazz;
    }

    @Override
    public boolean useNamespace() {
        return baseXducer.useNamespace();
    }

    @Override
    public void declareNamespace(T t, XMLSerializer w) throws AccessorException {
        baseXducer.declareNamespace(printMap.get(t),w);
    }

    @Override
    public CharSequence print(T t) throws AccessorException {
        return baseXducer.print(printMap.get(t));
    }

    @Override
    public T parse(CharSequence lexical) throws AccessorException, SAXException {
        // TODO: error handling

        B b = baseXducer.parse(lexical);

        if (tokenStringType) {
            b = (B) ((String)b).trim();
        }

        return parseMap.get(b);
    }

    @Override
    public void writeText(XMLSerializer w, T t, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        baseXducer.writeText(w,printMap.get(t),fieldName);
    }

    @Override
    public void writeLeafElement(XMLSerializer w, Name tagName, T o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
        baseXducer.writeLeafElement(w,tagName,printMap.get(o),fieldName);
    }

    @Override
    public QName getTypeName(T instance) {
        return null;
    }
}
