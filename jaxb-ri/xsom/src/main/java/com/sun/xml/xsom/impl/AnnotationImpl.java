/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.XSAnnotation;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class AnnotationImpl implements XSAnnotation
{
    private Object annotation;
    public Object getAnnotation() { return annotation; }

    public Object setAnnotation(Object o) {
        Object r = this.annotation;
        this.annotation = o;
        return r;
    }

    private final Locator locator;
    public Locator getLocator() { return locator; }

    public AnnotationImpl( Object o, Locator _loc ) {
        this.annotation = o;
        this.locator = _loc;
    }

    public AnnotationImpl() {
        locator = NULL_LOCATION;
    }

    private static class LocatorImplUnmodifiable extends LocatorImpl {

        @Override
        public void setColumnNumber(int columnNumber) {
            return;
        }

        @Override
        public void setPublicId(String publicId) {
            return;
        }

        @Override
        public void setSystemId(String systemId) {
            return;
        }

        @Override
        public void setLineNumber(int lineNumber) {
            return;
        }
    };
    
    private static final LocatorImplUnmodifiable NULL_LOCATION = new LocatorImplUnmodifiable();
}
