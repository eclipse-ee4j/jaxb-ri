/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.runtime;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Kohsuke Kawaguchi
 */
public class RuntimeUtil {
    /**
     * XmlAdapter for printing arbitrary object by using {@link Object#toString()}.
     */
    public static final class ToStringAdapter extends XmlAdapter<String,Object> {
        public Object unmarshal(String s) {
            throw new UnsupportedOperationException();
        }

        public String marshal(Object o) {
            if(o==null)     return null;
            return o.toString();
        }
    }

    /**
     * Map from {@link Class} objects representing primitive types
     * to {@link Class} objects representing their boxed types.
     * <p>
     * e.g., {@code int -> Integer}.
     */
    public static final Map<Class,Class> boxToPrimitive;

    /**
     * Reverse map of {@link #boxToPrimitive}.
     */
    public static final Map<Class,Class> primitiveToBox;

    static {
        Map<Class,Class> b = new HashMap<Class,Class>();
        b.put(Byte.TYPE,Byte.class);
        b.put(Short.TYPE,Short.class);
        b.put(Integer.TYPE,Integer.class);
        b.put(Long.TYPE,Long.class);
        b.put(Character.TYPE,Character.class);
        b.put(Boolean.TYPE,Boolean.class);
        b.put(Float.TYPE,Float.class);
        b.put(Double.TYPE,Double.class);
        b.put(Void.TYPE,Void.class);

        primitiveToBox = Collections.unmodifiableMap(b);

        Map<Class,Class> p = new HashMap<Class,Class>();
        for( Map.Entry<Class,Class> e :  b.entrySet() )
            p.put(e.getValue(),e.getKey());

        boxToPrimitive = Collections.unmodifiableMap(p);
    }

    /**
     * Reports a print conversion error while marshalling.
     */
/*
    public static void handlePrintConversionException(
        Object caller, Exception e, XMLSerializer serializer ) throws SAXException {

        if( e instanceof SAXException )
            // assume this exception is not from application.
            // (e.g., when a marshaller aborts the processing, this exception
            //        will be thrown)
            throw (SAXException)e;

        ValidationEvent ve = new PrintConversionEventImpl(
            ValidationEvent.ERROR, e.getMessage(),
            new ValidationEventLocatorImpl(caller), e );
        serializer.reportError(ve);
    }
*/

    /**
     * Reports that the type of an object in a property is unexpected.
     */
/*
    public static void handleTypeMismatchError( XMLSerializer serializer,
            Object parentObject, String fieldName, Object childObject ) throws SAXException {

         ValidationEvent ve = new ValidationEventImpl(
            ValidationEvent.ERROR, // maybe it should be a fatal error.
             Messages.TYPE_MISMATCH.format(
                getTypeName(parentObject),
                fieldName,
                getTypeName(childObject) ),
            new ValidationEventLocatorExImpl(parentObject,fieldName) );

        serializer.reportError(ve);
    }
*/

    private static String getTypeName( Object o ) {
        return o.getClass().getName();
    }
}
