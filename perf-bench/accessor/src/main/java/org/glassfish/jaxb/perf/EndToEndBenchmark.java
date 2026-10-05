/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.perf;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import org.glassfish.jaxb.runtime.AccessorFactory;
import org.glassfish.jaxb.runtime.AccessorFactoryImpl;
import org.glassfish.jaxb.runtime.XmlAccessorFactory;
import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import tools.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/** End-to-end JAXB/Jackson throughput over the same verified object graph. */
@State(Scope.Thread)
public class EndToEndBenchmark {
    @Param({"4", "32", "256"})
    public int books;

    private Marshaller reflectionMarshaller;
    private Marshaller handleMarshaller;
    private Unmarshaller reflectionUnmarshaller;
    private Unmarshaller handleUnmarshaller;
    private XmlMapper jackson;
    private Catalog catalog;
    private byte[] xml;
    private final ByteArrayOutputStream output = new ByteArrayOutputStream(16 * 1024);

    @Setup(Level.Trial)
    public void setup() throws Exception {
        CatalogAccessorFactory.useHandles = false;
        JAXBRIContext reflectionContext = context();
        CatalogAccessorFactory.useHandles = true;
        JAXBRIContext handleContext = context();
        reflectionMarshaller = reflectionContext.createMarshaller();
        handleMarshaller = handleContext.createMarshaller();
        reflectionUnmarshaller = reflectionContext.createUnmarshaller();
        handleUnmarshaller = handleContext.createUnmarshaller();
        jackson = new XmlMapper();

        catalog = fixture(books);
        output.reset();
        handleMarshaller.marshal(catalog, output);
        xml = output.toByteArray();
        verifyGraph((Catalog) reflectionUnmarshaller.unmarshal(new ByteArrayInputStream(xml)));
        verifyGraph((Catalog) handleUnmarshaller.unmarshal(new ByteArrayInputStream(xml)));
        verifyGraph(jackson.readValue(xml, Catalog.class));

        output.reset();
        jackson.writeValue(output, catalog);
        verifyGraph((Catalog) handleUnmarshaller.unmarshal(new ByteArrayInputStream(output.toByteArray())));
    }

    private static JAXBRIContext context() throws JAXBException {
        return JAXBRIContext.newInstance(new Class<?>[]{Catalog.class, Book.class}, null,
                null, null, false, null, true, false, false, false);
    }

    private static Catalog fixture(int count) {
        Catalog result = new Catalog();
        for (int i = 0; i < count; i++) {
            Book book = new Book();
            book.id = i;
            book.title = "XML book " + i + " — JAXB benchmark";
            result.books.add(book);
        }
        return result;
    }

    private void verifyGraph(Catalog actual) {
        if (actual == null || actual.books.size() != catalog.books.size())
            throw new IllegalStateException("Binding did not produce the expected catalog size");
        for (int i = 0; i < catalog.books.size(); i++) {
            Book expected = catalog.books.get(i), found = actual.books.get(i);
            if (expected.id != found.id || !expected.title.equals(found.title))
                throw new IllegalStateException("Binding mismatch at book " + i);
        }
    }

    @Benchmark public void marshalReflection() throws Exception {
        output.reset();
        reflectionMarshaller.marshal(catalog, output);
    }

    @Benchmark public void marshalHandles() throws Exception {
        output.reset();
        handleMarshaller.marshal(catalog, output);
    }

    @Benchmark public void marshalJackson() throws Exception {
        output.reset();
        jackson.writeValue(output, catalog);
    }

    @Benchmark public Object unmarshalReflection() throws Exception {
        return reflectionUnmarshaller.unmarshal(new ByteArrayInputStream(xml));
    }

    @Benchmark public Object unmarshalHandles() throws Exception {
        return handleUnmarshaller.unmarshal(new ByteArrayInputStream(xml));
    }

    @Benchmark public Object unmarshalJackson() throws Exception {
        return jackson.readValue(xml, Catalog.class);
    }

    /** Test-only switch selected while JAXB builds each context's immutable model. */
    public static final class CatalogAccessorFactory implements AccessorFactory {
        static volatile boolean useHandles;
        private final AccessorFactoryImpl handles = AccessorFactoryImpl.getInstance();

        @Override public Accessor createFieldAccessor(Class bean, java.lang.reflect.Field field, boolean readOnly)
                throws JAXBException {
            if (useHandles) return handles.createFieldAccessor(bean, field, readOnly);
            return readOnly ? new Accessor.ReadOnlyFieldReflection(field) : new Accessor.FieldReflection(field);
        }

        @Override public Accessor createPropertyAccessor(Class bean, java.lang.reflect.Method getter,
                java.lang.reflect.Method setter) throws JAXBException {
            if (useHandles) return handles.createPropertyAccessor(bean, getter, setter);
            if (getter == null) return new Accessor.SetterOnlyReflection(setter);
            if (setter == null) return new Accessor.GetterOnlyReflection(getter);
            return new Accessor.GetterSetterReflection(getter, setter);
        }
    }

    @XmlRootElement(name = "catalog")
    @JacksonXmlRootElement(localName = "catalog")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlAccessorFactory(CatalogAccessorFactory.class)
    public static class Catalog {
        @XmlElement(name = "book")
        @JacksonXmlProperty(localName = "book")
        @JacksonXmlElementWrapper(useWrapping = false)
        public List<Book> books = new ArrayList<>();
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlAccessorFactory(CatalogAccessorFactory.class)
    public static class Book {
        @XmlAttribute
        @JacksonXmlProperty(isAttribute = true, localName = "id")
        public int id;
        @XmlValue
        @JacksonXmlText
        public String title;
    }
}
