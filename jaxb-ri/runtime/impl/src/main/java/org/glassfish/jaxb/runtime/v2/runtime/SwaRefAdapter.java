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


import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallingContext;
import jakarta.activation.DataHandler;
import jakarta.xml.bind.annotation.XmlAttachmentRef;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;


/**
 * {@link XmlAdapter} that binds the value as a SOAP attachment.
 *
 * <p>
 * On the user classes the SwA handling is done by using the {@link XmlAttachmentRef}
 * annotation, but internally we treat it as a {@link XmlJavaTypeAdapter} with this
 * adapter class. This is true with both XJC and the runtime.
 *
 * <p>
 * the model builder code and the code generator does the conversion and
 * shield the rest of the RI from this mess.
 * Also see @see <a href="http://webservices.xml.com/pub/a/ws/2003/09/16/wsbp.html?page=2">http://webservices.xml.com/pub/a/ws/2003/09/16/wsbp.html?page=2</a>.
 *
 * @author Kohsuke Kawaguchi
 */
public final class SwaRefAdapter extends XmlAdapter<String,DataHandler> {

    public SwaRefAdapter() {
    }

    public DataHandler unmarshal(String cid) {
        AttachmentUnmarshaller au = UnmarshallingContext.getInstance().parent.getAttachmentUnmarshaller();
        // TODO: error check
        return au.getAttachmentAsDataHandler(cid);
    }

    public String marshal(DataHandler data) {
        if(data==null)      return null;
        AttachmentMarshaller am = XMLSerializer.getInstance().attachmentMarshaller;
        // TODO: error check
        return am.addSwaRefAttachment(data);
    }
}
