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

import com.sun.istack.NotNull;
import org.glassfish.jaxb.runtime.api.Bridge;
import org.glassfish.jaxb.runtime.api.TypeReference;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallerImpl;
import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link Bridge} decorator for {@link XmlAdapter}.
 *
 * @author Kohsuke Kawaguchi
 */
final class BridgeAdapter<OnWire,InMemory> extends InternalBridge<InMemory> {
    private final InternalBridge<OnWire> core;
    private final Class<? extends XmlAdapter<OnWire,InMemory>> adapter;

    public BridgeAdapter(InternalBridge<OnWire> core, Class<? extends XmlAdapter<OnWire,InMemory>> adapter) {
        super(core.getContext());
        this.core = core;
        this.adapter = adapter;
    }

    public void marshal(Marshaller m, InMemory inMemory, XMLStreamWriter output) throws JAXBException {
        core.marshal(m,adaptM(m,inMemory),output);
    }

    public void marshal(Marshaller m, InMemory inMemory, OutputStream output, NamespaceContext nsc) throws JAXBException {
        core.marshal(m,adaptM(m,inMemory),output,nsc);
    }

    public void marshal(Marshaller m, InMemory inMemory, Node output) throws JAXBException {
        core.marshal(m,adaptM(m,inMemory),output);
    }

    public void marshal(Marshaller context, InMemory inMemory, ContentHandler contentHandler) throws JAXBException {
        core.marshal(context,adaptM(context,inMemory),contentHandler);
    }

    public void marshal(Marshaller context, InMemory inMemory, Result result) throws JAXBException {
        core.marshal(context,adaptM(context,inMemory),result);
    }

    private OnWire adaptM(Marshaller m,InMemory v) throws JAXBException {
        XMLSerializer serializer = ((MarshallerImpl)m).serializer;
        serializer.pushCoordinator();
        try {
            return _adaptM(serializer, v);
        } finally {
            serializer.popCoordinator();
        }
    }

    private OnWire _adaptM(XMLSerializer serializer, InMemory v) throws MarshalException {
        XmlAdapter<OnWire,InMemory> a = serializer.getAdapter(adapter);
        try {
            return a.marshal(v);
        } catch (Exception e) {
            serializer.handleError(e,v,null);
            throw new MarshalException(e);
        }
    }


    public @NotNull InMemory unmarshal(Unmarshaller u, XMLStreamReader in) throws JAXBException {
        return adaptU(u, core.unmarshal(u,in));
    }

    public @NotNull InMemory unmarshal(Unmarshaller u, Source in) throws JAXBException {
        return adaptU(u, core.unmarshal(u,in));
    }

    public @NotNull InMemory unmarshal(Unmarshaller u, InputStream in) throws JAXBException {
        return adaptU(u, core.unmarshal(u,in));
    }

    public @NotNull InMemory unmarshal(Unmarshaller u, Node n) throws JAXBException {
        return adaptU(u, core.unmarshal(u,n));
    }

    public TypeReference getTypeReference() {
        return core.getTypeReference();
    }

    private @NotNull InMemory adaptU(Unmarshaller _u, OnWire v) throws JAXBException {
        UnmarshallerImpl u = (UnmarshallerImpl) _u;
        XmlAdapter<OnWire,InMemory> a = u.coordinator.getAdapter(adapter);
        u.coordinator.pushCoordinator();
        try {
            return a.unmarshal(v);
        } catch (Exception e) {
            throw new UnmarshalException(e);
        } finally {
            u.coordinator.popCoordinator();
        }
    }

    void marshal(InMemory o, XMLSerializer out) throws IOException, SAXException, XMLStreamException {
        try {
            core.marshal(_adaptM( XMLSerializer.getInstance(), o ), out );
        } catch (MarshalException e) {
            // recover from error by not marshalling this element.
        }
    }
}
