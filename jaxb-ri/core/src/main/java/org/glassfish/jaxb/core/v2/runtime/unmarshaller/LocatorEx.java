/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.runtime.unmarshaller;

import java.net.URL;

import jakarta.xml.bind.ValidationEventLocator;

import org.xml.sax.Locator;
import org.w3c.dom.Node;

/**
 * Object that returns the current location that the {@code org.glassfish.jaxb.core.v2.runtime.unmarshaller.XmlVisitor}
 * is parsing.
 *
 * @author Kohsuke Kawaguchi
 */
public interface LocatorEx extends Locator {
    /**
     * Gets the current location in a {@link ValidationEventLocator} object.
     * @return 
     */
    ValidationEventLocator getLocation();

    /**
     * Immutable snapshot of a {@link LocatorEx}
     */
    public static final class Snapshot implements LocatorEx, ValidationEventLocator {
        private final int columnNumber,lineNumber,offset;
        private final String systemId,publicId;
        private final URL url;
        private final Object object;
        private final Node node;

        public Snapshot(LocatorEx loc) {
            columnNumber = loc.getColumnNumber();
            lineNumber = loc.getLineNumber();
            systemId = loc.getSystemId();
            publicId = loc.getPublicId();

            ValidationEventLocator vel = loc.getLocation();
            offset = vel.getOffset();
            url = vel.getURL();
            object = vel.getObject();
            node = vel.getNode();
        }

        public Object getObject() {
            return object;
        }

        public Node getNode() {
            return node;
        }

        public int getOffset() {
            return offset;
        }

        public URL getURL() {
            return url;
        }

        public int getColumnNumber() {
            return columnNumber;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public String getSystemId() {
            return systemId;
        }

        public String getPublicId() {
            return publicId;
        }

        public ValidationEventLocator getLocation() {
            return this;
        }
    }
}
