/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime;

import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import jakarta.xml.bind.JAXBException;

import java.lang.reflect.Field;

/**
 * A means to allow the user to provide customized Accessor  
 * to be used by JAXB. Adds ability to suppress warnings.
 */
public interface InternalAccessorFactory extends AccessorFactory {
    /**
     * Access a field of the class. 
     *
     * @param bean the class to be processed.
     * @param f the field within the class to be accessed.
     * @param readOnly  the isStatic value of the field's modifier.
     * @param supressWarnings suppress reflection warnings
     * @return Accessor the accessor for this field
     *
     * @throws JAXBException reports failures of the method.
     */
    Accessor createFieldAccessor(Class bean, Field f, boolean readOnly, boolean supressWarnings) throws JAXBException;
    
}
