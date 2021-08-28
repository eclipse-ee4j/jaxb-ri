/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model.nav;

import java.lang.reflect.Type;
import java.util.Collection;

import com.sun.codemodel.JClass;
import org.glassfish.jaxb.core.v2.model.nav.Navigator;
import org.glassfish.jaxb.core.v2.runtime.Location;

/**
 * {@link Navigator} implementation for XJC.
 *
 * Most of the Navigator methods are used for parsing the model, which doesn't happen
 * in XJC. So Most of the methods aren't really implemented. Implementations should
 * be filled in as needed.
 *
 * @author Kohsuke Kawaguchi
 */
public final class NavigatorImpl implements Navigator<NType,NClass,Void,Void> {
    public static final NavigatorImpl theInstance = new NavigatorImpl();

    private NavigatorImpl() {
    }

    @Override
    public NClass getSuperClass(NClass nClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NType getBaseClass(NType nt, NClass base) {
        if(nt instanceof EagerNType) {
            EagerNType ent = (EagerNType) nt;
            if (base instanceof EagerNClass) {
                EagerNClass enc = (EagerNClass) base;
                return create(Utils.REFLECTION_NAVIGATOR.getBaseClass(ent.t, enc.c));
            }
            // lazy class can never be a base type of an eager type
            return null;
        }
        if (nt instanceof NClassByJClass) {
            NClassByJClass nnt = (NClassByJClass) nt;
            if (base instanceof EagerNClass) {
                EagerNClass enc = (EagerNClass) base;
                return ref(nnt.clazz.getBaseClass(enc.c));
            }
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public String getClassName(NClass nClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTypeName(NType type) {
        return type.fullName();
    }

    @Override
    public String getClassShortName(NClass nClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends Void> getDeclaredFields(NClass nClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void getDeclaredField(NClass clazz, String fieldName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends Void> getDeclaredMethods(NClass nClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NClass getDeclaringClassForField(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NClass getDeclaringClassForMethod(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NType getFieldType(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFieldName(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMethodName(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NType getReturnType(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NType[] getMethodParameters(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isStaticMethod(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFinalMethod(Void aVoid) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSubClassOf(NType sub, NType sup) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NClass ref(Class c) {
        return create(c);
    }

    public NClass ref(JClass c) {
        if(c==null)     return null;
        return new NClassByJClass(c);
    }

    @Override
    public NType use(NClass nc) {
        return nc;
    }

    @Override
    public NClass asDecl(NType nt) {
        if(nt instanceof NClass)
            return (NClass)nt;
        else
            return null;
    }

    @Override
    public NClass asDecl(Class c) {
        return ref(c);
    }

    @Override
    public boolean isArray(NType nType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArrayButNotByteArray(NType t) {
        throw new UnsupportedOperationException();
    }


    @Override
    public NType getComponentType(NType nType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NType getTypeArgument(NType nt, int i) {
        if (nt instanceof EagerNType) {
            EagerNType ent = (EagerNType) nt;
            return create(Utils.REFLECTION_NAVIGATOR.getTypeArgument(ent.t,i));
        }
        if (nt instanceof NClassByJClass) {
            NClassByJClass nnt = (NClassByJClass) nt;
            return ref(nnt.clazz.getTypeParameters().get(i));
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isParameterizedType(NType nt) {
        if (nt instanceof EagerNType) {
            EagerNType ent = (EagerNType) nt;
            return Utils.REFLECTION_NAVIGATOR.isParameterizedType(ent.t);
        }
        if (nt instanceof NClassByJClass) {
            NClassByJClass nnt = (NClassByJClass) nt;
            return nnt.clazz.isParameterized();
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPrimitive(NType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NType getPrimitive(Class primitiveType) {
        return create(primitiveType);
    }

    @SuppressWarnings("FinalStaticMethod")
    public static final NType create(Type t) {
        if(t==null)     return null;
        if(t instanceof Class)
            return create((Class)t);

        return new EagerNType(t);
    }

    public static NClass create( Class c ) {
        if(c==null)     return null;
        return new EagerNClass(c);
    }

    /**
     * Creates a {@link NType} representation for a parameterized type
     * {@code RawType<ParamType1,ParamType2,...> }.
     */
    public static NType createParameterizedType( NClass rawType, NType... args ) {
        return new NParameterizedType(rawType,args);
    }

    public static NType createParameterizedType( Class rawType, NType... args ) {
        return new NParameterizedType(create(rawType),args);
    }

    @Override
    public Location getClassLocation(final NClass c) {
        // not really needed for XJC but doesn't hurt to have one
        return new Location() {
            @Override
            public String toString() {
                return c.fullName();
            }
        };
    }

    @Override
    public Location getFieldLocation(Void v) {
        throw new IllegalStateException();
    }

    @Override
    public Location getMethodLocation(Void v) {
        throw new IllegalStateException();
    }

    @Override
    public boolean hasDefaultConstructor(NClass nClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isStaticField(Void aVoid) {
        throw new IllegalStateException();
    }

    @Override
    public boolean isPublicMethod(Void aVoid) {
        throw new IllegalStateException();
    }

    @Override
    public boolean isPublicField(Void aVoid) {
        throw new IllegalStateException();
    }

    @Override
    public boolean isEnum(NClass c) {
        return isSubClassOf(c,create(Enum.class));
    }

    @Override
    public <T> NType erasure(NType type) {
        if(type instanceof NParameterizedType) {
            NParameterizedType pt = (NParameterizedType) type;
            return pt.rawType;
        }
        return type;
    }

    @Override
    public boolean isAbstract(NClass clazz) {
        return clazz.isAbstract();
    }

    /**
     * @deprecated
     *      no class generated by XJC is final.
     */
    @Deprecated
    @Override
    public boolean isFinal(NClass clazz) {
        return false;
    }

    @Override
    public Void[] getEnumConstants(NClass clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NType getVoidType() {
        return ref(void.class);
    }

    @Override
    public String getPackageName(NClass clazz) {
        // TODO: implement this method later
        throw new UnsupportedOperationException();
    }

    @Override
    public NClass loadObjectFactory(NClass referencePoint, String pkg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBridgeMethod(Void method) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOverriding(Void method,NClass clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInterface(NClass clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTransient(Void f) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isInnerClass(NClass clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSameType(NType t1, NType t2) {
         throw new UnsupportedOperationException();
    }
}
