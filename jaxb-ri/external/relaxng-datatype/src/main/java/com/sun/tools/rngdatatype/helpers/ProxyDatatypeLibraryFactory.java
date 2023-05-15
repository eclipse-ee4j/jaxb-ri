/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.rngdatatype.helpers;

import com.sun.tools.rngdatatype.Datatype;
import com.sun.tools.rngdatatype.DatatypeBuilder;
import com.sun.tools.rngdatatype.DatatypeException;
import com.sun.tools.rngdatatype.DatatypeLibrary;
import com.sun.tools.rngdatatype.DatatypeLibraryFactory;
import com.sun.tools.rngdatatype.DatatypeStreamingValidator;
import com.sun.tools.rngdatatype.ValidationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public final class ProxyDatatypeLibraryFactory implements DatatypeLibraryFactory {

    private static final String RELAXNG_FACTORY_NAME = "org.relaxng.datatype.DatatypeLibraryFactory";

    //search for implementations implementing older org.relaxng.datatype.DatatypeLibraryFactory
    //since there is no difference expected between old and new DatatypeLibraryFactory,
    //we can make the old one working with the help of proxy

    public ProxyDatatypeLibraryFactory() {
    }

    @Override
    public DatatypeLibrary createDatatypeLibrary(String uri) {
        Class<?> factoryClass;
        try {
            factoryClass = Class.forName(RELAXNG_FACTORY_NAME, false, ProxyDatatypeLibraryFactory.class.getClassLoader());
            Module main = ProxyDatatypeLibraryFactory.class.getModule();
            main.addUses(factoryClass);
        } catch (Throwable t) {
            return null;
        }
        @SuppressWarnings({"unchecked"})
        ServiceLoader<org.relaxng.datatype.DatatypeLibraryFactory> extLoader = (ServiceLoader<org.relaxng.datatype.DatatypeLibraryFactory>) ServiceLoader.load(factoryClass);
        for (org.relaxng.datatype.DatatypeLibraryFactory factory : extLoader) {
            org.relaxng.datatype.DatatypeLibrary datatypeLibrary = factory.createDatatypeLibrary(uri);
            if (datatypeLibrary != null) {
                return new DatatypeLibrary() {
                    @Override
                    public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException {
                        try {
                            org.relaxng.datatype.DatatypeBuilder builder = datatypeLibrary.createDatatypeBuilder(baseTypeLocalName);

                            return new DatatypeBuilder() {
                                @Override
                                public void addParameter(String name, String strValue, ValidationContext context) throws DatatypeException {
                                    try {
                                        builder.addParameter(name, strValue, (org.relaxng.datatype.ValidationContext) Proxy.newProxyInstance(ValidationContext.class.getClassLoader(), new Class<?>[]{org.relaxng.datatype.ValidationContext.class},
                                                new GIH(org.relaxng.datatype.ValidationContext.class, context)));
                                    } catch (org.relaxng.datatype.DatatypeException de) {
                                        throw new DatatypeException(de.getIndex(), de.getMessage());
                                    }
                                }

                                @Override
                                public Datatype createDatatype() throws DatatypeException {
                                    try {
                                        org.relaxng.datatype.Datatype datatype = builder.createDatatype();
                                        return (Datatype) Proxy.newProxyInstance(Datatype.class.getClassLoader(), new Class<?>[]{Datatype.class},
                                                new GIH(org.relaxng.datatype.Datatype.class, datatype));
                                    } catch (org.relaxng.datatype.DatatypeException de) {
                                        throw new DatatypeException(de.getIndex(), de.getMessage());
                                    }
                                }
                            };
                        } catch (org.relaxng.datatype.DatatypeException de) {
                            throw new DatatypeException(de.getIndex(), de.getMessage());
                        }

                    }

                    @Override
                    public Datatype createDatatype(String typeLocalName) throws DatatypeException {
                        try {
                            org.relaxng.datatype.Datatype datatype = datatypeLibrary.createDatatype(typeLocalName);
                            return (Datatype) Proxy.newProxyInstance(Datatype.class.getClassLoader(), new Class<?>[]{Datatype.class},
                                    new GIH(org.relaxng.datatype.Datatype.class, datatype));
                        } catch (org.relaxng.datatype.DatatypeException de) {
                            throw new DatatypeException(de.getIndex(), de.getMessage());
                        }
                    }
                };
            }
        }
        return null;
    }

    private static class GIH implements InvocationHandler {
        private final Object obj;
        private final Map<String, Method> methods;

        GIH(Class<?> cls, Object obj) {
            this.obj = obj;
            methods = Arrays.stream(cls.getMethods()).collect(Collectors.toMap(k -> k.getParameterCount() + k.getName(), v -> v));
            for (Method m : Object.class.getMethods()) {
                methods.putIfAbsent(m.getParameterCount() + m.getName(), m);
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Modifier.isStatic(method.getModifiers())) {
                //we don't expect any static methods
                throw new IllegalArgumentException();
            }
            if (args != null) {
                List<Object> params = new ArrayList<>();
                for (Object o : args) {
                    if (o == null) {
                        params.add(o);
                    } else if (Proxy.isProxyClass(o.getClass())) {
                        params.add(o);
                    } else if (o instanceof ValidationContext) {
                        params.add(Proxy.newProxyInstance(
                                ValidationContext.class.getClassLoader(),
                                new Class<?>[]{org.relaxng.datatype.ValidationContext.class},
                                new GIH(org.relaxng.datatype.ValidationContext.class, o)));
                    } else if (o instanceof org.relaxng.datatype.ValidationContext) {
                        params.add(Proxy.newProxyInstance(
                                ValidationContext.class.getClassLoader(),
                                new Class<?>[]{ValidationContext.class},
                                new GIH(ValidationContext.class, o)));
                    } else {
                        params.add(o);
                    }
                }
                if ("createStreamingValidator".equals(method.getName()) && org.relaxng.datatype.ValidationContext.class.equals(method.getParameterTypes()[0])) {
                    Object result = methods.get(method.getParameterCount() + method.getName()).invoke(obj, params.toArray(new Object[0]));
                    return Proxy.newProxyInstance(DatatypeStreamingValidator.class.getClassLoader(),
                            new Class<?>[]{DatatypeStreamingValidator.class},
                            new GIH(DatatypeStreamingValidator.class, result)
                    );
                }
                return methods.get(method.getParameterCount() + method.getName()).invoke(obj, params.toArray(new Object[0]));
            } else {
                return methods.get(method.getParameterCount() + method.getName()).invoke(obj, args);
            }
        }
    }
}
