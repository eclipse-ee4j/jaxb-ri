/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.HashMap;

/**
 * Dynamically implements the typed annotation writer interfaces.
 *
 * @author Kohsuke Kawaguchi
 */
class TypedAnnotationWriter<A extends Annotation,W extends JAnnotationWriter<A>>
    implements InvocationHandler, JAnnotationWriter<A> {
    /**
     * This is what we are writing to.
     */
    private final JAnnotationUse use;

    /**
     * The annotation that we are writing.
     */
    private final Class<A> annotation;

    /**
     * The type of the writer.
     */
    private final Class<W> writerType;

    /**
     * Keeps track of writers for array members.
     * Lazily created.
     */
    private Map<String,JAnnotationArrayMember> arrays;

    public TypedAnnotationWriter(Class<A> annotation, Class<W> writer, JAnnotationUse use) {
        this.annotation = annotation;
        this.writerType = writer;
        this.use = use;
    }

    public JAnnotationUse getAnnotationUse() {
        return use;
    }

    public Class<A> getAnnotationType() {
        return annotation;
    }

    private static Method getOverriddenJAnnotationWriterMethod(Method method) {
        if(method.getDeclaringClass()==JAnnotationWriter.class) {
            return method;
        }
        try {
            return JAnnotationWriter.class.getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException __) {
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            switch (method.getName()) {
                case "equals":
                    return proxy == args[0];
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "toString":
                    return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
            }
        }

        Method overridenMethod = getOverriddenJAnnotationWriterMethod(method);
        if(overridenMethod != null) {
            try {
                return overridenMethod.invoke(this,args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        String name = method.getName();
        Object arg=null;
        if(args!=null && args.length>0)
            arg = args[0];

        // check how it's defined on the annotation
        Method m = annotation.getDeclaredMethod(name);
        Class<?> rt = m.getReturnType();

        // array value
        if(rt.isArray()) {
            return addArrayValue(proxy,name,rt.getComponentType(),method.getReturnType(),arg);
        }

        // sub annotation
        if(Annotation.class.isAssignableFrom(rt)) {
            Class<? extends Annotation> r = (Class<? extends Annotation>)rt;
            return new TypedAnnotationWriter(
                r,method.getReturnType(),use.annotationParam(name,r)).createProxy();
        }

        // scalar value

        if(arg instanceof JType) {
            JType targ = (JType) arg;
            checkType(Class.class,rt);
            if(m.getDefaultValue()!=null) {
                // check the default
                if(targ.equals(targ.owner().ref((Class)m.getDefaultValue())))
                    return proxy;   // defaulted
            }
            use.param(name,targ);
            return proxy;
        }

        // other Java built-in types
        checkType(arg.getClass(),rt);
        if(m.getDefaultValue()!=null && m.getDefaultValue().equals(arg))
            // defaulted. no need to write out.
            return proxy;

        if(arg instanceof String) {
            use.param(name,(String)arg);
            return proxy;
        }
        if(arg instanceof Boolean) {
            use.param(name,(Boolean)arg);
            return proxy;
        }
        if(arg instanceof Integer) {
            use.param(name,(Integer)arg);
            return proxy;
        }
        if(arg instanceof Class) {
            use.param(name,(Class)arg);
            return proxy;
        }
        if(arg instanceof Enum) {
            use.param(name,(Enum)arg);
            return proxy;
        }

        throw new IllegalArgumentException("Unable to handle this method call "+method.toString());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object addArrayValue(Object proxy,String name, Class<?> itemType, Class<?> expectedReturnType, Object arg) {
        if(arrays==null)
            arrays = new HashMap<String,JAnnotationArrayMember>();
        JAnnotationArrayMember m = arrays.get(name);
        if(m==null) {
            m = use.paramArray(name);
            arrays.put(name,m);
        }

        // sub annotation
        if(Annotation.class.isAssignableFrom(itemType)) {
            Class<? extends Annotation> r = (Class<? extends Annotation>)itemType;
            if(!JAnnotationWriter.class.isAssignableFrom(expectedReturnType))
                throw new IllegalArgumentException("Unexpected return type "+expectedReturnType);
            return new TypedAnnotationWriter(r,expectedReturnType,m.annotate(r)).createProxy();
        }

        // primitive
        if(arg instanceof JType) {
            checkType(Class.class,itemType);
            m.param((JType)arg);
            return proxy;
        }
        checkType(arg.getClass(),itemType);
        if(arg instanceof String) {
            m.param((String)arg);
            return proxy;
        }
        if(arg instanceof Boolean) {
            m.param((Boolean)arg);
            return proxy;
        }
        if(arg instanceof Integer) {
            m.param((Integer)arg);
            return proxy;
        }
        if(arg instanceof Class) {
            m.param((Class)arg);
            return proxy;
        }
        // TODO: enum constant. how should we handle it?

        throw new IllegalArgumentException("Unable to handle this method call ");
    }


    /**
     * Check if the type of the argument matches our expectation.
     * If not, report an error.
     */
    private void checkType(Class<?> actual, Class<?> expected) {
        if(expected==actual || expected.isAssignableFrom(actual))
            return; // no problem

        if( expected==JCodeModel.boxToPrimitive.get(actual) )
            return; // no problem

        throw new IllegalArgumentException("Expected "+expected+" but found "+actual);
    }

    /**
     * Creates a proxy and returns it.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private W createProxy() {
        return (W)Proxy.newProxyInstance(
            SecureLoader.getClassClassLoader(writerType),new Class<?>[]{writerType},this);
    }

    /**
     * Creates a new typed annotation writer.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static <W extends JAnnotationWriter<?>> W create(Class<W> w, JAnnotatable annotatable) {
        Class<? extends Annotation> a = findAnnotationType(w);
        return (W)new TypedAnnotationWriter(a,w,annotatable.annotate(a)).createProxy();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Class<? extends Annotation> findAnnotationType(Class<?> clazz) {
        for( Type t : clazz.getGenericInterfaces()) {
            if(t instanceof ParameterizedType) {
                ParameterizedType p = (ParameterizedType) t;
                if(p.getRawType()==JAnnotationWriter.class)
                    return (Class<? extends Annotation>)p.getActualTypeArguments()[0];
            }
            if(t instanceof Class<?>) {
                // recursive search
                Class<? extends Annotation> r = findAnnotationType((Class<?>)t);
                if(r!=null)     return r;
            }
        }
        return null;
    }
}
