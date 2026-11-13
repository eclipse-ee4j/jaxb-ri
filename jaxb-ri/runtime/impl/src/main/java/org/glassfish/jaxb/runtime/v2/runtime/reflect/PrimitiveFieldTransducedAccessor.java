/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import org.glassfish.jaxb.runtime.DatatypeConverterImpl;
import org.glassfish.jaxb.runtime.v2.model.impl.RuntimeBuiltinLeafInfoImpl;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * {@link TransducedAccessor} for a field of a primitive type that uses the built-in
 * conversion of its wrapper type.
 *
 * <p>
 * The generic {@link TransducedAccessor.CompositeTransducedAccessorImpl} goes through
 * {@link Field#get(Object)} / {@link Field#set(Object, Object)}, so every value read or
 * written is boxed. These accessors use the primitive {@code Field} methods and the same
 * {@link DatatypeConverterImpl} conversions the built-in transducers use, so the lexical
 * forms accepted and produced are identical; {@code int} elements are also written without
 * an intermediate {@code String}.
 *
 * <p>
 * Only plain {@link Accessor.FieldReflection} accessors combined with the unmodified built-in
 * transducer qualify: adapters, {@code @XmlSchemaType}, {@code @XmlID}/{@code @XmlIDREF},
 * MIME types and getter/setter properties keep the generic path.
 */
abstract class PrimitiveFieldTransducedAccessor<BeanT> extends DefaultTransducedAccessor<BeanT> {

    protected final Field f;

    private PrimitiveFieldTransducedAccessor(Field f) {
        this.f = f;
    }

    /**
     * @return
     *      a primitive accessor for {@code acc}/{@code xducer}, or {@code null} if this pair
     *      must use the generic implementation.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static <T> TransducedAccessor<T> create(Transducer xducer, Accessor acc) {
        if (acc.getClass() != Accessor.FieldReflection.class)
            return null;
        Field f = ((Accessor.FieldReflection) acc).f;
        Class<?> type = f.getType();
        if (!type.isPrimitive() || xducer != RuntimeBuiltinLeafInfoImpl.LEAVES.get(box(type)))
            return null;
        if (type == int.class) return new IntField<>(f);
        if (type == long.class) return new LongField<>(f);
        if (type == boolean.class) return new BooleanField<>(f);
        if (type == double.class) return new DoubleField<>(f);
        if (type == float.class) return new FloatField<>(f);
        if (type == short.class) return new ShortField<>(f);
        if (type == byte.class) return new ByteField<>(f);
        return null;    // char has no built-in transducer
    }

    private static Class<?> box(Class<?> primitive) {
        if (primitive == int.class) return Integer.class;
        if (primitive == long.class) return Long.class;
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == double.class) return Double.class;
        if (primitive == float.class) return Float.class;
        if (primitive == short.class) return Short.class;
        if (primitive == byte.class) return Byte.class;
        return null;
    }

    @Override
    public final boolean hasValue(BeanT bean) {
        return true;    // a primitive field always has a value
    }

    static IllegalAccessError accessError(IllegalAccessException e) {
        return new IllegalAccessError(e.getMessage());
    }

    private static final class IntField<BeanT> extends PrimitiveFieldTransducedAccessor<BeanT> {
        IntField(Field f) { super(f); }

        @Override
        public String print(BeanT bean) {
            return DatatypeConverterImpl._printInt(get(bean));
        }

        @Override
        public void parse(BeanT bean, CharSequence lexical) {
            int v = DatatypeConverterImpl._parseInt(lexical);
            try {
                f.setInt(bean, v);
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }

        @Override
        public void writeLeafElement(XMLSerializer w, Name tagName, BeanT bean, String fieldName) throws SAXException, IOException, XMLStreamException {
            w.leafElement(tagName, get(bean), fieldName);
        }

        private int get(BeanT bean) {
            try {
                return f.getInt(bean);
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }
    }

    private static final class LongField<BeanT> extends PrimitiveFieldTransducedAccessor<BeanT> {
        LongField(Field f) { super(f); }

        @Override
        public String print(BeanT bean) {
            try {
                return DatatypeConverterImpl._printLong(f.getLong(bean));
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }

        @Override
        public void parse(BeanT bean, CharSequence lexical) {
            long v = DatatypeConverterImpl._parseLong(lexical);
            try {
                f.setLong(bean, v);
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }
    }

    private static final class BooleanField<BeanT> extends PrimitiveFieldTransducedAccessor<BeanT> {
        BooleanField(Field f) { super(f); }

        @Override
        public String print(BeanT bean) {
            try {
                return DatatypeConverterImpl._printBoolean(f.getBoolean(bean));
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }

        @Override
        public void parse(BeanT bean, CharSequence lexical) {
            // an unrecognized literal parses to null, which the generic accessor
            // stores as the field's uninitialized value: false
            Boolean v = DatatypeConverterImpl._parseBoolean(lexical);
            try {
                f.setBoolean(bean, v != null && v);
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }
    }

    private static final class DoubleField<BeanT> extends PrimitiveFieldTransducedAccessor<BeanT> {
        DoubleField(Field f) { super(f); }

        @Override
        public String print(BeanT bean) {
            try {
                return DatatypeConverterImpl._printDouble(f.getDouble(bean));
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }

        @Override
        public void parse(BeanT bean, CharSequence lexical) {
            double v = DatatypeConverterImpl._parseDouble(lexical);
            try {
                f.setDouble(bean, v);
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }
    }

    private static final class FloatField<BeanT> extends PrimitiveFieldTransducedAccessor<BeanT> {
        FloatField(Field f) { super(f); }

        @Override
        public String print(BeanT bean) {
            try {
                return DatatypeConverterImpl._printFloat(f.getFloat(bean));
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }

        @Override
        public void parse(BeanT bean, CharSequence lexical) {
            float v = DatatypeConverterImpl._parseFloat(lexical.toString());
            try {
                f.setFloat(bean, v);
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }
    }

    private static final class ShortField<BeanT> extends PrimitiveFieldTransducedAccessor<BeanT> {
        ShortField(Field f) { super(f); }

        @Override
        public String print(BeanT bean) {
            try {
                return DatatypeConverterImpl._printShort(f.getShort(bean));
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }

        @Override
        public void parse(BeanT bean, CharSequence lexical) {
            short v = DatatypeConverterImpl._parseShort(lexical);
            try {
                f.setShort(bean, v);
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }
    }

    private static final class ByteField<BeanT> extends PrimitiveFieldTransducedAccessor<BeanT> {
        ByteField(Field f) { super(f); }

        @Override
        public String print(BeanT bean) {
            try {
                return DatatypeConverterImpl._printByte(f.getByte(bean));
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }

        @Override
        public void parse(BeanT bean, CharSequence lexical) {
            byte v = DatatypeConverterImpl._parseByte(lexical);
            try {
                f.setByte(bean, v);
            } catch (IllegalAccessException e) {
                throw accessError(e);
            }
        }
    }
}
