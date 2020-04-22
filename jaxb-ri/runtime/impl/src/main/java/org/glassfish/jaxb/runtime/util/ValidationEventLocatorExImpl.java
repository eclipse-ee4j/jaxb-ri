/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.util;

import org.glassfish.jaxb.runtime.ValidationEventLocatorEx;
import jakarta.xml.bind.helpers.ValidationEventLocatorImpl;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ValidationEventLocatorExImpl
    extends ValidationEventLocatorImpl implements ValidationEventLocatorEx {
    
    private final String fieldName;
        
    public ValidationEventLocatorExImpl( Object target, String fieldName ) {
        super(target);
        this.fieldName = fieldName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    /**
     * Returns a nice string representation for better debug experience.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[url=");
        buf.append(getURL());
        buf.append(",line=");
        buf.append(getLineNumber());
        buf.append(",column=");
        buf.append(getColumnNumber());
        buf.append(",node=");
        buf.append(getNode());
        buf.append(",object=");
        buf.append(getObject());
        buf.append(",field=");
        buf.append(getFieldName());
        buf.append("]");
        
        return buf.toString();
    }
}
