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
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallingContext;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * The active {@link Coordinator} is published through a thread local for the duration of each
 * unmarshalling / marshalling callback. These tests pin down the observable contract: the
 * coordinator is visible inside callbacks, nested episodes restore the outer one, and nothing
 * is left visible on the thread afterwards.
 */
public class CoordinatorTest {

    @XmlRootElement(name = "item")
    public static class Item {
        @XmlAttribute
        public int id;
        @XmlElement
        public List<Item> child = new ArrayList<>();
    }

    @Test
    public void noCoordinatorIsVisibleAfterUnmarshalAndMarshal() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Item.class);
        Item item = (Item) context.createUnmarshaller().unmarshal(
                new StringReader("<item id='1'><child id='2'/><child id='3'/></item>"));
        assertEquals(2, item.child.size());
        assertNull(Coordinator._getInstance());

        context.createMarshaller().marshal(item, new ByteArrayOutputStream());
        assertNull(Coordinator._getInstance());
    }

    @Test
    public void nestedUnmarshalRestoresTheOuterCoordinator() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Item.class);
        Unmarshaller outer = context.createUnmarshaller();
        Unmarshaller inner = context.createUnmarshaller();
        List<String> seen = new ArrayList<>();

        outer.setListener(new Unmarshaller.Listener() {
            @Override
            public void afterUnmarshal(Object target, Object parent) {
                UnmarshallingContext before = UnmarshallingContext.getInstance();
                if (((Item) target).id == 2) {
                    try {
                        Item nested = (Item) inner.unmarshal(new StringReader("<item id='9'/>"));
                        seen.add("nested:" + nested.id);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
                // the outer episode must see its own context again after the nested one
                assertSame(before, UnmarshallingContext.getInstance());
                seen.add("after:" + ((Item) target).id);
            }
        });

        Item item = (Item) outer.unmarshal(
                new StringReader("<item id='1'><child id='2'/><child id='3'/></item>"));

        assertEquals(List.of("nested:9", "after:2", "after:3", "after:1"), seen);
        assertEquals(3, item.child.get(1).id);
        assertNull(Coordinator._getInstance());
    }

    @Test
    public void coordinatorIsNotLeakedAfterAFailedUnmarshal() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Item.class);
        try {
            context.createUnmarshaller().unmarshal(new StringReader("<item id='1'><child id='2'></item>"));
        } catch (Exception expected) {
            // malformed document
        }
        assertNull(Coordinator._getInstance());
    }
}
