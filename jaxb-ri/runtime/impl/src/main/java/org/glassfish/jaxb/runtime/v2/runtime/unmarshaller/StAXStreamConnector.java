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

import org.glassfish.jaxb.core.WhiteSpaceProcessor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.lang.reflect.Constructor;

/**
 * Reads XML from StAX {@link XMLStreamReader} and
 * feeds events to {@link XmlVisitor}.
 * <p>
 * TODO:
 * Finding the optimized FI implementations is a bit hacky and not very
 * extensible. Can we use the service provider mechanism in general for 
 * concrete implementations of StAXConnector.
 *
 * @author Ryan.Shoemaker@Sun.COM
 * @author Kohsuke Kawaguchi
 * @version JAXB 2.0
 */
class StAXStreamConnector extends StAXConnector {

    /**
     * Creates a {@link StAXConnector} from {@link XMLStreamReader}.
     *
     * This method checks if the parser is FI parser and acts accordingly.
     */
    public static StAXConnector create(XMLStreamReader reader, XmlVisitor visitor) {
        // try optimized codepath
        final Class readerClass = reader.getClass();
        if (FI_STAX_READER_CLASS != null && FI_STAX_READER_CLASS.isAssignableFrom(readerClass) && FI_CONNECTOR_CTOR!=null) {
            try {
                return FI_CONNECTOR_CTOR.newInstance(reader,visitor);
            } catch (Exception t) {
            }
        }

        // Quick hack until SJSXP fixes 6270116
        boolean isZephyr = readerClass.getName().equals("com.sun.xml.stream.XMLReaderImpl");
        if (getBoolProp(reader,"org.codehaus.stax2.internNames") &&
            getBoolProp(reader,"org.codehaus.stax2.internNsUris"))
            ; // no need for interning
        else
        if (isZephyr)
            ; // no need for interning
        else
        if (checkImplementaionNameOfSjsxp(reader))
            ; // no need for interning.
        else
            visitor = new InterningXmlVisitor(visitor);

        if (STAX_EX_READER_CLASS!=null && STAX_EX_READER_CLASS.isAssignableFrom(readerClass)) {
            try {
                return STAX_EX_CONNECTOR_CTOR.newInstance(reader,visitor);
            } catch (Exception t) {
            }
        }

        return new StAXStreamConnector(reader,visitor);
    }

    private static boolean checkImplementaionNameOfSjsxp(XMLStreamReader reader) {
        try {
            Object name = reader.getProperty("http://java.sun.com/xml/stream/properties/implementation-name");
            return name!=null && name.equals("sjsxp");
        } catch (Exception e) {
            // be defensive against broken StAX parsers since javadoc is not clear
            // about when an error happens
            return false;
        }
    }

    private static boolean getBoolProp(XMLStreamReader r, String n) {
        try {
            Object o = r.getProperty(n);
            if(o instanceof Boolean)    return (Boolean)o;
            return false;
        } catch (Exception e) {
            // be defensive against broken StAX parsers since javadoc is not clear
            // about when an error happens
            return false;
        }
    }


    // StAX event source
    private final XMLStreamReader staxStreamReader;

    /**
     * SAX may fire consecutive characters event, but we don't allow it.
     * so use this buffer to perform buffering.
     */
    protected final StringBuilder buffer = new StringBuilder();

    /**
     * Set to true if the text() event is reported, and therefore
     * the following text() event should be suppressed.
     */
    protected boolean textReported = false;

    protected StAXStreamConnector(XMLStreamReader staxStreamReader, XmlVisitor visitor) {
        super(visitor);
        this.staxStreamReader = staxStreamReader;
    }

