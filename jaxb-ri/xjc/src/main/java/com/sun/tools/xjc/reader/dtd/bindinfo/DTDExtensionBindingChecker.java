/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd.bindinfo;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.reader.AbstractExtensionBindingChecker;
import com.sun.tools.xjc.reader.Const;

import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;

/**
 * {@link XMLFilter} that checks the use of extension namespace URIs
 * (to see if they have corresponding plugins), and otherwise report an error.
 *
 * <p>
 * This code also masks the recognized extensions from the validator that
 * will be plugged as the next component to this.
 *
 * @author Kohsuke Kawaguchi
 */
final class DTDExtensionBindingChecker extends AbstractExtensionBindingChecker {
    public DTDExtensionBindingChecker(String schemaLanguage, Options options, ErrorHandler handler) {
        super(schemaLanguage, options, handler);
    }

    /**
     * Returns true if the elements with the given namespace URI
     * should be blocked by this filter.
     */
    private boolean needsToBePruned( String uri ) {
        if( uri.equals(schemaLanguage) )
            return false;
        if( uri.equals(Const.JAXB_NSURI) )
            return false;
        if( uri.equals(Const.XJC_EXTENSION_URI) )
            return false;
        // we don't want validator to see extensions that we understand ,
        // because they will complain.
        // OTOH, if  this is an extension that we didn't understand,
        // we want the validator to report an error
        return enabledExtensions.contains(uri);
    }



    public void startElement(String uri, String localName, String qName, Attributes atts)
        throws SAXException {

        if( !isCutting() ) {
            if(!uri.equals("")) {
                // "" is the standard namespace
                checkAndEnable(uri);

                verifyTagName(uri, localName, qName);

                if(needsToBePruned(uri))
                    startCutting();
            }
        }

        super.startElement(uri, localName, qName, atts);
    }
}
