/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.schemagen;

import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.LocalAttribute;
import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.LocalElement;
import org.glassfish.jaxb.runtime.v2.schemagen.xmlschema.Schema;
import com.sun.xml.txw2.TypedXmlWriter;
import jakarta.xml.bind.annotation.XmlNsForm;

import javax.xml.namespace.QName;

/**
 * Represents the form default value.
 *
 * @author Kohsuke Kawaguchi
 */
enum Form {
    QUALIFIED(XmlNsForm.QUALIFIED,true) {
        void declare(String attName, Schema schema) {
            schema._attribute(attName,"qualified");
        }
    },
    UNQUALIFIED(XmlNsForm.UNQUALIFIED,false) {
        void declare(String attName,Schema schema) {
            // pointless, but required by the spec.
            // people need to understand that @attributeFormDefault is a syntax sugar
            schema._attribute(attName,"unqualified");
        }
    },
    UNSET(XmlNsForm.UNSET,false) {
        void declare(String attName,Schema schema) {
        }
    };

    /**
     * The same constant defined in the spec.
     */
    private final XmlNsForm xnf;

    /**
     * What's the effective value? UNSET means unqualified per XSD spec.)
     */
    public final boolean isEffectivelyQualified;

    Form(XmlNsForm xnf, boolean effectivelyQualified) {
        this.xnf = xnf;
        this.isEffectivelyQualified = effectivelyQualified;
    }

    /**
     * Writes the attribute on the generated {@code <schema>} element.
     */
    abstract void declare(String attName, Schema schema);

    /**
     * Given the effective 'form' value, write (or suppress) the @form attribute
     * on the generated XML.
     */
    public void writeForm(LocalElement e, QName tagName) {
        _writeForm(e,tagName);
    }

    public void writeForm(LocalAttribute a, QName tagName) {
        _writeForm(a,tagName);
    }

    private void _writeForm(TypedXmlWriter e, QName tagName) {
        boolean qualified = tagName.getNamespaceURI().length()>0;

        if(qualified && this!=QUALIFIED)
            e._attribute("form","qualified");
        else
        if(!qualified && this==QUALIFIED)
            e._attribute("form","unqualified");
    }

    /**
     * Gets the constant the corresponds to the given {@link XmlNsForm}.
     */
    public static Form get(XmlNsForm xnf) {
        for (Form v : values()) {
            if(v.xnf==xnf)
                return v;
        }
        throw new IllegalArgumentException();
    }

}
