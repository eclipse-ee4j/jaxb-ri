/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.IOException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Kohsuke Kawaguchi
 */
public class XMLResolverAdapter implements XMLResolver {
    private final EntityResolver entityResolver;
    private final XMLInputFactory inputFactory;

    public XMLResolverAdapter(XMLInputFactory inputFactory, EntityResolver entityResolver) {
        this.inputFactory = inputFactory;
        this.entityResolver = entityResolver;
    }

    public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) throws XMLStreamException {
        try {
            InputSource is = entityResolver.resolveEntity(publicID,systemID);
            if(is==null)        return null;

            StreamSource ss = new StreamSource();
            ss.setInputStream(is.getByteStream());
            ss.setPublicId(is.getPublicId());
            ss.setSystemId(is.getSystemId());
            ss.setReader(is.getCharacterStream());
            // this ignores the is.getEncoding(), but that's JAXP's fault.
            return inputFactory.createXMLStreamReader(ss);
        } catch (SAXException e) {
            throw new XMLStreamException(e);
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}
