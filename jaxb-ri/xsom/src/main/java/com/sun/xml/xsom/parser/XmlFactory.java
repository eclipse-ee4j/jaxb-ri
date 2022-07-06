/*
 * Copyright (c) 2013, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.parser;

import com.sun.xml.xsom.impl.parser.Messages;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides helper methods for creating properly configured XML parser
 * factory instances with namespace support turned on and configured for
 * security.
 *
 * @author snajper
 */
public final class XmlFactory {

    private static final Logger LOGGER = Logger.getLogger(XmlFactory.class.getName());

    /**
     * If true XML security features when parsing XML documents will be disabled.
     * The default value is false.
     * <p>
     * Boolean
     */
    private static final String DISABLE_XML_SECURITY = "org.glassfish.jaxb.disableXmlSecurity";

    private static final boolean XML_SECURITY_DISABLED = AccessController.doPrivileged(
            new PrivilegedAction<>() {
                @Override
                public Boolean run() {
                    return Boolean.getBoolean(DISABLE_XML_SECURITY);
                }
            }
    );

    private XmlFactory() {
    }

    private static boolean isXMLSecurityDisabled(boolean runtimeSetting) {
        return XML_SECURITY_DISABLED || runtimeSetting;
    }

    /**
     * Returns properly configured (e.g. security features) parser factory
     * - namespaceAware == true
     * - securityProcessing == is set based on security processing property, default is true
     */
    public static SAXParserFactory createParserFactory(boolean disableSecureProcessing) throws IllegalStateException {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //https://rules.sonarsource.com/java/RSPEC-2755
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "SAXParserFactory instance: {0}", factory);
            }
            factory.setNamespaceAware(true);
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, !isXMLSecurityDisabled(disableSecureProcessing));
            return factory;
        } catch (ParserConfigurationException | SAXNotRecognizedException | SAXNotSupportedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new IllegalStateException(ex);
        } catch (AbstractMethodError er) {
            LOGGER.log(Level.SEVERE, null, er);
            throw new IllegalStateException(Messages.format(Messages.INVALID_JAXP_IMPLEMENTATION), er);
        }
    }
}
