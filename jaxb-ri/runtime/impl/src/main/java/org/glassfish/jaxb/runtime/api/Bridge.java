/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.api;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Mini-marshaller/unmarshaller that is specialized for a particular
 * element name and a type.
 *
 * <p>
 * Instances of this class is stateless and multi-thread safe.
 * They are reentrant.
 *
 * <p>
 * All the marshal operation generates fragments.
 *
 * <p>
 * <b>Subject to change without notice</b>.
 *
 * @since JAXB 2.0 EA1
 * @author Kohsuke Kawaguchi
 */
public abstract class Bridge<T> {
    protected Bridge(JAXBContextImpl context) {
        this.context = context;
    }

    protected final JAXBContextImpl context;

    /**
     * Gets the {@link JAXBRIContext} to which this object belongs.
     *
     * @since 2.1
     */
    public @NotNull JAXBRIContext getContext() {
        return context;
    }

    /**
     *
     * @throws JAXBException
     *      if there was an error while marshalling.
     *
     * @since 2.0 EA1
     */
    public final void marshal(T object,XMLStreamWriter output) throws JAXBException {
        marshal(object,output,null);
    }
    public final void marshal(T object,XMLStreamWriter output, AttachmentMarshaller am) throws JAXBException {
        Marshaller m = context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        marshal(m,object,output);
        m.setAttachmentMarshaller(null);
        context.marshallerPool.recycle(m);
    }

    public abstract void marshal(@NotNull Marshaller m,T object,XMLStreamWriter output) throws JAXBException;


    /**
     * Marshals the specified type object with the implicit element name
     * associated with this instance of .
     *
     * @param nsContext
     *      if this marshalling is done to marshal a subelement, this {@link NamespaceContext}
     *      represents in-scope namespace bindings available for that element. Can be null,
     *      in which case JAXB assumes no in-scope namespaces.
     * @throws JAXBException
     *      if there was an error while marshalling.
     *
     * @since 2.0 EA1
     */
    public void marshal(T object,OutputStream output, NamespaceContext nsContext) throws JAXBException {
        marshal(object,output,nsContext,null);
    }
    /**
     * @since 2.0.2
     */
    public void marshal(T object,OutputStream output, NamespaceContext nsContext, AttachmentMarshaller am) throws JAXBException {
        Marshaller m = context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        marshal(m,object,output,nsContext);
        m.setAttachmentMarshaller(null);
        context.marshallerPool.recycle(m);
    }

    public abstract void marshal(@NotNull Marshaller m,T object,OutputStream output, NamespaceContext nsContext) throws JAXBException;


    public final void marshal(T object,Node output) throws JAXBException {
        Marshaller m = context.marshallerPool.take();
        marshal(m,object,output);
        context.marshallerPool.recycle(m);
    }

    public abstract void marshal(@NotNull Marshaller m,T object,Node output) throws JAXBException;


    /**
     * @since 2.0 EA4
     */
    public final void marshal(T object, ContentHandler contentHandler) throws JAXBException {
        marshal(object,contentHandler,null);
    }
    /**
     * @since 2.0.2
     */
    public final void marshal(T object, ContentHandler contentHandler, AttachmentMarshaller am) throws JAXBException {
        Marshaller m = context.marshallerPool.take();
        m.setAttachmentMarshaller(am);
        marshal(m,object,contentHandler);
        m.setAttachmentMarshaller(null);
        context.marshallerPool.recycle(m);
    }

    public abstract void marshal(@NotNull Marshaller m,T object, ContentHandler contentHandler) throws JAXBException;

    /**
     * @since 2.0 EA4
     */
    public final void marshal(T object, Result result) throws JAXBException {
        Marshaller m = context.marshallerPool.take();
        marshal(m,object,result);
        context.marshallerPool.recycle(m);
    }

    public abstract void marshal(@NotNull Marshaller m,T object, Result result) throws JAXBException;



    private T exit(T r, Unmarshaller u) {
        u.setAttachmentUnmarshaller(null);
        context.unmarshallerPool.recycle(u);
        return r;
    }

    /**
     * Unmarshals the specified type object.
     *
     * @param in
     *      the parser must be pointing at a start tag
     *      that encloses the XML type that this  is
     *      instanciated for.
     *
     * @return
     *      never null.
     *
     * @throws JAXBException
     *      if there was an error while unmarshalling.
     *
     * @since 2.0 EA1
     */
    public final @NotNull T unmarshal(@NotNull XMLStreamReader in) throws JAXBException {
        return unmarshal(in,null);
    }
    /**
     * @since 2.0.3
     */
    public final @NotNull T unmarshal(@NotNull XMLStreamReader in, @Nullable AttachmentUnmarshaller au) throws JAXBException {
        Unmarshaller u = context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return exit(unmarshal(u,in),u);
    }

    public abstract @NotNull T unmarshal(@NotNull Unmarshaller u, @NotNull XMLStreamReader in) throws JAXBException;

    /**
     * Unmarshals the specified type object.
     *
     * @param in
     *      the parser must be pointing at a start tag
     *      that encloses the XML type that this  is
     *      instanciated for.
     *
     * @return
     *      never null.
     *
     * @throws JAXBException
     *      if there was an error while unmarshalling.
     *
     * @since 2.0 EA1
     */
    public final @NotNull T unmarshal(@NotNull Source in) throws JAXBException {
        return unmarshal(in,null);
    }
    /**
     * @since 2.0.3
     */
    public final @NotNull T unmarshal(@NotNull Source in, @Nullable AttachmentUnmarshaller au) throws JAXBException {
        Unmarshaller u = context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return exit(unmarshal(u,in),u);
    }

    public abstract @NotNull T unmarshal(@NotNull Unmarshaller u, @NotNull Source in) throws JAXBException;

    /**
     * Unmarshals the specified type object.
     *
     * @param in
     *      the parser must be pointing at a start tag
     *      that encloses the XML type that this  is
     *      instanciated for.
     *
     * @return
     *      never null.
     *
     * @throws JAXBException
     *      if there was an error while unmarshalling.
     *
     * @since 2.0 EA1
     */
    public final @NotNull T unmarshal(@NotNull InputStream in) throws JAXBException {
        Unmarshaller u = context.unmarshallerPool.take();
        return exit(unmarshal(u,in),u);
    }

    public abstract @NotNull T unmarshal(@NotNull Unmarshaller u, @NotNull InputStream in) throws JAXBException;

    /**
     * Unmarshals the specified type object.
     *
     * @param n
     *      Node to be unmarshalled.
     *
     * @return
     *      never null.
     *
     * @throws JAXBException
     *      if there was an error while unmarshalling.
     *
     * @since 2.0 FCS
     */
    public final @NotNull T unmarshal(@NotNull Node n) throws JAXBException {
        return unmarshal(n,null);
    }
    /**
     * @since 2.0.3
     */
    public final @NotNull T unmarshal(@NotNull Node n, @Nullable AttachmentUnmarshaller au) throws JAXBException {
        Unmarshaller u = context.unmarshallerPool.take();
        u.setAttachmentUnmarshaller(au);
        return exit(unmarshal(u,n),u);
    }

    public abstract @NotNull T unmarshal(@NotNull Unmarshaller context, @NotNull Node n) throws JAXBException;

    /**
     * Gets the {@link TypeReference} from which this bridge was created.
     */
    public abstract TypeReference getTypeReference();
}
