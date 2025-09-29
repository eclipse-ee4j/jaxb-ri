/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.glassfish.jaxb.runtime.v2.runtime;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Date;

@XmlRootElement(name = "illegalAnnotationsExceptionDto")
public class IllegalAnnotationsExceptionDTO {

    @XmlAttribute(name = "field")
    protected Date field;

    public Date getField() {
        return field;
    }

    public void setField(Date field) {
        this.field = field;
    }
}
