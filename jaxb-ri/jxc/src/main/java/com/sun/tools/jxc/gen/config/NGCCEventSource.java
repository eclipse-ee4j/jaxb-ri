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
public interface NGCCEventSource {
    /**
     * Replaces an old handler with a new handler, and returns
     * ID of the EventReceiver thread.
     * @param _old old handler
     * @param _new new handler
     * @return
     *      Thread ID of the receiver that can handle this event,
     *      or -1 if none.
     */
    int replace( NGCCEventReceiver _old, NGCCEventReceiver _new );
    
    /** Sends an enter element event to the specified EventReceiver thread.
     * @param receiverThreadId Thread ID of the receiver that can handle this event
     * @param uri element uri
     * @param local element local name
     * @param qname element qname
     * @param atts element attributes
     * @throws org.xml.sax.SAXException for errors
     */
    void sendEnterElement( int receiverThreadId, String uri, String local, String qname, Attributes atts ) throws SAXException;

    void sendLeaveElement( int receiverThreadId, String uri, String local, String qname ) throws SAXException;
    void sendEnterAttribute( int receiverThreadId, String uri, String local, String qname ) throws SAXException;
    void sendLeaveAttribute( int receiverThreadId, String uri, String local, String qname ) throws SAXException;
    void sendText( int receiverThreadId, String value ) throws SAXException;
}
