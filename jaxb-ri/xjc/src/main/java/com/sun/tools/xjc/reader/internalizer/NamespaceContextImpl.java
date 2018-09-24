/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sun.tools.xjc.reader.internalizer;

import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Implements {@link NamespaceContext} by looking at the in-scope
 * namespace binding of a DOM element.
 *
 * @author Kohsuke Kawaguchi
 */
final class NamespaceContextImpl implements NamespaceContext {
    private final Element e;

    public NamespaceContextImpl(Element e) {
        this.e = e;
    }

    public String getNamespaceURI(String prefix) {
        Node parent = e;
        String namespace = null;
        final String prefixColon = prefix + ':';

        if (prefix.equals("xml")) {
            namespace = XMLConstants.XML_NS_URI;
        } else {
            int type;

            while ((null != parent) && (null == namespace)
                    && (((type = parent.getNodeType()) == Node.ELEMENT_NODE)
                    || (type == Node.ENTITY_REFERENCE_NODE))) {
                if (type == Node.ELEMENT_NODE) {
                    if (parent.getNodeName().startsWith(prefixColon))
                        return parent.getNamespaceURI();
                    NamedNodeMap nnm = parent.getAttributes();

                    for (int i = 0; i < nnm.getLength(); i++) {
                        Node attr = nnm.item(i);
                        String aname = attr.getNodeName();
                        boolean isPrefix = aname.startsWith("xmlns:");

                        if (isPrefix || aname.equals("xmlns")) {
                            int index = aname.indexOf(':');
                            String p = isPrefix ? aname.substring(index + 1) : "";

                            if (p.equals(prefix)) {
                                namespace = attr.getNodeValue();

                                break;
                            }
                        }
                    }
                }

                parent = parent.getParentNode();
            }
        }

        if(prefix.equals(""))
            return "";  // default namespace
        return namespace;
    }

    public String getPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    public Iterator getPrefixes(String namespaceURI) {
        throw new UnsupportedOperationException();
    }
}
