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
import org.glassfish.jaxb.runtime.AccessorFactory;
import org.glassfish.jaxb.runtime.AccessorFactoryImpl;
import org.glassfish.jaxb.runtime.XmlAccessorFactory;
import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** End-to-end JAXB throughput with identical models and only Accessor strategy changed. */
@State(Scope.Thread)
public class EndToEndBenchmark {
    private Marshaller reflectionMarshaller;
    private Marshaller handleMarshaller;
    private Unmarshaller reflectionUnmarshaller;
    private Unmarshaller handleUnmarshaller;
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

        catalog = new Catalog();
        catalog.setName("benchmark catalog");
        for (int i = 0; i < 64; i++) {
            Book book = new Book();
            book.setIsbn("978-" + i);
            book.setTitle("A JAXB benchmark title " + i);
            book.setAuthor("Author " + (i % 8));
            book.setPrice(12.5 + i);
            catalog.getBooks().add(book);
        }
        output.reset();
        handleMarshaller.marshal(catalog, output);
        xml = output.toByteArray();
        Object fromReflection = reflectionUnmarshaller.unmarshal(new ByteArrayInputStream(xml));
        Object fromHandles = handleUnmarshaller.unmarshal(new ByteArrayInputStream(xml));
        if (!same((Catalog) fromReflection, catalog) || !same((Catalog) fromHandles, catalog)) {
            throw new IllegalStateException("End-to-end JAXB implementations disagree");
        }
    }

    private static JAXBRIContext context() throws JAXBException {
        return JAXBRIContext.newInstance(new Class<?>[]{Catalog.class, Book.class}, null,
                null, null, false, null, true, false, false, false);
    }

    private static boolean same(Catalog a, Catalog b) {
        if (!a.getName().equals(b.getName()) || a.getBooks().size() != b.getBooks().size()) return false;
        for (int i = 0; i < a.getBooks().size(); i++) {
            Book x = a.getBooks().get(i), y = b.getBooks().get(i);
            if (!x.getIsbn().equals(y.getIsbn()) || !x.getTitle().equals(y.getTitle())
                    || !x.getAuthor().equals(y.getAuthor()) || x.getPrice() != y.getPrice()) return false;
        }
        return true;
    }

    @Benchmark public void marshalReflection() throws Exception {
        output.reset();
        reflectionMarshaller.marshal(catalog, output);
    }

    @Benchmark public void marshalHandles() throws Exception {
        output.reset();
        handleMarshaller.marshal(catalog, output);
    }

    @Benchmark public Object unmarshalReflection() throws Exception {
        return reflectionUnmarshaller.unmarshal(new ByteArrayInputStream(xml));
    }

    @Benchmark public Object unmarshalHandles() throws Exception {
        return handleUnmarshaller.unmarshal(new ByteArrayInputStream(xml));
    }

    /** Test-only switch selected while JAXB builds each context's immutable model. */
    public static final class CatalogAccessorFactory implements AccessorFactory {
        static volatile boolean useHandles;
        private final AccessorFactoryImpl handles = AccessorFactoryImpl.getInstance();

        @Override public Accessor createFieldAccessor(Class bean, Field field, boolean readOnly) throws JAXBException {
            if (useHandles) return handles.createFieldAccessor(bean, field, readOnly);
            return readOnly ? new Accessor.ReadOnlyFieldReflection(field) : new Accessor.FieldReflection(field);
        }

        @Override public Accessor createPropertyAccessor(Class bean, Method getter, Method setter) throws JAXBException {
            if (useHandles) return handles.createPropertyAccessor(bean, getter, setter);
            if (getter == null) return new Accessor.SetterOnlyReflection(setter);
            if (setter == null) return new Accessor.GetterOnlyReflection(getter);
            return new Accessor.GetterSetterReflection(getter, setter);
        }
    }

    @XmlRootElement @XmlAccessorType(XmlAccessType.PROPERTY) @XmlAccessorFactory(CatalogAccessorFactory.class)
    public static class Catalog {
        private String name;
        private List<Book> books = new ArrayList<>();
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        @XmlElement public List<Book> getBooks() { return books; }
        public void setBooks(List<Book> books) { this.books = books; }
    }

    @XmlAccessorType(XmlAccessType.PROPERTY) @XmlAccessorFactory(CatalogAccessorFactory.class)
    public static class Book {
        private String isbn, title, author;
        private double price;
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }
}
