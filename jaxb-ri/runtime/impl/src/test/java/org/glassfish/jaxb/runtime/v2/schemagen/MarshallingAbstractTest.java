/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.schemagen;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.JaxbConcreteContainer;
import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.JaxbConcreteDeployment;
import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.JaxbContainer;
import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.JaxbDistribution;
import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.JaxbEnvironmentModel;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class MarshallingAbstractTest {

    @XmlSeeAlso({B.class, C.class})
    abstract static class A {
        //marshal/unmarshal of list elements containing elements of type = A will work without @XmlValue annotation...
        @XmlValue
        protected String value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ClassType1")
    public static class B extends A {
        public B() {}
        public B(String value) {
            this.value = value;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "ClassType2")
    public static class C extends A {
        public C() {}
        public C(String value) {
            this.value = value;
        }
    }

    @XmlRootElement(name="root")
    @XmlAccessorType(XmlAccessType.FIELD)
    static class Mapping {
        @XmlElementWrapper(name = "list")
        @XmlElement(name="element")
        Collection<A> list = new ArrayList<>();
        A element1;
        A element2;
    }

    @Test
    public void testMarshalSingleElement() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Mapping.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter resultWriter = new StringWriter();
        Mapping mapping = new Mapping();
        mapping.element1 = new B("B1");
        mapping.element2 = new C("C1");
        marshaller.marshal(mapping, resultWriter);

        String expectedXml1 = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <root>
                    <list/>
                    <element1 xsi:type="ClassType1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">B1</element1>
                    <element2 xsi:type="ClassType2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">C1</element2>
                </root>
                """;
        assertEquals(expectedXml1, resultWriter.toString());
    }

    @Test
    public void testMarshalCollection() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Mapping.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter resultWriter = new StringWriter();
        Mapping mapping = new Mapping();
        mapping.element1 = new B("B1");
        mapping.element2 = new C("C1");
        mapping.list.add(new B("B"));
        mapping.list.add(new C("C"));
        String expectedXml2 = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <root>
                    <list>
                        <element xsi:type="ClassType1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">B</element>
                        <element xsi:type="ClassType2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">C</element>
                    </list>
                    <element1 xsi:type="ClassType1" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">B1</element1>
                    <element2 xsi:type="ClassType2" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">C1</element2>
                </root>
                """;
        marshaller.marshal(mapping, resultWriter);
        assertEquals(expectedXml2, resultWriter.toString());
    }

    @Test
    public void testUnmarshalSingleElement() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Mapping.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        //works without list..
        String sourceXml1 = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <root>
                    <element1 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ClassType1">B1</element1>
                    <element2 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ClassType2">C1</element2>
                </root>
                """;
        JAXBElement<Mapping> element = unmarshaller.unmarshal(new StreamSource(new StringReader(sourceXml1)), Mapping.class);
        assertNotNull(element.getValue());
        assertEquals(B.class, element.getValue().element1.getClass());
        assertEquals(C.class, element.getValue().element2.getClass());
    }

    @Test
    public void testUnmarshalCollection() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Mapping.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        //don't work -> try to instantiate the abstract class
        String sourceXml2 = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <root>
                    <list>
                        <element xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ClassType1">B</element>
                        <element xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ClassType2">C</element>
                    </list>
                    <element1 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ClassType1">B1</element1>
                    <element2 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ClassType2">C1</element2>
                </root>
                """;

        try {
            JAXBElement<Mapping> element = unmarshaller.unmarshal(new StreamSource(new StringReader(sourceXml2)), Mapping.class);
            assertEquals(2, element.getValue().list.size());
        }
        catch (Throwable e) {
            fail();
        }
    }

    @Test
    public void testXmlIDRefMarshal() throws Exception {

        JAXBContext cont = JAXBContext.newInstance(JaxbEnvironmentModel.class);
        JaxbEnvironmentModel envModel = new JaxbEnvironmentModel();
        JaxbDistribution dist = new JaxbDistribution();
        JaxbConcreteDeployment dep = new JaxbConcreteDeployment();
        dep.setContextRoot("Context-Root");
        dist.addDeployment(dep);
        envModel.setDistribution(dist);
        JaxbContainer container = new JaxbConcreteContainer();
        container.addDeployment(dep);
        envModel.setContainer(container);

        String sourceXsd = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">
                
                  <xs:element name="environmentModel" type="environmentModelType"/>
                
                  <xs:complexType name="environmentModelType">
                    <xs:sequence>
                      <xs:element name="container" type="containerType" minOccurs="0"/>
                      <xs:element name="distribution" type="distributionType" minOccurs="0"/>
                    </xs:sequence>
                  </xs:complexType>
                
                  <xs:complexType name="containerType" abstract="true">
                    <xs:sequence>
                      <xs:element name="deployments">
                        <xs:complexType>
                          <xs:sequence>
                            <xs:element name="deployment" type="xs:IDREF" minOccurs="0" maxOccurs="unbounded"/>
                          </xs:sequence>
                        </xs:complexType>
                      </xs:element>
                    </xs:sequence>
                  </xs:complexType>
                
                  <xs:complexType name="deploymentType" abstract="true">
                    <xs:sequence>
                      <xs:element name="contextRoot" type="xs:ID"/>
                    </xs:sequence>
                  </xs:complexType>
                
                  <xs:complexType name="concreteDeploymentType">
                    <xs:complexContent>
                      <xs:extension base="deploymentType">
                        <xs:sequence/>
                      </xs:extension>
                    </xs:complexContent>
                  </xs:complexType>
                
                  <xs:complexType name="concreteContainerType">
                    <xs:complexContent>
                      <xs:extension base="containerType">
                        <xs:sequence/>
                      </xs:extension>
                    </xs:complexContent>
                  </xs:complexType>
                
                  <xs:complexType name="distributionType">
                    <xs:sequence>
                      <xs:element name="deployments">
                        <xs:complexType>
                          <xs:sequence>
                            <xs:element name="deployment" type="deploymentType" minOccurs="0" maxOccurs="unbounded"/>
                          </xs:sequence>
                        </xs:complexType>
                      </xs:element>
                    </xs:sequence>
                  </xs:complexType>
                
                  <xs:complexType name="mainTest">
                    <xs:sequence/>
                  </xs:complexType>
                </xs:schema>""";
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new StreamSource(new StringReader(sourceXsd)));

        Marshaller marshal = cont.createMarshaller();
        marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshal.setSchema(schema);
        StringWriter resultWriter = new StringWriter();
        marshal.marshal(envModel, resultWriter);

        String expectedXml = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <environmentModel>
                    <container xsi:type="concreteContainerType" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                        <deployments>
                            <deployment>Context-Root</deployment>
                        </deployments>
                    </container>
                    <distribution>
                        <deployments>
                            <deployment xsi:type="concreteDeploymentType" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                                <contextRoot>Context-Root</contextRoot>
                            </deployment>
                        </deployments>
                    </distribution>
                </environmentModel>
                """;

        assertEquals(expectedXml, resultWriter.toString());
    }

}
