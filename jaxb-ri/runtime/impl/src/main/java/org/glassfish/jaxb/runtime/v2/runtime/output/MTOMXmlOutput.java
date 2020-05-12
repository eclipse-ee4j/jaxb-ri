/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.output;

import org.glassfish.jaxb.core.v2.WellKnownNamespace;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Base64Data;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * {@link XmlOutput} decorator that supports MTOM.
 *
 * @author Kohsuke Kawaguchi
 */
public final class MTOMXmlOutput extends XmlOutputAbstractImpl {

    private final XmlOutput next;

    /**
     * Remembers the last namespace URI and local name so that we can pass them to
     * {@link AttachmentMarshaller}.
     */
    private String nsUri,localName;

    public MTOMXmlOutput(XmlOutput next) {
        this.next = next;
    }

    public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext) throws IOException, SAXException, XMLStreamException {
        super.startDocument(serializer,fragment,nsUriIndex2prefixIndex, nsContext);
        next.startDocument(serializer, fragment, nsUriIndex2prefixIndex, nsContext);
    }

    public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
        next.endDocument(fragment);
        super.endDocument(fragment);
    }

    public void beginStartTag(Name name) throws IOException, XMLStreamException {
        next.beginStartTag(name);
        this.nsUri = name.nsUri;
        this.localName = name.localName;
    }

    public void beginStartTag(int prefix, String localName) throws IOException, XMLStreamException {
        next.beginStartTag(prefix, localName);
        this.nsUri = nsContext.getNamespaceURI(prefix);
        this.localName = localName;
    }

    public void attribute( Name name, String value ) throws IOException, XMLStreamException {
        next.attribute(name, value);
    }

    public void attribute( int prefix, String localName, String value ) throws IOException, XMLStreamException {
        next.attribute(prefix, localName, value);
    }

    public void endStartTag() throws IOException, SAXException {
        next.endStartTag();
    }

    public void endTag(Name name) throws IOException, SAXException, XMLStreamException {
        next.endTag(name);
    }

    public void endTag(int prefix, String localName) throws IOException, SAXException, XMLStreamException {
        next.endTag(prefix, localName);
    }

    public void text( String value, boolean needsSeparatingWhitespace ) throws IOException, SAXException, XMLStreamException {
        next.text(value,needsSeparatingWhitespace);
    }

    public void text( Pcdata value, boolean needsSeparatingWhitespace ) throws IOException, SAXException, XMLStreamException {
        if(value instanceof Base64Data && !serializer.getInlineBinaryFlag()) {
            Base64Data b64d = (Base64Data) value;
            String cid;
            if(b64d.hasData())
                cid = serializer.attachmentMarshaller.addMtomAttachment(
                                b64d.get(),0,b64d.getDataLen(),b64d.getMimeType(),nsUri,localName);
            else
                cid = serializer.attachmentMarshaller.addMtomAttachment(
                    b64d.getDataHandler(),nsUri,localName);

            if(cid!=null) {
                nsContext.getCurrent().push();
                int prefix = nsContext.declareNsUri(WellKnownNamespace.XOP,"xop",false);
                beginStartTag(prefix,"Include");
                attribute(-1,"href",cid);
                endStartTag();
                endTag(prefix,"Include");
                nsContext.getCurrent().pop();
                return;
            }
        }
        next.text(value, needsSeparatingWhitespace);
    }
}
