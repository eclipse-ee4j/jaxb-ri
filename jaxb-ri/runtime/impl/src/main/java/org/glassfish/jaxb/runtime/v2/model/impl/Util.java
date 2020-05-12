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

import org.glassfish.jaxb.core.v2.model.annotation.AnnotationReader;
import org.glassfish.jaxb.core.v2.model.annotation.AnnotationSource;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import jakarta.activation.MimeType;
import jakarta.activation.MimeTypeParseException;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSchemaTypes;

import javax.xml.namespace.QName;

/**
 * Common code between {@link PropertyInfoImpl} and {@link ElementInfoImpl}.
 *
 * @author Kohsuke Kawaguchi
 */
final class Util {
    static <T,C,F,M> QName calcSchemaType(
            AnnotationReader<T,C,F,M> reader,
            AnnotationSource primarySource, C enclosingClass, T individualType, Locatable src ) {

        XmlSchemaType xst = primarySource.readAnnotation(XmlSchemaType.class);
        if(xst!=null) {
            return new QName(xst.namespace(),xst.name());
        }

        // check the defaulted annotation
        XmlSchemaTypes xsts = reader.getPackageAnnotation(XmlSchemaTypes.class,enclosingClass,src);
        XmlSchemaType[] values = null;
        if(xsts!=null)
            values = xsts.value();
        else {
            xst = reader.getPackageAnnotation(XmlSchemaType.class,enclosingClass,src);
            if(xst!=null) {
                values = new XmlSchemaType[1];
                values[0] = xst;
            }
        }
        if(values!=null) {
            for( XmlSchemaType item : values ) {
                if(reader.getClassValue(item,"type").equals(individualType)) {
                    return new QName(item.namespace(),item.name());
                }
            }
        }

        return null;
    }
    
    static MimeType calcExpectedMediaType(AnnotationSource primarySource,
                        ModelBuilder builder ) {
        XmlMimeType xmt = primarySource.readAnnotation(XmlMimeType.class);
        if(xmt==null)
            return null;
        
        try {
            return new MimeType(xmt.value());
        } catch (MimeTypeParseException e) {
            builder.reportError(new IllegalAnnotationException(
                Messages.ILLEGAL_MIME_TYPE.format(xmt.value(),e.getMessage()),
                xmt
            ));
            return null;
        }
    }
}
