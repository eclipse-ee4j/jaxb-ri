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

import java.io.StringWriter;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Marshalling of {@link XMLGregorianCalendar} values with an explicit schema type
 * and a negative year (<a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/1191">#1191</a>).
 */
public class XMLGregorianCalendarNegativeYearTest {

    @XmlRootElement(name = "dates")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Dates {

        @XmlSchemaType(name = "dateTime")
        XMLGregorianCalendar dateTime;

        @XmlSchemaType(name = "date")
        XMLGregorianCalendar date;

        @XmlSchemaType(name = "gYear")
        XMLGregorianCalendar year;
    }

    @Test
    public void negativeYearsKeepTheirSignAndPadding() throws Exception {
        assertEquals("<dates>"
                        + "<dateTime>-0001-12-31T23:59:59.999Z</dateTime>"
                        + "<date>-0044-03-15</date>"
                        + "<year>-12345</year>"
                        + "</dates>",
                marshal("-0001-12-31T23:59:59.999Z", "-0044-03-15", "-12345"));
    }

    @Test
    public void positiveYearsAreUnchanged() throws Exception {
        assertEquals("<dates>"
                        + "<dateTime>0001-01-01T00:00:00Z</dateTime>"
                        + "<date>2026-10-15</date>"
                        + "<year>12345</year>"
                        + "</dates>",
                marshal("0001-01-01T00:00:00Z", "2026-10-15", "12345"));
    }

    private static String marshal(String dateTime, String date, String year) throws Exception {
        DatatypeFactory factory = DatatypeFactory.newInstance();
        Dates dates = new Dates();
        dates.dateTime = factory.newXMLGregorianCalendar(dateTime);
        dates.date = factory.newXMLGregorianCalendar(date);
        dates.year = factory.newXMLGregorianCalendar(year);

        Marshaller marshaller = JAXBContext.newInstance(Dates.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(dates, writer);
        return writer.toString();
    }
}
