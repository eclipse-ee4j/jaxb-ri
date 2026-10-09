/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.glassfish.jaxb.runtime.v2.runtime.property.Property;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLInputFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Single-valued primitive element properties are bound as leaf properties. The expected values
 * below are what the element node path, used for them before, produces: including the reset to
 * the default value after a recovered conversion error, the handling of empty elements, xsi:nil
 * and xsi:type, and the reported events.
 */
public class PrimitiveElementTest {

    private static final String XSI = "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'";

    @XmlRootElement(name = "r")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class R {
        public int i = 5;
        public double d = 2.5;
        @XmlElement(nillable = true) public int n = 7;
        public boolean z = true;
        @XmlElement(defaultValue = "11") public long dl = 1;
        public int[] arr;
    }

    private static String run(String doc, boolean stax) throws Exception {
        JAXBContext c = JAXBContext.newInstance(R.class);
        Unmarshaller u = c.createUnmarshaller();
        List<String> events = new ArrayList<>();
        u.setEventHandler(e -> {
            events.add(e.getMessage() + "@" + e.getLocator().getLineNumber() + ":" + e.getLocator().getColumnNumber());
            return true;
        });
        R r = (R) (stax
                ? u.unmarshal(XMLInputFactory.newFactory().createXMLStreamReader(new StringReader(doc)))
                : u.unmarshal(new StringReader(doc)));
        Marshaller m = c.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter w = new StringWriter();
        m.marshal(r, w);
        return r.i + " " + r.d + " " + r.n + " " + r.z + " " + r.dl + " " + (stax ? "" : events + " ") + w;
    }

    @Test
    public void primitiveElementsAreLeafProperties() throws Exception {
        JAXBContextImpl c = (JAXBContextImpl) JAXBContext.newInstance(R.class);
        ClassBeanInfoImpl<?> bi = (ClassBeanInfoImpl<?>) c.getBeanInfo(R.class);
        int leaves = 0;
        for (Property<?> p : bi.properties)
            if (p.getClass().getSimpleName().equals("SingleElementLeafProperty")) leaves++;
        assertEquals(5, leaves);
    }

    @Test
    public void sameOutcomeAsTheNodePath() throws Exception {
        for (boolean stax : new boolean[] {false, true}) {
            assertEquals("0 0.0 0 false 1 " + (stax ? "" : "[Not a number: abc@1:14, x@1:22] ")
                            + "<r><i>0</i><d>0.0</d><n>0</n><z>false</z><dl>1</dl></r>",
                    run("<r><i>abc</i><d>x</d><n xsi:nil='true' " + XSI + "/><z>maybe</z></r>", stax));
            assertEquals("4 2.5 7 true 1 " + (stax ? "" : "[] ") + "<r><i>4</i><d>2.5</d><n>7</n><z>true</z><dl>1</dl></r>",
                    run("<r><i xsi:type='xs:short' xmlns:xs='http://www.w3.org/2001/XMLSchema' " + XSI + ">3</i><i>4</i></r>", stax));
            assertEquals("0 0.0 7 true 11 " + (stax ? "" : "[@1:15] ") + "<r><i>0</i><d>0.0</d><n>7</n><z>true</z><dl>11</dl></r>",
                    run("<r><i/><d></d><dl/></r>", stax));
            assertEquals("9 2.5 7 true 1 " + (stax ? "" : "[] ") + "<r><i>9</i><d>2.5</d><n>7</n><z>true</z><dl>1</dl></r>",
                    run("<r><i xsi:nil='true' " + XSI + ">9</i></r>", stax));
            assertTrue(run("<r><i> -12 </i><d> 1e3 </d><z> 1 </z><dl>-9223372036854775808</dl></r>", stax)
                    .startsWith("-12 1000.0 7 true -9223372036854775808 "));
        }
    }
}
