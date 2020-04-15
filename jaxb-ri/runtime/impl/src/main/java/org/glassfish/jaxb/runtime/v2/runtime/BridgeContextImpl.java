/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import org.glassfish.jaxb.runtime.api.BridgeContext;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallerImpl;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;

/**
 * {@link BridgeContext} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
public final class BridgeContextImpl extends BridgeContext {

    public final UnmarshallerImpl unmarshaller;
    public final MarshallerImpl marshaller;

    BridgeContextImpl(JAXBContextImpl context) {
        unmarshaller = context.createUnmarshaller();
        marshaller = context.createMarshaller();
    }

    public void setErrorHandler(ValidationEventHandler handler) {
        try {
            unmarshaller.setEventHandler(handler);
            marshaller.setEventHandler(handler);
        } catch (JAXBException e) {
            // impossible
            throw new Error(e);
        }
    }

    public void setAttachmentMarshaller(AttachmentMarshaller m) {
        marshaller.setAttachmentMarshaller(m);
    }

    public void setAttachmentUnmarshaller(AttachmentUnmarshaller u) {
        unmarshaller.setAttachmentUnmarshaller(u);
    }

    public AttachmentMarshaller getAttachmentMarshaller() {
        return marshaller.getAttachmentMarshaller();
    }

    public AttachmentUnmarshaller getAttachmentUnmarshaller() {
        return unmarshaller.getAttachmentUnmarshaller();
    }
}
