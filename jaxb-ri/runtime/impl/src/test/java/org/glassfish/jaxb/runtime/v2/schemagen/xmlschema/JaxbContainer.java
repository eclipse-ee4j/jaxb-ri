/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.schemagen.xmlschema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "containerType", propOrder = {
        "deployments"
})
@XmlSeeAlso({JaxbConcreteContainer.class})
public abstract class JaxbContainer {

    public static final String ELEMENT_NAME = "container";

    /**
     * Deployments that this container-managed datasource is intended to be used by.
     */
    @XmlIDREF
    @XmlElementWrapper(name = "deployments", required = true)
    @XmlElement(name = JaxbDeployment.ELEMENT_NAME)
    private List<JaxbDeployment> deployments;

    /**
     * Creates new instance of JaxbContainer with empty version, intended for JAXB purposes only!
     */
    protected JaxbContainer() {
        this.deployments = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    public List<JaxbDeployment> getDeployments() {
        return Collections.unmodifiableList(this.deployments);
    }

    public void setDeployments(final List<JaxbDeployment> deployments) {
        this.deployments = deployments;
    }

    /**
     * Adds given Deployment into the list of deployments which use this datasource.
     *
     * @param deployment Deployment to be added, must not be null
     */
    public void addDeployment(final JaxbDeployment deployment) {
        if (deployment == null) {
            throw new IllegalArgumentException("Cannot add null Deployment!");
        }
        this.deployments.add(deployment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JaxbContainer that = (JaxbContainer) o;

        return !(deployments != null ? !deployments.equals(that.deployments) : that.deployments != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (deployments != null ? deployments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JaxbContainer{" +
                "deployments=" + deployments +
                '}';
    }
}
