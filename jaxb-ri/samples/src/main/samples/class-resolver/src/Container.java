/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import com.sun.xml.bind.IDResolver;
import com.sun.xml.bind.api.ClassResolver;

/**
 * Miniture DI container.
 *
 * @author Kohsuke Kawaguchi
 */
@XmlRootElement
public class Container {
    @XmlElement(name="value")
    private List<Value> values = new ArrayList<Value>();

    private static class Value {
        /**
         * ID to identify {@link #value}.
         */
        @XmlAttribute(required=true)
        @XmlID
        private String id;

        /**
         * This annotation causes JAXB to trigger {@link ClassResolver}
         * on this field.
         */
        @XmlAnyElement(lax=true)
        private Object value;
    }

    public static Container load(File file) throws JAXBException {
        Unmarshaller u = CONTEXT.createUnmarshaller();
        // register ClassResolver
        u.setProperty(ClassResolver.class.getName(), new ClassResolverImpl());
        u.setProperty(IDResolver.class.getName(), new IDResolverImpl());
        return (Container)u.unmarshal(file);
    }

    /**
     * Informs JAXB lazily to use such and such class for unmarshalling.
     */
    static final class ClassResolverImpl extends ClassResolver {
        public Class<?> resolveElementName(String nsUri, String localName) throws Exception {
            // assume that element names look like
            // <p:ClassName xmlns:p="java:package.name">
            // and try to load that class.
            if(nsUri.startsWith("java:")) {
                String className = nsUri.substring(5)+'.'+localName;
                // if an exception is thrown from here, it will be passed to
                // ValidationEventHandler
                return Class.forName(className);
            }

            // returning null means 'I have no clue about this element'
            return null;
        }
    }

    /**
     * Notice that this example places the ID attribute on {@link Value},
     * not on the bean object ({@link Value#value}.) So we use
     * a custom {@link IDResolver} so that {@link XmlIDREF} resolves
     * into the {@link Value#value}.
     */
    static final class IDResolverImpl extends IDResolver {
        private final Map<String,Object> table = new HashMap<String,Object>();
        public void bind(String id, Object obj) {
            table.put(id,obj);
        }

        public Callable<?> resolve(final String id, Class targetType) {
            return new Callable<Object>() {
                public Object call() throws Exception {
                    // if IDREF resolves to a Value object,
                    // use the inner value
                    Object o = table.get(id);
                    if(o instanceof Value) {
                        return ((Value)o).value;
                    }
                    return o;
                }
            };
        }
    }


    /**
     * Gets the object for the ID.
     */
    public Object get(String id) {
        for (Value v : values) {
            if(v.id.equals(id))
                return v.value;
        }
        return null;
    }

    /**
     * Note that this {@link JAXBContext} only knows about
     * {@link Container}.
     */
    private static final JAXBContext CONTEXT;

    static {
        try {
            CONTEXT = JAXBContext.newInstance(Container.class);
        } catch (JAXBException e) {
            // this is a deployment error
            throw new Error(e);
        }
    }
}
