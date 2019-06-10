/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.schemagen;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.sun.xml.bind.v2.schemagen.xmlidref.JaxbConcreteContainer;
import com.sun.xml.bind.v2.schemagen.xmlidref.JaxbConcreteDeployment;
import com.sun.xml.bind.v2.schemagen.xmlidref.JaxbContainer;
import com.sun.xml.bind.v2.schemagen.xmlidref.JaxbDistribution;
import com.sun.xml.bind.v2.schemagen.xmlidref.JaxbEnvironmentModel;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import junit.textui.TestRunner;

public class MarshallingAbstractTest extends TestCase {
    public static void main(String[] args) {
        TestRunner.run(MarshallingAbstractTest.class);
    }

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
        Collection<A> list = new ArrayList<A>();
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

        String expectedXml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<root>\n"
                + "    <list/>\n"
                + "    <element1 xsi:type=\"ClassType1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">B1</element1>\n"
                + "    <element2 xsi:type=\"ClassType2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">C1</element2>\n"
                + "</root>\n";
        Assert.assertEquals(resultWriter.toString(), expectedXml1);
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
        String expectedXml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<root>\n"
                + "    <list>\n"
                + "        <element xsi:type=\"ClassType1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">B</element>\n"
                + "        <element xsi:type=\"ClassType2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">C</element>\n"
                + "    </list>\n"
                + "    <element1 xsi:type=\"ClassType1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">B1</element1>\n"
                + "    <element2 xsi:type=\"ClassType2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">C1</element2>\n"
                + "</root>\n";
        marshaller.marshal(mapping, resultWriter);
        Assert.assertEquals(resultWriter.toString(), expectedXml2);
    }

    @Test
    public void testUnmarshalSingleElement() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Mapping.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        //works without list..
        String sourceXml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<root>\n"
                + "    <element1 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ClassType1\">B1</element1>\n"
                + "    <element2 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ClassType2\">C1</element2>\n"
                + "</root>\n";
        JAXBElement<Mapping> element = unmarshaller.unmarshal(new StreamSource(new StringReader(sourceXml1)), Mapping.class);
        Assert.assertNotNull(element.getValue());
        Assert.assertEquals(B.class, element.getValue().element1.getClass());
        Assert.assertEquals(C.class, element.getValue().element2.getClass());
    }

    @Test
    public void testUnmarshalCollection() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Mapping.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        //don't work -> try to instantiate the abstract class
        String sourceXml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<root>\n"
                + "    <list>\n"
                + "        <element xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ClassType1\">B</element>\n"
                + "        <element xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ClassType2\">C</element>\n"
                + "    </list>\n"
                + "    <element1 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ClassType1\">B1</element1>\n"
                + "    <element2 xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ClassType2\">C1</element2>\n"
                + "</root>\n";

        try {
            JAXBElement<Mapping> element = unmarshaller.unmarshal(new StreamSource(new StringReader(sourceXml2)), Mapping.class);
            Assert.assertEquals(2, element.getValue().list.size());
        }
        catch (Throwable e) {
            Assert.fail();
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

        String sourceXsd = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<xs:schema version=\"1.0\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "\n" +
                "  <xs:element name=\"environmentModel\" type=\"environmentModelType\"/>\n" +
                "\n" +
                "  <xs:complexType name=\"environmentModelType\">\n" +
                "    <xs:sequence>\n" +
                "      <xs:element name=\"container\" type=\"containerType\" minOccurs=\"0\"/>\n" +
                "      <xs:element name=\"distribution\" type=\"distributionType\" minOccurs=\"0\"/>\n" +
                "    </xs:sequence>\n" +
                "  </xs:complexType>\n" +
                "\n" +
                "  <xs:complexType name=\"containerType\" abstract=\"true\">\n" +
                "    <xs:sequence>\n" +
                "      <xs:element name=\"deployments\">\n" +
                "        <xs:complexType>\n" +
                "          <xs:sequence>\n" +
                "            <xs:element name=\"deployment\" type=\"xs:IDREF\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
                "          </xs:sequence>\n" +
                "        </xs:complexType>\n" +
                "      </xs:element>\n" +
                "    </xs:sequence>\n" +
                "  </xs:complexType>\n" +
                "\n" +
                "  <xs:complexType name=\"deploymentType\" abstract=\"true\">\n" +
                "    <xs:sequence>\n" +
                "      <xs:element name=\"contextRoot\" type=\"xs:ID\"/>\n" +
                "    </xs:sequence>\n" +
                "  </xs:complexType>\n" +
                "\n" +
                "  <xs:complexType name=\"concreteDeploymentType\">\n" +
                "    <xs:complexContent>\n" +
                "      <xs:extension base=\"deploymentType\">\n" +
                "        <xs:sequence/>\n" +
                "      </xs:extension>\n" +
                "    </xs:complexContent>\n" +
                "  </xs:complexType>\n" +
                "\n" +
                "  <xs:complexType name=\"concreteContainerType\">\n" +
                "    <xs:complexContent>\n" +
                "      <xs:extension base=\"containerType\">\n" +
                "        <xs:sequence/>\n" +
                "      </xs:extension>\n" +
                "    </xs:complexContent>\n" +
                "  </xs:complexType>\n" +
                "\n" +
                "  <xs:complexType name=\"distributionType\">\n" +
                "    <xs:sequence>\n" +
                "      <xs:element name=\"deployments\">\n" +
                "        <xs:complexType>\n" +
                "          <xs:sequence>\n" +
                "            <xs:element name=\"deployment\" type=\"deploymentType\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\n" +
                "          </xs:sequence>\n" +
                "        </xs:complexType>\n" +
                "      </xs:element>\n" +
                "    </xs:sequence>\n" +
                "  </xs:complexType>\n" +
                "\n" +
                "  <xs:complexType name=\"mainTest\">\n" +
                "    <xs:sequence/>\n" +
                "  </xs:complexType>\n" +
                "</xs:schema>";
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = sf.newSchema(new StreamSource(new StringReader(sourceXsd)));

        Marshaller marshal = cont.createMarshaller();
        marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshal.setSchema(schema);
        StringWriter resultWriter = new StringWriter();
        marshal.marshal(envModel, resultWriter);

        String expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<environmentModel>\n" +
                "    <container xsi:type=\"concreteContainerType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <deployments>\n" +
                "            <deployment>Context-Root</deployment>\n" +
                "        </deployments>\n" +
                "    </container>\n" +
                "    <distribution>\n" +
                "        <deployments>\n" +
                "            <deployment xsi:type=\"concreteDeploymentType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "                <contextRoot>Context-Root</contextRoot>\n" +
                "            </deployment>\n" +
                "        </deployments>\n" +
                "    </distribution>\n" +
                "</environmentModel>\n";

        Assert.assertEquals(resultWriter.toString(), expectedXml);
    }

}