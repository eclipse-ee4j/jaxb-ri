/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime;

import org.glassfish.jaxb.core.v2.ClassFactory;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallingContext;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;
import jakarta.xml.bind.ValidationEventLocator;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.helpers.ValidationEventImpl;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.HashMap;

/**
 * Object that coordinates the marshalling/unmarshalling.
 *
 * <p>
 * This class takes care of the logic that allows code to obtain
 * {@link UnmarshallingContext} and {@link XMLSerializer} instances
 * during the unmarshalling/marshalling.
 *
 * <p>
 * This is done by using a {@link ThreadLocal}. Therefore one unmarshalling/marshalling
 * episode has to be done from the beginning till end by the same thread.
 * (Note that the same {@link Coordinator} can be then used by a different thread
 * for an entirely different episode.)
 *
 * This class also maintains the user-configured instances of {@link XmlAdapter}s.
 *
 * <p>
 * This class implements {@link ErrorHandler} and propages erros to this object
 * as the {@link ValidationEventHandler}, which will be implemented in a derived class.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class Coordinator implements ErrorHandler, ValidationEventHandler {
    
    private final HashMap<Class<? extends XmlAdapter>,XmlAdapter> adapters =
            new HashMap<>();

    /**
     * Default constructor.
     */
    protected Coordinator() {};

    public final XmlAdapter putAdapter(Class<? extends XmlAdapter> c, XmlAdapter a) {
        if(a==null)
            return adapters.remove(c);
        else
            return adapters.put(c,a);
    }

    /**
     * Gets the instance of the adapter.
     *
     * @return
     *      always non-null.
     */
    public final <T extends XmlAdapter> T getAdapter(Class<T> key) {
        T v = key.cast(adapters.get(key));
        if(v==null) {
            v = ClassFactory.create(key);
            putAdapter(key,v);
        }
        return v;
    }

    public <T extends XmlAdapter> boolean containsAdapter(Class<T> type) {
        return adapters.containsKey(type);
    }

    // this much is necessary to avoid calling get and set twice when we push.
    private static final ThreadLocal<Coordinator> activeTable = new ThreadLocal<>();

    /**
     * The {@link Coordinator} in charge before this {@link Coordinator}.
     */
    private Coordinator old;

    /**
     * Called whenever an execution flow enters the realm of this .
     */
    protected final void pushCoordinator() {
        old = activeTable.get();
        activeTable.set(this);
    }

    /**
     * Called whenever an execution flow exits the realm of this .
     */
    protected final void popCoordinator() {
        if (old != null)
            activeTable.set(old);
        else
            activeTable.remove();
        old = null; // avoid memory leak
    }

    public static Coordinator _getInstance() {
        return activeTable.get();
    }

//
//
// ErrorHandler implementation
//
//
    /**
     * Gets the current location. Used for reporting the error source location.
     */
    protected abstract ValidationEventLocator getLocation();

    @Override
    public final void error(SAXParseException exception) throws SAXException {
        propagateEvent( ValidationEvent.ERROR, exception );
    }

    @Override
    public final void warning(SAXParseException exception) throws SAXException {
        propagateEvent( ValidationEvent.WARNING, exception );
    }

    @Override
    public final void fatalError(SAXParseException exception) throws SAXException {
        propagateEvent( ValidationEvent.FATAL_ERROR, exception );
    }

    private void propagateEvent( int severity, SAXParseException saxException )
        throws SAXException {

        ValidationEventImpl ve =
            new ValidationEventImpl( severity, saxException.getMessage(), getLocation() );

        Exception e = saxException.getException();
        if( e != null ) {
            ve.setLinkedException( e );
        } else {
            ve.setLinkedException( saxException );
        }

        // call the client's event handler.  If it returns false, then bail-out
        // and terminate the unmarshal operation.
        boolean result = handleEvent( ve );
        if( ! result ) {
            // bail-out of the parse with a SAX exception, but convert it into
            // an UnmarshalException back in in the AbstractUnmarshaller
            throw saxException;
        }
    }
}
