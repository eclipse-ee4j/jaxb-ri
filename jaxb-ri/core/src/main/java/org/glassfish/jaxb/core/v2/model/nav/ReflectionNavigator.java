/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.nav;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;

import org.glassfish.jaxb.core.v2.runtime.Location;

/**
 * {@link Navigator} implementation for {@code java.lang.reflect}.
 *
 */
/*package*/final class ReflectionNavigator implements Navigator<Type, Class<?>, Field, Method> {

//  ----------  Singleton -----------------
    private static final ReflectionNavigator INSTANCE = new ReflectionNavigator();

    /*package*/static ReflectionNavigator getInstance() {
        return INSTANCE;
    }

    private ReflectionNavigator() {
    }
//  ---------------------------------------

    @Override
    public Class<?> getSuperClass(Class<?> clazz) {
        if (clazz == Object.class) {
            return null;
        }
        Class<?> sc = clazz.getSuperclass();
        if (sc == null) {
            sc = Object.class;        // error recovery
        }
        return sc;
    }

    private static final TypeVisitor<Type, Class<?>> baseClassFinder = new TypeVisitor<Type, Class<?>>() {

        @Override
        public Type onClass(Class<?> c, Class<?> sup) {
            // t is a raw type
            if (sup == c) {
                return sup;
            }

            Type r;

            Type sc = c.getGenericSuperclass();
            if (sc != null) {
                r = visit(sc, sup);
                if (r != null) {
                    return r;
                }
            }

            for (Type i : c.getGenericInterfaces()) {
                r = visit(i, sup);
                if (r != null) {
                    return r;
                }
            }

            return null;
        }

        @Override
        public Type onParameterizdType(ParameterizedType p, Class<?> sup) {
            Class<?> raw = (Class<?>) p.getRawType();
            if (raw == sup) {
                // p is of the form sup<...>
                return p;
            } else {
                // recursively visit super class/interfaces
                Type r = raw.getGenericSuperclass();
                if (r != null) {
                    r = visit(bind(r, raw, p), sup);
                }
                if (r != null) {
                    return r;
                }
                for (Type i : raw.getGenericInterfaces()) {
                    r = visit(bind(i, raw, p), sup);
                    if (r != null) {
                        return r;
                    }
                }
                return null;
            }
        }

        @Override
        public Type onGenericArray(GenericArrayType g, Class<?> sup) {
            // not clear what I should do here
            return null;
        }

        @Override
        public Type onVariable(TypeVariable v, Class<?> sup) {
            return visit(v.getBounds()[0], sup);
        }

        @Override
        public Type onWildcard(WildcardType w, Class<?> sup) {
            // not clear what I should do here
            return null;
        }

        /**
         * Replaces the type variables in {@code t} by its actual arguments.
         *
         * @param decl provides a list of type variables. See {@link GenericDeclaration#getTypeParameters()}
         * @param args actual arguments. See {@link ParameterizedType#getActualTypeArguments()}
         */
        private Type bind(Type t, GenericDeclaration decl, ParameterizedType args) {
            return binder.visit(t, new BinderArg(decl, args.getActualTypeArguments()));
        }
    };

    private static class BinderArg {

        final TypeVariable[] params;
        final Type[] args;

        BinderArg(TypeVariable[] params, Type[] args) {
            this.params = params;
            this.args = args;
            assert params.length == args.length;
        }

        public BinderArg(GenericDeclaration decl, Type[] args) {
            this(decl.getTypeParameters(), args);
        }

        Type replace(TypeVariable v) {
            for (int i = 0; i < params.length; i++) {
                if (params[i].equals(v)) {
                    return args[i];
                }
            }
            return v;   // this is a free variable
        }
    }
    private static final TypeVisitor<Type, BinderArg> binder = new TypeVisitor<Type, BinderArg>() {

        @Override
        public Type onClass(Class<?> c, BinderArg args) {
            return c;
        }

        @Override
        public Type onParameterizdType(ParameterizedType p, BinderArg args) {
            Type[] params = p.getActualTypeArguments();

            boolean different = false;
            for (int i = 0; i < params.length; i++) {
                Type t = params[i];
                params[i] = visit(t, args);
                different |= t != params[i];
            }

            Type newOwner = p.getOwnerType();
            if (newOwner != null) {
                newOwner = visit(newOwner, args);
            }
            different |= p.getOwnerType() != newOwner;

            if (!different) {
                return p;
            }

            return new ParameterizedTypeImpl((Class<?>) p.getRawType(), params, newOwner);
        }

        @Override
        public Type onGenericArray(GenericArrayType g, BinderArg types) {
            Type c = visit(g.getGenericComponentType(), types);
            if (c == g.getGenericComponentType()) {
                return g;
            }

            return new GenericArrayTypeImpl(c);
        }

        @Override
        public Type onVariable(TypeVariable v, BinderArg types) {
            return types.replace(v);
        }

        @Override
        public Type onWildcard(WildcardType w, BinderArg types) {
            // TODO: this is probably still incorrect
            // bind( "? extends T" ) with T= "? extends Foo" should be "? extends Foo",
            // not "? extends (? extends Foo)"
            Type[] lb = w.getLowerBounds();
            Type[] ub = w.getUpperBounds();
            boolean diff = false;

            for (int i = 0; i < lb.length; i++) {
                Type t = lb[i];
                lb[i] = visit(t, types);
                diff |= (t != lb[i]);
            }

            for (int i = 0; i < ub.length; i++) {
                Type t = ub[i];
                ub[i] = visit(t, types);
                diff |= (t != ub[i]);
            }

            if (!diff) {
                return w;
            }

            return new WildcardTypeImpl(lb, ub);
        }
    };

    @Override
    public Type getBaseClass(Type t, Class<?> sup) {
        return baseClassFinder.visit(t, sup);
    }

    @Override
    public String getClassName(Class<?> clazz) {
        return clazz.getName();
    }

    @Override
    public String getTypeName(Type type) {
        if (type instanceof Class) {
            Class<?> c = (Class) type;
            if (c.isArray()) {
                return getTypeName(c.getComponentType()) + "[]";
            }
            return c.getName();
        }
        return type.toString();
    }

    @Override
    public String getClassShortName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    @Override
    public Collection<? extends Field> getDeclaredFields(final Class<?> clazz) {
        Field[] fields = AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
            @Override
            public Field[] run() {
                return clazz.getDeclaredFields();
            }
        });
        return Arrays.asList(fields);
    }

    @Override
    public Field getDeclaredField(final Class<?> clazz, final String fieldName) {
        return AccessController.doPrivileged(new PrivilegedAction<Field>() {
            @Override
            public Field run() {
                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    return null;
                }
            }
        });
    }

    @Override
    public Collection<? extends Method> getDeclaredMethods(final Class<?> clazz) {
        Method[] methods =
            AccessController.doPrivileged(new PrivilegedAction<Method[]>() {
                @Override
                public Method[] run() {
                    return clazz.getDeclaredMethods();
                }
            });
        return Arrays.asList(methods);
    }

    @Override
    public Class<?> getDeclaringClassForField(Field field) {
        return field.getDeclaringClass();
    }

    @Override
    public Class<?> getDeclaringClassForMethod(Method method) {
        return method.getDeclaringClass();
    }

    @Override
    public Type getFieldType(Field field) {
        if (field.getType().isArray()) {
            Class<?> c = field.getType().getComponentType();
            if (c.isPrimitive()) {
                return Array.newInstance(c, 0).getClass();
            }
        }
        return fix(field.getGenericType());
    }

    @Override
    public String getFieldName(Field field) {
        return field.getName();
    }

    @Override
    public String getMethodName(Method method) {
        return method.getName();
    }

    @Override
    public Type getReturnType(Method method) {
        return fix(method.getGenericReturnType());
    }

    @Override
    public Type[] getMethodParameters(Method method) {
        return method.getGenericParameterTypes();
    }

    @Override
    public boolean isStaticMethod(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    @Override
    public boolean isFinalMethod(Method method) {
        return Modifier.isFinal(method.getModifiers());
    }

    @Override
    public boolean isSubClassOf(Type sub, Type sup) {
        return erasure(sup).isAssignableFrom(erasure(sub));
    }

    @Override
    public Class<?> ref(Class<?> c) {
        return c;
    }

    @Override
    public Class<?> use(Class<?> c) {
        return c;
    }

    @Override
    public Class<?> asDecl(Type t) {
        return erasure(t);
    }

    @Override
    public Class<?> asDecl(Class<?> c) {
        return c;
    }
    /**
     * Implements the logic for {@link #erasure(Type)}.
     */
    private static final TypeVisitor<Class, Void> eraser = new TypeVisitor<Class, Void>() {

        @Override
        public Class<?> onClass(Class<?> c, Void v) {
            return c;
        }

        @Override
        public Class<?> onParameterizdType(ParameterizedType p, Void v) {
            // TODO: why getRawType returns Type? not Class?
            return visit(p.getRawType(), null);
        }

        @Override
        public Class<?> onGenericArray(GenericArrayType g, Void v) {
            return Array.newInstance(
                    visit(g.getGenericComponentType(), null),
                    0).getClass();
        }

        @Override
        public Class<?> onVariable(TypeVariable tv, Void v) {
            return visit(tv.getBounds()[0], null);
        }

        @Override
        public Class<?> onWildcard(WildcardType w, Void v) {
            return visit(w.getUpperBounds()[0], null);
        }
    };

    /**
     * Returns the runtime representation of the given type.
     *
     * This corresponds to the notion of the erasure in JSR-14.
     *
     * <p>
     * Because of the difference in the way Annotation Processing and the Java reflection
     * treats primitive type and array type, we can't define this method
     * on {@link Navigator}.
     *
     * <p>
     * It made me realize how difficult it is to define the common navigation
     * layer for two different underlying reflection library. The other way
     * is to throw away the entire parameterization and go to the wrapper approach.
     */
    @Override
    public <T> Class<T> erasure(Type t) {
        return eraser.visit(t, null);
    }

    @Override
    public boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    @Override
    public boolean isFinal(Class<?> clazz) {
        return Modifier.isFinal(clazz.getModifiers());
    }

    /**
     * Returns the {@link Type} object that represents {@code clazz<T1,T2,T3>}.
     */
    public Type createParameterizedType(Class<?> rawType, Type... arguments) {
        return new ParameterizedTypeImpl(rawType, arguments, null);
    }

    @Override
    public boolean isArray(Type t) {
        if (t instanceof Class) {
            Class<?> c = (Class) t;
            return c.isArray();
        }
        if (t instanceof GenericArrayType) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isArrayButNotByteArray(Type t) {
        if (t instanceof Class) {
            Class<?> c = (Class) t;
            return c.isArray() && c != byte[].class;
        }
        if (t instanceof GenericArrayType) {
            t = ((GenericArrayType) t).getGenericComponentType();
            return t != Byte.TYPE;
        }
        return false;
    }

    @Override
    public Type getComponentType(Type t) {
        if (t instanceof Class) {
            Class<?> c = (Class) t;
            return c.getComponentType();
        }
        if (t instanceof GenericArrayType) {
            return ((GenericArrayType) t).getGenericComponentType();
        }

        throw new IllegalArgumentException();
    }

    @Override
    public Type getTypeArgument(Type type, int i) {
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return fix(p.getActualTypeArguments()[i]);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean isParameterizedType(Type type) {
        return type instanceof ParameterizedType;
    }

    @Override
    public boolean isPrimitive(Type type) {
        if (type instanceof Class) {
            Class<?> c = (Class) type;
            return c.isPrimitive();
        }
        return false;
    }

    @Override
    public Type getPrimitive(Class<?> primitiveType) {
        assert primitiveType.isPrimitive();
        return primitiveType;
    }

    @Override
    public Location getClassLocation(final Class<?> clazz) {
        return new Location() {

            @Override
            public String toString() {
                return clazz.getName();
            }
        };
    }

    @Override
    public Location getFieldLocation(final Field field) {
        return new Location() {

            @Override
            public String toString() {
                return field.toString();
            }
        };
    }

    @Override
    public Location getMethodLocation(final Method method) {
        return new Location() {

            @Override
            public String toString() {
                return method.toString();
            }
        };
    }

    @Override
    public boolean hasDefaultConstructor(Class<?> c) {
        try {
            c.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false; // todo: do this WITHOUT exception throw
        }
    }

    @Override
    public boolean isStaticField(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    @Override
    public boolean isPublicMethod(Method method) {
        return Modifier.isPublic(method.getModifiers());
    }

    @Override
    public boolean isPublicField(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }

    @Override
    public boolean isEnum(Class<?> c) {
        return Enum.class.isAssignableFrom(c);
    }

    @Override
    public Field[] getEnumConstants(Class<?> clazz) {
        try {
            Object[] values = clazz.getEnumConstants();
            Field[] fields = new Field[values.length];
            for (int i = 0; i < values.length; i++) {
                fields[i] = clazz.getField(((Enum) values[i]).name());
            }
            return fields;
        } catch (NoSuchFieldException e) {
            // impossible
            throw new NoSuchFieldError(e.getMessage());
        }
    }

    @Override
    public Type getVoidType() {
        return Void.class;
    }

    @Override
    public String getPackageName(Class<?> clazz) {
        String name = clazz.getName();
        int idx = name.lastIndexOf('.');
        if (idx < 0) {
            return "";
        } else {
            return name.substring(0, idx);
        }
    }

    @Override
    public Class<?> loadObjectFactory(Class<?> referencePoint, String pkg) {
        ClassLoader cl = SecureLoader.getClassClassLoader(referencePoint);
        if (cl == null)
            cl = SecureLoader.getSystemClassLoader();

        try {
            return cl.loadClass(pkg + ".ObjectFactory");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean isBridgeMethod(Method method) {
        return method.isBridge();
    }

    @Override
    public boolean isOverriding(Method method, final Class<?> base) {
        // this isn't actually correct,
        // as the JLS considers
        // class Derived extends Base<Integer> {
        //   Integer getX() { ... }
        // }
        // class Base<T> {
        //   T getX() { ... }
        // }
        // to be overrided. Handling this correctly needs a careful implementation

        final String name = method.getName();
        final Class[] params = method.getParameterTypes();

        return AccessController.doPrivileged(
                new PrivilegedAction<Boolean>() {

                    @Override
                    public Boolean run() {
                        Class<?> clazz = base;
                        while (clazz != null) {
                            try {
                                Method m = clazz.getDeclaredMethod(name, params);
                                if (m != null) {
                                    return Boolean.TRUE;
                                }
                            } catch (NoSuchMethodException ignored) {
                                // recursively go into the base class
                            }
                            clazz = clazz.getSuperclass();
                        }
                        return Boolean.FALSE;
                    }
                }
        );
    }

    @Override
    public boolean isInterface(Class<?> clazz) {
        return clazz.isInterface();
    }

    @Override
    public boolean isTransient(Field f) {
        return Modifier.isTransient(f.getModifiers());
    }

    @Override
    public boolean isInnerClass(Class<?> clazz) {
        return clazz.getEnclosingClass() != null && !Modifier.isStatic(clazz.getModifiers());
    }

    @Override
    public boolean isSameType(Type t1, Type t2) {
        return t1.equals(t2);
    }

    /**
     * JDK 5.0 has a bug of creating {@link GenericArrayType} where it shouldn't.
     * fix that manually to work around the problem.
     *
     * See bug 6202725.
     */
    private Type fix(Type t) {
        if (!(t instanceof GenericArrayType)) {
            return t;
        }

        GenericArrayType gat = (GenericArrayType) t;
        if (gat.getGenericComponentType() instanceof Class) {
            Class<?> c = (Class<?>) gat.getGenericComponentType();
            return Array.newInstance(c, 0).getClass();
        }

        return t;
    }
}
