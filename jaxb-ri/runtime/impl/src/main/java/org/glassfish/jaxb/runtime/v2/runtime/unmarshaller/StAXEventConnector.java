/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.util.Iterator;

/**
 * This is a simple utility class that adapts StAX events from an
 * {@link XMLEventReader} to unmarshaller events on a
 * {@link XmlVisitor}, bridging between the two
 * parser technologies.
 *
 * @author Ryan.Shoemaker@Sun.COM
 * @version 1.0
 */
final class StAXEventConnector extends StAXConnector {

    // StAX event source
    private final XMLEventReader staxEventReader;

    /** Current event. */
    private XMLEvent event;

    /**
     * Shared and reused {@link Attributes}.
     */
    private final AttributesImpl attrs = new AttributesImpl();

    /**
     * SAX may fire consective characters event, but we don't allow it.
     * so use this buffer to perform buffering.
     */
    private final StringBuilder buffer = new StringBuilder();

    private boolean seenText;

    /**
     * Construct a new StAX to SAX adapter that will convert a StAX event
     * stream into a SAX event stream.
     * 
     * @param staxCore
     *                StAX event source
     * @param visitor
     *                sink
     */
    public StAXEventConnector(XMLEventReader staxCore, XmlVisitor visitor) {
        super(visitor);
        staxEventReader = staxCore;
    }

    @Override
    public void bridge() throws XMLStreamException {

        try {
            // remembers the nest level of elements to know when we are done.
            int depth=0;

            event = staxEventReader.peek();

            if( !event.isStartDocument() && !event.isStartElement() )
                throw new IllegalStateException();

            // if the parser is on START_DOCUMENT, skip ahead to the first element
            do {
                event = staxEventReader.nextEvent();
            } while( !event.isStartElement() );

            handleStartDocument(event.asStartElement().getNamespaceContext());

            OUTER:
            while(true) {
                // These are all of the events listed in the javadoc for
                // XMLEvent.
                // The spec only really describes 11 of them.
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT :
                        handleStartElement(event.asStartElement());
                        depth++;
                        break;
                    case XMLStreamConstants.END_ELEMENT :
                        depth--;
                        handleEndElement(event.asEndElement());
                        if(depth==0)    break OUTER;
                        break;
                    case XMLStreamConstants.CHARACTERS :
                    case XMLStreamConstants.CDATA :
                    case XMLStreamConstants.SPACE :
                        handleCharacters(event.asCharacters());
                        break;
                }


                event=staxEventReader.nextEvent();
            }

            handleEndDocument();
            event = null; // avoid keeping a stale reference
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    protected Location getCurrentLocation() {
        return event.getLocation();
    }

    @Override
    protected String getCurrentQName() {
        QName qName;
        if(event.isEndElement())
            qName = event.asEndElement().getName();
        else
            qName = event.asStartElement().getName();
        return getQName(qName.getPrefix(), qName.getLocalPart());
    }


    private void handleCharacters(Characters event) throws SAXException, XMLStreamException {
        if(!predictor.expectText())
            return;     // text isn't expected. simply skip

        seenText = true;

        // check the next event
        XMLEvent next;
        while(true) {
            next = staxEventReader.peek();
            if(!isIgnorable(next))
                break;
            staxEventReader.nextEvent();
        }

        if(isTag(next)) {
            // this is by far the common case --- you have <foo>abc</foo> or <foo>abc<bar/>...</foo>
            visitor.text(event.getData());
            return;
        }

        // otherwise we have things like "abc<!-- test -->def".
        // concatenate all text
        buffer.append(event.getData());

        while(true) {
            while(true) {
                next = staxEventReader.peek();
                if(!isIgnorable(next))
                    break;
                staxEventReader.nextEvent();
            }

            if(isTag(next)) {
                // found all adjacent text
                visitor.text(buffer);
                buffer.setLength(0);
                return;
            }

            buffer.append(next.asCharacters().getData());
            staxEventReader.nextEvent();    // consume
        }
    }

    private boolean isTag(XMLEvent event) {
        int eventType = event.getEventType();
        return eventType==XMLEvent.START_ELEMENT || eventType==XMLEvent.END_ELEMENT;
    }

    private boolean isIgnorable(XMLEvent event) {
        int eventType = event.getEventType();
        return eventType==XMLEvent.COMMENT || eventType==XMLEvent.PROCESSING_INSTRUCTION;
    }

    private void handleEndElement(EndElement event) throws SAXException {
        if(!seenText && predictor.expectText()) {
            visitor.text("");
        }

        // fire endElement
        QName qName = event.getName();
        tagName.uri = fixNull(qName.getNamespaceURI());
        tagName.local = qName.getLocalPart();
        visitor.endElement(tagName);

        // end namespace bindings
        for( Iterator<Namespace> i = event.getNamespaces(); i.hasNext();) {
            String prefix = fixNull(i.next().getPrefix());  // be defensive
            visitor.endPrefixMapping(prefix);
        }

        seenText = false;
    }

    private void handleStartElement(StartElement event) throws SAXException {
        // start namespace bindings
        for (Iterator i = event.getNamespaces(); i.hasNext();) {
            Namespace ns = (Namespace)i.next();
            visitor.startPrefixMapping(
                fixNull(ns.getPrefix()),
                fixNull(ns.getNamespaceURI()));
        }

        // fire startElement
        QName qName = event.getName();
        tagName.uri = fixNull(qName.getNamespaceURI());
        String localName = qName.getLocalPart();
        tagName.uri = fixNull(qName.getNamespaceURI());
        tagName.local = localName;
        tagName.atts = getAttributes(event);
        visitor.startElement(tagName);

        seenText = false;
    }



    /**
     * Get the attributes associated with the given START_ELEMENT StAXevent.
     *
     * @return the StAX attributes converted to an org.xml.sax.Attributes
     */
    private Attributes getAttributes(StartElement event) {
        attrs.clear();

        // in SAX, namespace declarations are not part of attributes by default.
        // (there's a property to control that, but as far as we are concerned
        // we don't use it.) So don't add xmlns:* to attributes.

        // gather non-namespace attrs
        for (Iterator i = event.getAttributes(); i.hasNext();) {
            Attribute staxAttr = (Attribute)i.next();

            QName name = staxAttr.getName();
            String uri = fixNull(name.getNamespaceURI());
            String localName = name.getLocalPart();
            String prefix = name.getPrefix();
            String qName;
            if (prefix == null || prefix.length() == 0)
                qName = localName;
            else
                qName = prefix + ':' + localName;
            String type = staxAttr.getDTDType();
            String value = staxAttr.getValue();
            
            attrs.addAttribute(uri, localName, qName, type, value);
        }

        return attrs;
    }
}
