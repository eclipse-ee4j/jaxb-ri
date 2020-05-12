/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.core.v2.WellKnownNamespace;
import org.glassfish.jaxb.core.v2.runtime.unmarshaller.LocatorEx;
import jakarta.activation.DataHandler;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;

/**
 * Decorator of {@link XmlVisitor} that performs XOP processing.
 * Used to support MTOM.
 *
 * @author Kohsuke Kawaguchi
 */
final class MTOMDecorator implements XmlVisitor {

    private final XmlVisitor next;

    private final AttachmentUnmarshaller au;

    private UnmarshallerImpl parent;

    private final Base64Data base64data = new Base64Data();

    /**
     * True if we are between the start and the end of xop:Include
     */
    private boolean inXopInclude;

    /**
     * UGLY HACK: we need to ignore the whitespace that follows
     * the attached base64 image.
     *
     * This happens twice; once before {@code </xop:Include>}, another
     * after {@code </xop:Include>}. The spec guarantees that
     * no valid pcdata can follow {@code </xop:Include>}.
     */
    private boolean followXop;

    public MTOMDecorator(UnmarshallerImpl parent,XmlVisitor next, AttachmentUnmarshaller au) {
        this.parent = parent;
        this.next = next;
        this.au = au;
    }

    public void startDocument(LocatorEx loc, NamespaceContext nsContext) throws SAXException {
        next.startDocument(loc,nsContext);
    }

    public void endDocument() throws SAXException {
        next.endDocument();
    }

    public void startElement(TagName tagName) throws SAXException {
        if(tagName.local.equals("Include") && tagName.uri.equals(WellKnownNamespace.XOP)) {
            // found xop:Include
            String href = tagName.atts.getValue("href");
            DataHandler attachment = au.getAttachmentAsDataHandler(href);
            if(attachment==null) {
                // report an error and ignore
                parent.getEventHandler().handleEvent(null);
                // TODO
            }
            base64data.set(attachment);
            next.text(base64data);
            inXopInclude = true;
            followXop = true;
        } else
            next.startElement(tagName);
    }

    public void endElement(TagName tagName) throws SAXException {
        if(inXopInclude) {
            // consume </xop:Include> by ourselves.
            inXopInclude = false;
            followXop = true;
            return;
        }
        next.endElement(tagName);
    }

    public void startPrefixMapping(String prefix, String nsUri) throws SAXException {
        next.startPrefixMapping(prefix,nsUri);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
        next.endPrefixMapping(prefix);
    }

    public void text( CharSequence pcdata ) throws SAXException {
        if(!followXop)
            next.text(pcdata);
        else
            followXop = false;
    }

    public UnmarshallingContext getContext() {
        return next.getContext();
    }

    public TextPredictor getPredictor() {
        return next.getPredictor();
    }
}
