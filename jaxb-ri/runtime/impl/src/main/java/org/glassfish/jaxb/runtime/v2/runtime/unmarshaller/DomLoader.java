/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.unmarshaller;

import org.glassfish.jaxb.runtime.v2.runtime.JAXBContextImpl;
import jakarta.xml.bind.annotation.DomHandler;
import org.xml.sax.SAXException;

import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;

/**
 * Loads a DOM.
 *
 * @author Kohsuke Kawaguchi
 */
public class DomLoader<ResultT extends Result> extends Loader {

    private final DomHandler<?,ResultT> dom;

    /**
     * Used to capture the state.
     *
     * This instance is created for each unmarshalling episode.
     */
    private final class State {
        
        /** This handler will receive SAX events. */
        private TransformerHandler handler = null;

        /** {@link #handler} will produce this result. */
        private final ResultT result;

        // nest level of elements.
        int depth = 1;

        public State( UnmarshallingContext context ) throws SAXException {
            handler = JAXBContextImpl.createTransformerHandler(context.getJAXBContext().disableSecurityProcessing);
            result = dom.createUnmarshaller(context);

            handler.setResult(result);

            // emulate the start of documents
            try {
                handler.setDocumentLocator(context.getLocator());
                handler.startDocument();
                declarePrefixes( context, context.getAllDeclaredPrefixes() );
            } catch( SAXException e ) {
                context.handleError(e);
                throw e;
            }
        }

        public Object getElement() {
            return dom.getElement(result);
        }

        private void declarePrefixes( UnmarshallingContext context, String[] prefixes ) throws SAXException {
            for( int i=prefixes.length-1; i>=0; i-- ) {
                String nsUri = context.getNamespaceURI(prefixes[i]);
                if(nsUri==null)     throw new IllegalStateException("prefix '" +prefixes[i]+ "' isn't bound");
                handler.startPrefixMapping(prefixes[i],nsUri );
            }
        }

        private void undeclarePrefixes( String[] prefixes ) throws SAXException {
            for( int i=prefixes.length-1; i>=0; i-- )
                handler.endPrefixMapping( prefixes[i] );
        }
    }

    public DomLoader(DomHandler<?, ResultT> dom) {
        super(true);
        this.dom = dom;
    }

    @Override
    public void startElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
        UnmarshallingContext context = state.getContext();
        if (state.getTarget() == null)
            state.setTarget(new State(context));

        State s = (State) state.getTarget();
        try {
            s.declarePrefixes(context, context.getNewlyDeclaredPrefixes());
            s.handler.startElement(ea.uri, ea.local, ea.getQname(), ea.atts);
        } catch (SAXException e) {
            context.handleError(e);
            throw e;
        }
    }

    @Override
    public void childElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
        state.setLoader(this);
        State s = (State) state.getPrev().getTarget();
        s.depth++;
        state.setTarget(s);
    }

    @Override
    public void text(UnmarshallingContext.State state, CharSequence text) throws SAXException {
        if(text.length()==0)
            return;     // there's no point in creating an empty Text node in DOM. 
        try {
            State s = (State) state.getTarget();
            s.handler.characters(text.toString().toCharArray(),0,text.length());
        } catch( SAXException e ) {
            state.getContext().handleError(e);
            throw e;
        }
    }

    @Override
    public void leaveElement(UnmarshallingContext.State state, TagName ea) throws SAXException {
        State s = (State) state.getTarget();
        UnmarshallingContext context = state.getContext();

        try {
            s.handler.endElement(ea.uri, ea.local, ea.getQname());
            s.undeclarePrefixes(context.getNewlyDeclaredPrefixes());
        } catch( SAXException e ) {
            context.handleError(e);
            throw e;
        }

        if((--s.depth)==0) {
            // emulate the end of the document
            try {
                s.undeclarePrefixes(context.getAllDeclaredPrefixes());
                s.handler.endDocument();
            } catch( SAXException e ) {
                context.handleError(e);
                throw e;
            }

            // we are done
            state.setTarget(s.getElement());
        }
    }

}
