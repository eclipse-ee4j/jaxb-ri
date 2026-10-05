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
import com.ctc.wstx.sax.WstxSAXParserFactory;
import com.ctc.wstx.stax.WstxInputFactory;
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
import tools.jackson.dataformat.xml.XmlFactory;
import tools.jackson.dataformat.xml.XmlMapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import tools.jackson.dataformat.xml.annotation.JacksonXmlText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.sax.SAXSource;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/** End-to-end JAXB/Jackson throughput over the same verified object graph. */
@State(Scope.Thread)
public class EndToEndBenchmark {
    @Param({"4", "32", "256"})
    public int books;

    private Marshaller reflectionMarshaller;
    private Marshaller handleMarshaller;
    private Unmarshaller reflectionUnmarshaller;
    private Unmarshaller handleUnmarshaller;
    private Unmarshaller parserJdkStaxUnmarshaller;
    private Unmarshaller parserWoodstoxStaxUnmarshaller;
    private Unmarshaller parserWoodstoxSaxUnmarshaller;
    private XMLInputFactory parserJdkStaxFactory;
    private XMLInputFactory parserWoodstoxFactory;
    private XMLReader parserWoodstoxSaxReader;
    private XmlMapper parserWoodstoxJackson;
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

        parserJdkStaxUnmarshaller = handleContext.createUnmarshaller();
        parserWoodstoxStaxUnmarshaller = handleContext.createUnmarshaller();
        parserWoodstoxSaxUnmarshaller = handleContext.createUnmarshaller();
        parserJdkStaxFactory = secureStaxFactory(XMLInputFactory.newDefaultFactory());
        WstxInputFactory woodstoxFactory = new WstxInputFactory();
        parserWoodstoxFactory = secureStaxFactory(woodstoxFactory);
        WstxSAXParserFactory woodstoxSaxFactory = new WstxSAXParserFactory(woodstoxFactory);
        woodstoxSaxFactory.setNamespaceAware(true);
        woodstoxSaxFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        parserWoodstoxSaxReader = woodstoxSaxFactory.newSAXParser().getXMLReader();
        // Supplying a factory bypasses Jackson's default factory hardening, so both
        // databinders deliberately share this explicitly secured Woodstox factory.
        parserWoodstoxJackson = new XmlMapper(new XmlFactory(parserWoodstoxFactory));

        catalog = fixture(books);
        output.reset();
        handleMarshaller.marshal(catalog, output);
        xml = output.toByteArray();
        verifyGraph((Catalog) reflectionUnmarshaller.unmarshal(new ByteArrayInputStream(xml)));
        verifyGraph((Catalog) handleUnmarshaller.unmarshal(new ByteArrayInputStream(xml)));
        verifyGraph(jackson.readValue(xml, Catalog.class));
        verifyGraph((Catalog) unmarshalStax(parserJdkStaxUnmarshaller, parserJdkStaxFactory));
        verifyGraph((Catalog) unmarshalStax(parserWoodstoxStaxUnmarshaller, parserWoodstoxFactory));
        verifyGraph((Catalog) parserWoodstoxSaxUnmarshaller.unmarshal(new SAXSource(
                parserWoodstoxSaxReader, new InputSource(new ByteArrayInputStream(xml)))));
        verifyGraph(parserWoodstoxJackson.readValue(xml, Catalog.class));

        XMLReader defaultSaxReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        System.err.printf("Parser providers: JAXP SAX=%s / %s; JDK StAX=%s; Woodstox StAX=%s; Woodstox SAX=%s%n",
                SAXParserFactory.newInstance().getClass().getName(), defaultSaxReader.getClass().getName(),
                parserJdkStaxFactory.getClass().getName(),
                parserWoodstoxFactory.getClass().getName(), parserWoodstoxSaxReader.getClass().getName());

        output.reset();
        jackson.writeValue(output, catalog);
        verifyGraph((Catalog) handleUnmarshaller.unmarshal(new ByteArrayInputStream(output.toByteArray())));
    }

    private static JAXBRIContext context() throws JAXBException {
        return JAXBRIContext.newInstance(new Class<?>[]{Catalog.class, Book.class}, null,
                null, null, false, null, true, false, false, false);
    }

    private static XMLInputFactory secureStaxFactory(XMLInputFactory factory) {
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
        return factory;
    }

    private XMLStreamReader newReader(XMLInputFactory factory) throws XMLStreamException {
        return factory.createXMLStreamReader(new ByteArrayInputStream(xml));
    }

    private Object unmarshalStax(Unmarshaller unmarshaller, XMLInputFactory factory) throws Exception {
        XMLStreamReader reader = newReader(factory);
        try {
            return unmarshaller.unmarshal(reader);
        } finally {
            reader.close();
        }
    }

    private Object unmarshalWoodstoxSax() throws Exception {
        SAXSource source = new SAXSource(parserWoodstoxSaxReader,
                new InputSource(new ByteArrayInputStream(xml)));
        return parserWoodstoxSaxUnmarshaller.unmarshal(source);
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

    @Benchmark public Object unmarshalParserSaxDefault() throws Exception {
        return handleUnmarshaller.unmarshal(new ByteArrayInputStream(xml));
    }

    @Benchmark public Object unmarshalParserStaxJdk() throws Exception {
        return unmarshalStax(parserJdkStaxUnmarshaller, parserJdkStaxFactory);
    }

    @Benchmark public Object unmarshalParserStaxWoodstox() throws Exception {
        return unmarshalStax(parserWoodstoxStaxUnmarshaller, parserWoodstoxFactory);
    }

    @Benchmark public Object unmarshalParserSaxWoodstox() throws Exception {
        return unmarshalWoodstoxSax();
    }

    @Benchmark public Object unmarshalParserJacksonWoodstox() throws Exception {
        return parserWoodstoxJackson.readValue(xml, Catalog.class);
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
