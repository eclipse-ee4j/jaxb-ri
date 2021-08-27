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

import org.glassfish.jaxb.runtime.v2.util.FatalAdapter;
import org.glassfish.jaxb.core.v2.runtime.unmarshaller.LocatorEx;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;

/**
 * {@link XmlVisitor} decorator that validates the events by using JAXP validation API.
 *
 * @author Kohsuke Kawaguchi
 */
final class ValidatingUnmarshaller implements XmlVisitor, XmlVisitor.TextPredictor {
    
    private final XmlVisitor next;
    private final ValidatorHandler validator;
    private NamespaceContext nsContext = null;

    /**
     * {@link TextPredictor} of the next {@link XmlVisitor}.
     */
    private final TextPredictor predictor;

    private char[] buf = new char[256];

    /**
     * Creates a new instance of ValidatingUnmarshaller.
     */
    public ValidatingUnmarshaller( Schema schema, XmlVisitor next ) {
        this.validator = schema.newValidatorHandler();
        this.next = next;
        this.predictor = next.getPredictor();
        // if the user bothers to use a validator, make validation errors fatal
        // so that it will abort unmarshalling.
        validator.setErrorHandler(new FatalAdapter(getContext()));
    }

    @Override
    public void startDocument(LocatorEx locator, NamespaceContext nsContext) throws SAXException {
        this.nsContext = nsContext;
        validator.setDocumentLocator(locator);
        validator.startDocument();
        next.startDocument(locator,nsContext);
    }

    @Override
    public void endDocument() throws SAXException {
        this.nsContext = null;
        validator.endDocument();
        next.endDocument();
    }

    @Override
    public void startElement(TagName tagName) throws SAXException {
        if(nsContext != null) {
            String tagNamePrefix = tagName.getPrefix().intern();
            if(tagNamePrefix != "") {
                validator.startPrefixMapping(tagNamePrefix, nsContext.getNamespaceURI(tagNamePrefix));
            }
        }
        validator.startElement(tagName.uri,tagName.local,tagName.getQname(),tagName.atts);
        next.startElement(tagName);
    }

    @Override
    public void endElement(TagName tagName ) throws SAXException {
        validator.endElement(tagName.uri,tagName.local,tagName.getQname());
        next.endElement(tagName);
    }

    @Override
    public void startPrefixMapping(String prefix, String nsUri) throws SAXException {
        validator.startPrefixMapping(prefix,nsUri);
        next.startPrefixMapping(prefix,nsUri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        validator.endPrefixMapping(prefix);
        next.endPrefixMapping(prefix);
    }

    @Override
    public void text( CharSequence pcdata ) throws SAXException {
        int len = pcdata.length();
        if(buf.length<len) {
            buf = new char[len];
        }
        for( int i=0;i<len; i++ )
            buf[i] = pcdata.charAt(i);  // isn't this kinda slow?

        validator.characters(buf,0,len);
        if(predictor.expectText())
            next.text(pcdata);
    }

    @Override
    public UnmarshallingContext getContext() {
        return next.getContext();
    }

    @Override
    public TextPredictor getPredictor() {
        return this;
    }

    // should be always invoked through TextPredictor
    @Deprecated
    @Override
    public boolean expectText() {
        // validator needs to make sure that there's no text
        // even when it's not expected. So always have them
        // send text, ignoring optimization hints from the unmarshaller
        return true;
    }
}