    @Override
    public void bridge() throws XMLStreamException {

        try {
            // remembers the nest level of elements to know when we are done.
            int depth=0;

            // if the parser is at the start tag, proceed to the first element
            int event = staxStreamReader.getEventType();
            if(event == XMLStreamConstants.START_DOCUMENT) {
                // nextTag doesn't correctly handle DTDs
                while( !staxStreamReader.isStartElement() )
                    event = staxStreamReader.next();
            }


            if( event!=XMLStreamConstants.START_ELEMENT)
                throw new IllegalStateException("The current event is not START_ELEMENT\n but " + event);

            handleStartDocument(staxStreamReader.getNamespaceContext());

            OUTER:
            while(true) {
                // These are all of the events listed in the javadoc for
                // XMLEvent.
                // The spec only really describes 11 of them.
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT :
                        handleStartElement();
                        depth++;
                        break;
                    case XMLStreamConstants.END_ELEMENT :
                        depth--;
                        handleEndElement();
                        if(depth==0)    break OUTER;
                        break;
                    case XMLStreamConstants.CHARACTERS :
                    case XMLStreamConstants.CDATA :
                    case XMLStreamConstants.SPACE :
                        handleCharacters();
                        break;
                    // otherwise simply ignore
                }

                event=staxStreamReader.next();
            }

            staxStreamReader.next();    // move beyond the end tag.

            handleEndDocument();
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    protected Location getCurrentLocation() {
        return staxStreamReader.getLocation();
    }

    @Override
    protected String getCurrentQName() {
        return getQName(staxStreamReader.getPrefix(),staxStreamReader.getLocalName());
    }

    private void handleEndElement() throws SAXException {
        processText(false);

        // fire endElement
        tagName.uri = fixNull(staxStreamReader.getNamespaceURI());
        tagName.local = staxStreamReader.getLocalName();
        visitor.endElement(tagName);

        // end namespace bindings
        int nsCount = staxStreamReader.getNamespaceCount();
        for (int i = nsCount - 1; i >= 0; i--) {
            visitor.endPrefixMapping(fixNull(staxStreamReader.getNamespacePrefix(i)));
        }
    }

    private void handleStartElement() throws SAXException {
        processText(true);

        // start namespace bindings
        int nsCount = staxStreamReader.getNamespaceCount();
        for (int i = 0; i < nsCount; i++) {
            visitor.startPrefixMapping(
                fixNull(staxStreamReader.getNamespacePrefix(i)),
                fixNull(staxStreamReader.getNamespaceURI(i)));
        }

        // fire startElement
        tagName.uri = fixNull(staxStreamReader.getNamespaceURI());
        tagName.local = staxStreamReader.getLocalName();
        tagName.atts = attributes;

        visitor.startElement(tagName);
    }

    /**
     * Proxy of {@link Attributes} that read from {@link XMLStreamReader}.
     */
    private final Attributes attributes = new Attributes() {
        @Override
        public int getLength() {
            return staxStreamReader.getAttributeCount();
        }

        @Override
        public String getURI(int index) {
            String uri = staxStreamReader.getAttributeNamespace(index);
            if(uri==null)   return "";
            return uri;
        }

        @Override
        public String getLocalName(int index) {
            return staxStreamReader.getAttributeLocalName(index);
        }

        @Override
        public String getQName(int index) {
            String prefix = staxStreamReader.getAttributePrefix(index);
            if(prefix==null || prefix.length()==0)
                return getLocalName(index);
            else
                return prefix + ':' + getLocalName(index);
        }

        @Override
        public String getType(int index) {
            return staxStreamReader.getAttributeType(index);
        }

        @Override
        public String getValue(int index) {
            return staxStreamReader.getAttributeValue(index);
        }

        @Override
        public int getIndex(String uri, String localName) {
            for( int i=getLength()-1; i>=0; i-- )
                if( localName.equals(getLocalName(i)) && uri.equals(getURI(i)))
                    return i;
            return -1;
        }

        // this method sholdn't be used that often (if at all)
        // so it's OK to be slow.
        @Override
        public int getIndex(String qName) {
            for( int i=getLength()-1; i>=0; i-- ) {
                if(qName.equals(getQName(i)))
                    return i;
            }
            return -1;
        }

        @Override
        public String getType(String uri, String localName) {
            int index = getIndex(uri,localName);
            if(index<0)     return null;
            return getType(index);
        }

        @Override
        public String getType(String qName) {
            int index = getIndex(qName);
            if(index<0)     return null;
            return getType(index);
        }

        @Override
        public String getValue(String uri, String localName) {
            int index = getIndex(uri,localName);
            if(index<0)     return null;
            return getValue(index);
        }

        @Override
        public String getValue(String qName) {
            int index = getIndex(qName);
            if(index<0)     return null;
            return getValue(index);
        }
    };

    protected void handleCharacters() throws XMLStreamException, SAXException {
        if( predictor.expectText() )
            buffer.append(
                staxStreamReader.getTextCharacters(),
                staxStreamReader.getTextStart(),
                staxStreamReader.getTextLength() );
    }

    private void processText( boolean ignorable ) throws SAXException {
        if( predictor.expectText() && (!ignorable || !WhiteSpaceProcessor.isWhiteSpace(buffer) || context.getCurrentState().isMixed())) {
            if(textReported) {
                textReported = false;
            } else {
                visitor.text(buffer);
            }
        }
        buffer.setLength(0);
    }



    /**
     * Reference to FI's StAXReader class, if FI can be loaded.
     */
    private static final Class FI_STAX_READER_CLASS = initFIStAXReaderClass();
    private static final Constructor<? extends StAXConnector> FI_CONNECTOR_CTOR = initFastInfosetConnectorClass();

    private static Class initFIStAXReaderClass() {
        try {
            Class<?> fisr = Class.forName("org.jvnet.fastinfoset.stax.FastInfosetStreamReader");
            Class<?> sdp = Class.forName("com.sun.xml.fastinfoset.stax.StAXDocumentParser");
            // Check if StAXDocumentParser implements FastInfosetStreamReader
            if (fisr.isAssignableFrom(sdp))
                return sdp;
            else
                return null;
        } catch (Throwable e) {
            return null;
        }
    }

    private static Constructor<? extends StAXConnector> initFastInfosetConnectorClass() {
        try {
            if (FI_STAX_READER_CLASS == null)
                return null;
            
            Class c = Class.forName(
                    "org.glassfish.jaxb.core.v2.runtime.unmarshaller.FastInfosetConnector");                
            return c.getConstructor(FI_STAX_READER_CLASS,XmlVisitor.class);
        } catch (Throwable e) {
            return null;
        }
    }

    //
    // reference to StAXEx classes
    //
    private static final Class STAX_EX_READER_CLASS = initStAXExReader();
    private static final Constructor<? extends StAXConnector> STAX_EX_CONNECTOR_CTOR = initStAXExConnector();

    private static Class initStAXExReader() {
        try {
            return Class.forName("org.jvnet.staxex.XMLStreamReaderEx");
        } catch (Throwable e) {
            return null;
        }
    }

    private static Constructor<? extends StAXConnector> initStAXExConnector() {
        try {
            Class c = Class.forName("org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.StAXExConnector");
            return c.getConstructor(STAX_EX_READER_CLASS,XmlVisitor.class);
        } catch (Throwable e) {
            return null;
        }
    }

}
