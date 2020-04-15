/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.api;

import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import jakarta.xml.bind.attachment.AttachmentUnmarshaller;

/**
 * Holds thread specific state information for {@link Bridge}s,
 * to make {@link Bridge} thread-safe.
 *
 * <p>
 * This object cannot be used concurrently; two threads cannot
 * use the same object with {@link Bridge}s at the same time, nor
 * a thread can use a {@link BridgeContext} with one {@link Bridge} while
 * the same context is in use by another {@link Bridge}.
 *
 * <p>
 * {@link BridgeContext} is relatively a heavy-weight object, and
 * therefore it is expected to be cached by the JAX-RPC RI.
 *
 * <p>
 * <b>Subject to change without notice</b>.
 *
 * @author Kohsuke Kawaguchi
 * @since 2.0 EA1
 * @see Bridge
 * @deprecated
 *      The caller no longer needs to use this, as {@link Bridge} has
 *      methods that can work without {@link BridgeContext}.
 */
public abstract class BridgeContext {
    protected BridgeContext() {}
    
    /**
     * Registers the error handler that receives unmarshalling/marshalling errors.
     *
     * @param handler
     *      can be null, in which case all errors will be considered fatal.
     *
     * @since 2.0 EA1
     */
    public abstract void setErrorHandler(ValidationEventHandler handler);

    /**
     * Sets the {@link AttachmentMarshaller}.
     *
     * @since 2.0 EA1
     */
    public abstract void setAttachmentMarshaller(AttachmentMarshaller m);

    /**
     * Sets the {@link AttachmentUnmarshaller}.
     *
     * @since 2.0 EA1
     */
    public abstract void setAttachmentUnmarshaller(AttachmentUnmarshaller m);

    /**
     * Gets the last {@link AttachmentMarshaller} set through
     * {@link AttachmentMarshaller}.
     *
     * @since 2.0 EA2
     */
    public abstract AttachmentMarshaller getAttachmentMarshaller();

    /**
     * Gets the last {@link AttachmentUnmarshaller} set through
     * {@link AttachmentUnmarshaller}.
     *
     * @since 2.0 EA2
     */
    public abstract AttachmentUnmarshaller getAttachmentUnmarshaller();
}
