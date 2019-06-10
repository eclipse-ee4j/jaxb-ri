/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.schemagen.xmlidref;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deploymentType")
@XmlSeeAlso({JaxbConcreteDeployment.class})
public abstract class JaxbDeployment {

    public static final String ELEMENT_NAME = "deployment";

    /**
     * The context root of this deployment, e.g. 'business-central'.
     */
    @XmlID
    @XmlElement(required = true)
    private String contextRoot;

    /**
     * {@inheritDoc}
     */
    public String getContextRoot() {
        return this.contextRoot;
    }

    public void setContextRoot(final String contextRoot) {
        this.contextRoot = contextRoot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JaxbDeployment that = (JaxbDeployment) o;

        return contextRoot != null ? contextRoot.equals(that.contextRoot) : that.contextRoot == null;
    }

    @Override
    public int hashCode() {
        return contextRoot != null ? contextRoot.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "JaxbDeployment{" +
                "contextRoot='" + contextRoot + '\'' +
                '}';
    }
}
