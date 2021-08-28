/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2;

import com.sun.xml.txw2.output.XmlSerializer;
import com.sun.xml.txw2.output.TXWSerializer;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.annotation.XmlNamespace;

import javax.xml.namespace.QName;

/**
 * Entry point to TXW.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class TXW {
    private TXW() {}    // no instanciation please


    /*package*/ static QName getTagName( Class<?> c ) {
        String localName="";
        String nsUri="##default";

        XmlElement xe = c.getAnnotation(XmlElement.class);
        if(xe!=null) {
            localName = xe.value();
            nsUri = xe.ns();
        }

        if(localName.length()==0) {
            localName = c.getName();
            int idx = localName.lastIndexOf('.');
            if(idx>=0)
                localName = localName.substring(idx+1);

            localName = Character.toLowerCase(localName.charAt(0))+localName.substring(1);
        }

        if(nsUri.equals("##default")) {
            Package pkg = c.getPackage();
            if(pkg!=null) {
                XmlNamespace xn = pkg.getAnnotation(XmlNamespace.class);
                if(xn!=null)
                    nsUri = xn.value();
            }
        }
        if(nsUri.equals("##default"))
            nsUri = "";

        return new QName(nsUri,localName);
    }

    /**
     * Creates a new {@link TypedXmlWriter} to write a new instance of a document.
     *
     * @param <T> an instance of {@link TypedXmlWriter}
     * @param rootElement
     *      The {@link TypedXmlWriter} interface that declares the content model of the root element.
     *      This interface must have {@link XmlElement} annotation on it to designate the tag name
     *      of the root element.
     * @param out
     *      The target of the writing.
     * @return
     *      always return non-null {@link TypedXmlWriter} that can be used
     *      to write the contents of the root element.
     */
    public static <T extends TypedXmlWriter> T create( Class<T> rootElement, XmlSerializer out ) {
        if (out instanceof TXWSerializer) {
            TXWSerializer txws = (TXWSerializer) out;
            return txws.txw._element(rootElement);
        }

        Document doc = new Document(out);
        QName n = getTagName(rootElement);
        return new ContainerElement(doc,null,n.getNamespaceURI(),n.getLocalPart())._cast(rootElement);
    }

    /**
     * Creates a new {@link TypedXmlWriter} to write a new instance of a document.
     *
     * <p>
     * Similar to the other method, but this version allows the caller to set the
     * tag name at the run-time.
     *
     * @param <T> an instance of {@link TypedXmlWriter}
     * @param tagName
     *      The tag name of the root document.
     * @param rootElement
     *      The {@link TypedXmlWriter} interface that declares the content model of the root element.
     *      This interface must have {@link XmlElement} annotation on it to designate the tag name
     *      of the root element.
     * @param out
     *      The target of the writing.
     * @return
     *      always return non-null {@link TypedXmlWriter} that can be used
     *      to write the contents of the root element.
     *
     * @see #create(Class,XmlSerializer)
     */
    public static <T extends TypedXmlWriter> T create( QName tagName, Class<T> rootElement, XmlSerializer out ) {
        if (out instanceof TXWSerializer) {
            TXWSerializer txws = (TXWSerializer) out;
            return txws.txw._element(tagName,rootElement);
        }
        return new ContainerElement(new Document(out),null,tagName.getNamespaceURI(),tagName.getLocalPart())._cast(rootElement);
    }
}
