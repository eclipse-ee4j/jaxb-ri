/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.gen.config;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * <p><b>
 *     Auto-generated, do not edit.
 * </b></p>
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public interface NGCCEventReceiver {
    void enterElement(String uri, String localName, String qname,Attributes atts) throws SAXException;
    void leaveElement(String uri, String localName, String qname) throws SAXException;
    void text(String value) throws SAXException;
    void enterAttribute(String uri, String localName, String qname) throws SAXException;
    void leaveAttribute(String uri, String localName, String qname) throws SAXException;
}
