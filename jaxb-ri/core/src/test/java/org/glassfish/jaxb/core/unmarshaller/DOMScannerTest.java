/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class DOMScannerTest extends TestCase {
	
	public void testParentDefaultNamespace() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("parentDefaultNs.xml");
		Document doc = builder.parse(is);
		NodeList testElems = doc.getElementsByTagName("test");
		Node testElem = testElems.item(0);
		
		DOMScanner scanner = new DOMScanner();
		MockContentHandler mockHandler = new MockContentHandler();
		Map<String,String> assertMapping = new HashMap<String,String>();
		assertMapping.put("xml", "http://www.w3.org/XML/1998/namespace");
		assertMapping.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		assertMapping.put("", "http://example.com/test/ns");
		mockHandler.setAssertion("value", assertMapping);
		scanner.setContentHandler(mockHandler);
		scanner.scan(testElem);
	}
	
	private class MockContentHandler implements ContentHandler {
		
		private Map<String,String> prefixMapping = new HashMap<String,String>();
		
		private Map<String,String> assertMapping = new HashMap<String,String>();
		private String assertElement = null;
		
		public void setAssertion(String elementName, Map<String,String> mapping) {
			this.assertElement = elementName;
			this.assertMapping.putAll(mapping);
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			
		}

		@Override
		public void startDocument() throws SAXException {
			
		}

		@Override
		public void endDocument() throws SAXException {
			
		}

		@Override
		public void startPrefixMapping(String prefix, String uri) throws SAXException {
			prefixMapping.put(prefix, uri);
		}

		@Override
		public void endPrefixMapping(String prefix) throws SAXException {
			prefixMapping.remove(prefix);
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
			if (assertElement != null && assertElement.equals(localName)) {
				assertTrue("Prefix mapping does not match expected mapping", prefixMapping.equals(assertMapping));
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			
		}

		@Override
		public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
			
		}

		@Override
		public void processingInstruction(String target, String data) throws SAXException {
			
		}

		@Override
		public void skippedEntity(String name) throws SAXException {
			
		}
		
	}

}
