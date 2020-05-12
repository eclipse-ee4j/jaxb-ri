/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.*;
import org.glassfish.jaxb.core.v2.runtime.Location;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Iterator;

/**
 * {@link EnumLeafInfo} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
class EnumLeafInfoImpl<T,C,F,M> extends TypeInfoImpl<T,C,F,M>
        implements EnumLeafInfo<T,C>, Element<T,C>, Iterable<EnumConstantImpl<T,C,F,M>> {

    /**
     * The enum class whose information this object represents.
     */
    /*package*/ final C clazz;

    NonElement<T,C> baseType;

    private final T type;

    /**
     * Can be null for anonymous types.
     */
    private final QName typeName;

    /**
     * All the {@link EnumConstantImpl}s are linked in this list.   
     */
    private EnumConstantImpl<T,C,F,M> firstConstant;

    /**
     * If this enum is also bound to an element, that tag name.
     * Or else null.
     */
    private QName elementName;

    /**
     * Used to recognize token vs string.
     */
    protected boolean tokenStringType;
            
    /**
     * @param clazz
     * @param type
     *      clazz and type should both point to the enum class
     *      that this {@link EnumLeafInfo} represents.
     *      Because of the type parameterization we have to take them separately.
     */
    public EnumLeafInfoImpl(ModelBuilder<T,C,F,M> builder,
                            Locatable upstream, C clazz, T type ) {
        super(builder,upstream);
        this.clazz = clazz;
        this.type = type;

        elementName = parseElementName(clazz);

        // compute the type name
        // TODO: I guess it must be allowed for enums to have @XmlElement
        typeName = parseTypeName(clazz);

        // locate the base type.
        // this can be done eagerly because there shouldn't be no cycle.
        XmlEnum xe = builder.reader.getClassAnnotation(XmlEnum.class, clazz, this);
        if(xe!=null) {
            T base = builder.reader.getClassValue(xe, "value");
            baseType = builder.getTypeInfo(base,this);
        } else {
            baseType = builder.getTypeInfo(builder.nav.ref(String.class),this);
        }
    }

    /**
     * Build {@link EnumConstant}s and discover/report any error in it.
     */
    protected void calcConstants() {
        EnumConstantImpl<T,C,F,M> last = null;
        
        // first check if we represent xs:token derived type
        Collection<? extends F> fields = nav().getDeclaredFields(clazz);
        for (F f : fields) {
            if (nav().isSameType(nav().getFieldType(f), nav().ref(String.class))) {
                XmlSchemaType schemaTypeAnnotation = builder.reader.getFieldAnnotation(XmlSchemaType.class, f, this);
                if (schemaTypeAnnotation != null) {
                    if ("token".equals(schemaTypeAnnotation.name())) {
                        tokenStringType = true;
                        break;
                    }
                };
            }
        }
        F[] constants = nav().getEnumConstants(clazz);
        for( int i=constants.length-1; i>=0; i-- ) {
            F constant = constants[i];
            String name = nav().getFieldName(constant);
            XmlEnumValue xev = builder.reader.getFieldAnnotation(XmlEnumValue.class, constant, this);

            String literal;
            if(xev==null)   literal = name;
            else            literal = xev.value();

            last = createEnumConstant(name,literal,constant,last);
        }
        this.firstConstant = last;
    }

    protected EnumConstantImpl<T,C,F,M> createEnumConstant(String name, String literal, F constant, EnumConstantImpl<T,C,F,M> last) {
        return new EnumConstantImpl<T,C,F,M>(this, name, literal, last);
    }


    public T getType() {
        return type;
    }

    /**
     *
     * @return true if enum is restriction/extension from xs:token type, otherwise false
     */
    public boolean isToken() {
        return tokenStringType;
    }
    
    /**
     * Leaf-type cannot be referenced from IDREF.
     *
     * @deprecated
     *      why are you calling a method whose return value is always known?
     */
    public final boolean canBeReferencedByIDREF() {
        return false;
    }

    public QName getTypeName() {
        return typeName;
    }

    public C getClazz() {
        return clazz;
    }

    public NonElement<T,C> getBaseType() {
        return baseType;
    }

    public boolean isSimpleType() {
        return true;
    }

    public Location getLocation() {
        return nav().getClassLocation(clazz);
    }

    public Iterable<? extends EnumConstantImpl<T,C,F,M>> getConstants() {
        if(firstConstant==null)
            calcConstants();
        return this;
    }

    @Override
    public void link() {
        // make sure we've computed constants
        getConstants();
        super.link();
    }

    /**
     * No substitution.
     *
     * @deprecated if you are invoking this method directly, there's something wrong.
     */
    public Element<T, C> getSubstitutionHead() {
        return null;
    }

    public QName getElementName() {
        return elementName;
    }

    public boolean isElement() {
        return elementName!=null;
    }

    public Element<T,C> asElement() {
        if(isElement())
            return this;
        else
            return null;
    }

    /**
     * When a bean binds to an element, it's always through {@link XmlRootElement},
     * so this method always return null.
     *
     * @deprecated
     *      you shouldn't be invoking this method on {@link ClassInfoImpl}.
     */
    public ClassInfo<T,C> getScope() {
        return null;
    }

    public Iterator<EnumConstantImpl<T,C,F,M>> iterator() {
        return new Iterator<EnumConstantImpl<T,C,F,M>>() {
            private EnumConstantImpl<T,C,F,M> next = firstConstant;
            public boolean hasNext() {
                return next!=null;
            }

            public EnumConstantImpl<T,C,F,M> next() {
                EnumConstantImpl<T,C,F,M> r = next;
                next = next.next;
                return r;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
