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
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Primitive fields bound through the built-in conversions must unmarshal, report errors and
 * marshal exactly like the same properties bound through getters and setters, which always use
 * the generic (boxing) accessor.
 */
public class PrimitiveFieldBindingTest {

    @XmlRootElement(name = "r")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Fields {
        @XmlAttribute public int attrInt;
        @XmlAttribute public boolean attrZ;
        @XmlElement public int i;
        @XmlElement public long l;
        @XmlElement public boolean z;
        @XmlElement public double d;
        @XmlElement public float f;
        @XmlElement public short s;
        @XmlElement public byte b;
        @XmlElement @XmlSchemaType(name = "unsignedShort") public int us;
        @XmlElement(defaultValue = "42") public int dv;
        @XmlElement public Value v;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Value {
        @XmlAttribute public long id;
        @XmlValue public int value;
    }

    @XmlRootElement(name = "r")
    @XmlAccessorType(XmlAccessType.PROPERTY)
    @XmlType(propOrder = {"i", "l", "z", "d", "f", "s", "b", "us", "dv", "v"})
    public static class Props {
        private int attrInt, i, us, dv; private boolean attrZ, z; private long l; private double d; private float f;
        private short s; private byte b; private PValue v;
        @XmlAttribute public int getAttrInt() { return attrInt; } public void setAttrInt(int x) { attrInt = x; }
        @XmlAttribute public boolean isAttrZ() { return attrZ; } public void setAttrZ(boolean x) { attrZ = x; }
        @XmlElement public int getI() { return i; } public void setI(int x) { i = x; }
        @XmlElement public long getL() { return l; } public void setL(long x) { l = x; }
        @XmlElement public boolean isZ() { return z; } public void setZ(boolean x) { z = x; }
        @XmlElement public double getD() { return d; } public void setD(double x) { d = x; }
        @XmlElement public float getF() { return f; } public void setF(float x) { f = x; }
        @XmlElement public short getS() { return s; } public void setS(short x) { s = x; }
        @XmlElement public byte getB() { return b; } public void setB(byte x) { b = x; }
        @XmlElement @XmlSchemaType(name = "unsignedShort") public int getUs() { return us; } public void setUs(int x) { us = x; }
        @XmlElement(defaultValue = "42") public int getDv() { return dv; } public void setDv(int x) { dv = x; }
        @XmlElement public PValue getV() { return v; } public void setV(PValue x) { v = x; }
    }

    @XmlAccessorType(XmlAccessType.PROPERTY)
    public static class PValue {
        private long id; private int value;
        @XmlAttribute public long getId() { return id; } public void setId(long x) { id = x; }
        @XmlValue public int getValue() { return value; } public void setValue(int x) { value = x; }
    }

    private static final String[] DOCS = {
        "<r attrInt='-1' attrZ='true'><i>2147483647</i><l>-9223372036854775808</l><z>1</z><d>-1.5E-7</d><f>INF</f>"
                + "<s>-32768</s><b>-128</b><us>65535</us><dv/><v id='9'> 77 </v></r>",
        "<r attrInt=' +3 ' attrZ='0'><i> 5 </i><l>0</l><z>false</z><d>NaN</d><f>0.1</f><s>1</s><b>1</b><us>1</us><v id='1'>-0</v></r>",
        // invalid lexical forms: must be reported (or tolerated) the same way
        "<r attrInt='x'><i>1</i></r>",
        "<r><i>12a</i><l>1.5</l><z>maybe</z><d>abc</d><f>1f</f><s>bad</s><b>1</b><v id='2'>q</v></r>",
        "<r><v id='x'>1</v></r>",
    };

    private static String describe(Object o) {
        if (o instanceof Fields x)
            return x.attrInt + "|" + x.attrZ + "|" + x.i + "|" + x.l + "|" + x.z + "|" + x.d + "|" + x.f + "|" + x.s + "|" + x.b
                    + "|" + x.us + "|" + x.dv + "|" + (x.v == null ? null : x.v.id + "/" + x.v.value);
        Props x = (Props) o;
        return x.getAttrInt() + "|" + x.isAttrZ() + "|" + x.getI() + "|" + x.getL() + "|" + x.isZ() + "|" + x.getD() + "|" + x.getF()
                + "|" + x.getS() + "|" + x.getB() + "|" + x.getUs() + "|" + x.getDv()
                + "|" + (x.getV() == null ? null : x.getV().getId() + "/" + x.getV().getValue());
    }

    private static String run(Class<?> type, String doc) throws Exception {
        JAXBContext context = JAXBContext.newInstance(type);
        Unmarshaller u = context.createUnmarshaller();
        List<String> events = new ArrayList<>();
        u.setEventHandler(e -> {
            events.add(e.getSeverity() + ":" + e.getMessage() + "@" + e.getLocator().getLineNumber()
                    + ":" + e.getLocator().getColumnNumber());
            return e.getSeverity() != ValidationEvent.FATAL_ERROR;
        });
        Object o;
        try {
            o = u.unmarshal(new StringReader(doc));
        } catch (Exception e) {
            // the outcome, including the exception, must be the same for both shapes
            Throwable t = e;
            while (t.getCause() != null) t = t.getCause();
            return events + "\n" + e.getClass().getName() + ": " + t.getClass().getName() + ": " + t.getMessage();
        }
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter w = new StringWriter();
        m.marshal(o, w);
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        m.marshal(o, bytes);
        return describe(o) + "\n" + events + "\n" + w + "\n" + bytes.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Test
    public void fieldsBehaveLikeProperties() throws Exception {
        for (String doc : DOCS) {
            assertEquals(run(Props.class, doc), run(Fields.class, doc), doc);
        }
    }
}
