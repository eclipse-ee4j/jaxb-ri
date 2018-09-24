/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import binder.*;

/**
 * Illustrate Binder use cases from JAXB 2.0 specification.
 */
public class UpdateablePartialBinding {
    public static void main(String[] args) throws Exception {
        File input = new File("po.xml");
        File output = new File("processedpo.xml");

        // create a JAXBContext capable of handling classes generated into
        // the binder package
        JAXBContext jc = JAXBContext.newInstance("binder");

        // Load XML document into DOM
        Document doc = loadDocument(input);

        System.out.println("Using binder to perform xpath that returns JAXB objects");
        xpathUsingBinder(jc.createBinder(), doc);
        System.out.println("Completed jaxbXpath");
        System.out.println();

        System.out.println("Updateable partial binding allowing for schema evolution and preservations of comments in XML document");
        updateablePartialBind(jc.createBinder(), doc);

        saveDocument(doc, output);
        System.out.println("Wrote updated DOM representation to file: " + output);

    }

    /**
     * JAXB xpath implemented leveraging Binder and JAXP 1.3 XPath.
     * 
     * KNOWN Limitations: 
     * xpath resolving to an XML attribute can not work since Binder.getJAXBNode() only works for Element, not Attribute.
     */
    static public class JAXBXpath {
        private final Document document;
        private final Binder<Node> binder;
        private Object jaxbRootObject;

        public Document getDocument() {
            return document;
        }

        public Binder getBinder() {
            return binder;
        }

        public Object getJaxbRootObject() {
            return jaxbRootObject;
        }

        public JAXBXpath(org.w3c.dom.Document doc, Binder<Node> b) throws JAXBException {
            binder = b;
            document = doc;
            // bind entire DOM document to JAXB
            jaxbRootObject = binder.unmarshal(document);
        }

        /**
         * @param xpathExpr can match zero, one or more nodes in document.
         * @return List of JAXB objects matching xpathExpr.
         */
        private List<Object> evaluateToMany(String xpathExpr) {
            List<Object> resultList = new ArrayList<Object>();
            for( Node node : xpath(document, xpathExpr) ) {
                resultList.add(binder.getJAXBNode(node));
            }
            return resultList;
        }

        /**
	 * Return JAXB object representing first match of <code>xpathExpr</code> over {@link getDocument()}
         * @param xpathExpr should only match one node in document.
         * @return JAXB object matching xpathExpr.
         */
        private Object evaluate(String xpathExpr) {
            List<Node> nodes = xpath(document, xpathExpr);
            if(nodes.size()>0)
                return binder.getJAXBNode(nodes.get(0));
            else
                return null;
        }
    }

    static public void xpathUsingBinder(Binder<Node> binder, Document doc) throws JAXBException {
        JAXBXpath jaxbXpath = new JAXBXpath(doc, binder);

        //use xpath over DOM to find specific JAXB bound objects.
        System.out.println("Ship Address found by xpath");
        displayAddress((USAddress)
                jaxbXpath.evaluate("/purchaseOrder/shipTo"));

        System.out.println("Bill Address found by xpath");
        displayAddress((USAddress)
                jaxbXpath.evaluate("/purchaseOrder/billTo"));

        System.out.println("items over $25 found by xpath");
        for (Object item : jaxbXpath.evaluateToMany(".//item[USPrice>=25.00]")) {
            displayItem((Items.Item) item);
        }
    }

    public static void displayAddress(USAddress address) {
        // display the address
        System.out.println("\t" + address.getName());
        System.out.println("\t" + address.getStreet());
        System.out.printf("\t%1s, %2s %3s\n",
                address.getCity(), address.getState(), address.getZip());
        System.out.println("\t" + address.getCountry() + "\n");
    }

    public static void displayItem(Items.Item item) {
        String result =
                "\t" + item.getQuantity() +
                        " copies of \"" + item.getProductName() +
                        "\"" + " price= " + item.getUSPrice().toString();
        if (item.getComment() != null) {
            result += " comment: " + item.getComment();
        }
        System.out.println(result);
    }


    static public void updateablePartialBind(Binder<Node> binder, Document doc) throws JAXBException {

        // Partially bind XML document to Java.
        // Only Nodes matching XPath are bound to JAXB objects.
        // Binder retains association between DOM and JAXB views.
        for( Node node : xpath(doc, "//item[USPrice>=25.00]") ) {
            // Unmarshal by declared type since element Item corresponds with a local element declaration in schema.
            // Partial Bind DOM Element"item" to instance of JAXB mapped binder.Items.Item class
            JAXBElement<Items.Item> itemE = binder.unmarshal(node, Items.Item.class);
            binder.Items.Item item = itemE.getValue();

            // Modify in Java
            item.setComment("qualifies for free shipping");

            // Sync changes made in Java back to XML document
	    binder.updateXML(item);
        }

        // Add $2 shipping per item under $25.
        for( Node node : xpath(doc, "//item[USPrice<25.00]") ) {
            // Unmarshal by declared type since element Item corresponds with a local element declaration in schema.
            // Partial Bind DOM Element"item" to instance of JAXB mapped binder.Items.Item class
            JAXBElement<Items.Item> itemE =
                    binder.unmarshal(node, Items.Item.class);
            binder.Items.Item item = itemE.getValue();

            // Modify in Java
            item.setUSPrice(item.getUSPrice().add(new BigDecimal("2.00")));

            // Sync changes made in Java back to XML document
            binder.updateXML(item);
        }
    }


    static Document loadDocument(File input) throws IOException, SAXException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            return dbf.newDocumentBuilder().parse(input);
        } catch (ParserConfigurationException e) {
            throw new Error(e); // impossible
        }
    }

    static public void saveDocument(Document document, File output) throws TransformerException, IOException {
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.transform(new DOMSource(document),new StreamResult(new FileOutputStream(output)));
    }


    static List<Node> xpath(Document document, String xpathExpression) {
        // create XPath
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();

        try {
            List<Node> result = new ArrayList<Node>();
            NodeList nl = (NodeList) xpath.evaluate(xpathExpression, document, XPathConstants.NODESET);
            for( int i=0; i<nl.getLength(); i++ )
                result.add(nl.item(i));
            return result;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }
}
