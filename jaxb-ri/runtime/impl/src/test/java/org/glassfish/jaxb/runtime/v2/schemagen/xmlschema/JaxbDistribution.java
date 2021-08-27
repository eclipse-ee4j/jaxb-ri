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
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlType;

/**
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "distributionType", propOrder = {
        "deployments"
})
public class JaxbDistribution {

    public static final String ELEMENT_NAME = "distribution";

    /**
     * The list of product deployments that are part of this distribution package.
     */
    @XmlElementWrapper(name = "deployments", required = true)
    @XmlElement(name = JaxbDeployment.ELEMENT_NAME)
    private List<JaxbDeployment> deployments;

    /**
     * Creates new instance of this class with default values!
     */
    public JaxbDistribution() {
        this.deployments = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    public List<JaxbDeployment> getDeployments() {
        return this.deployments;
    }

    public void setDeployments(final List<JaxbDeployment> deployments) {
        this.deployments = deployments;
    }

    /**
     * Adds given product deployment into the list of deployments that are part of this distribution.
     *
     * @param deployment product deployment to be added, must not be null
     */
    public void addDeployment(final JaxbDeployment deployment) {
        if (deployment == null) {
            throw new IllegalArgumentException("Cannot add null deployment!");
        }
        this.deployments.add(deployment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final JaxbDistribution that = (JaxbDistribution) o;

        return deployments != null ? deployments.equals(that.deployments) : that
                .deployments == null;

    }

    @Override
    public int hashCode() {
        int result = deployments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JaxbDistribution{" +
                "deployments=" + deployments +
                '}';
    }
}

