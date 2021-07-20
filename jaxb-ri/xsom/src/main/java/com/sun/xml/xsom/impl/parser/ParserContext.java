/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl.parser;

import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.impl.ElementDecl;
import com.sun.xml.xsom.impl.SchemaImpl;
import com.sun.xml.xsom.impl.SchemaSetImpl;
import com.sun.xml.xsom.parser.AnnotationParserFactory;
import com.sun.xml.xsom.parser.XMLParser;
import com.sun.xml.xsom.parser.XSOMParser;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * Provides context information to be used by {@link NGCCRuntimeEx}s.
 * 
 * <p>
 * This class does the actual processing for {@link XSOMParser},
 * but to hide the details from the public API, this class in
 * a different package.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ParserContext {

    /** SchemaSet to which a newly parsed schema is put in. */
    public final SchemaSetImpl schemaSet = new SchemaSetImpl();

    private final XSOMParser owner;

    final XMLParser parser;


    private final Vector<Patch> patchers = new Vector<Patch>();
    private final Vector<Patch> errorCheckers = new Vector<Patch>();

    /**
     * Documents that are parsed already. Used to avoid cyclic inclusion/double
     * inclusion of schemas. Set of {@link SchemaDocumentImpl}s.
     *
     * The actual data structure is map from {@link SchemaDocumentImpl} to itself,
     * so that we can access the {@link SchemaDocumentImpl} itself.
     */
    public final Map<SchemaDocumentImpl, SchemaDocumentImpl> parsedDocuments = new HashMap<SchemaDocumentImpl, SchemaDocumentImpl>();


    public ParserContext( XSOMParser owner, XMLParser parser ) {
        this.owner = owner;
        this.parser = parser;

        try (InputStream is = ParserContext.class.getResourceAsStream("datatypes.xsd")) {
            InputSource source = new InputSource(is);
            source.setSystemId("datatypes.xsd");
            parse(source);

            SchemaImpl xs = (SchemaImpl)
                    schemaSet.getSchema("http://www.w3.org/2001/XMLSchema");
            xs.addSimpleType(schemaSet.anySimpleType,true);
            xs.addComplexType(schemaSet.anyType,true);
        } catch( SAXException | IOException e ) {
            // this must be a bug of XSOM
            throw new InternalError(e.getMessage());
        }
    }

    public EntityResolver getEntityResolver() {
        return owner.getEntityResolver();
    }

    public AnnotationParserFactory getAnnotationParserFactory() {
        return owner.getAnnotationParserFactory();
    }

    /**
     * Parses a new XML Schema document.
     */
    public void parse( InputSource source ) throws SAXException {
        newNGCCRuntime().parseEntity(source,false,null,null);
    }


    public XSSchemaSet getResult() throws SAXException {
        // run all the patchers
        for (Patch patcher : patchers)
            patcher.run();
        patchers.clear();

        // build the element substitutability map
        Iterator itr = schemaSet.iterateElementDecls();
        while(itr.hasNext())
            ((ElementDecl)itr.next()).updateSubstitutabilityMap();

        // run all the error checkers
        for (Patch patcher : errorCheckers)
            patcher.run();
        errorCheckers.clear();


        if(hadError)    return null;
        else            return schemaSet;
    }

    public NGCCRuntimeEx newNGCCRuntime() {
        return new NGCCRuntimeEx(this);
    }



    /** Once an error is detected, this flag is set to true. */
    private boolean hadError = false;

    /** Turns on the error flag. */
    void setErrorFlag() { hadError=true; }

    /**
     * PatchManager implementation, which is accessible only from
     * NGCCRuntimEx.
     */
    final PatcherManager patcherManager = new PatcherManager() {
        public void addPatcher( Patch patch ) {
            patchers.add(patch);
        }
        public void addErrorChecker( Patch patch ) {
            errorCheckers.add(patch);
        }
        public void reportError( String msg, Locator src ) throws SAXException {
            // set a flag to true to avoid returning a corrupted object.
            setErrorFlag();

            SAXParseException e = new SAXParseException(msg,src);
            if(errorHandler==null)
                throw e;
            else
                errorHandler.error(e);
        }
    };

    /**
     * ErrorHandler proxy to turn on the hadError flag when an error
     * is found.
     */
    final ErrorHandler errorHandler = new ErrorHandler() {
        private ErrorHandler getErrorHandler() {
            if( owner.getErrorHandler()==null )
                return noopHandler;
            else
                return owner.getErrorHandler();
        }

        public void warning(SAXParseException e) throws SAXException {
            getErrorHandler().warning(e);
        }

        public void error(SAXParseException e) throws SAXException {
            setErrorFlag();
            getErrorHandler().error(e);
        }

        public void fatalError(SAXParseException e) throws SAXException {
            setErrorFlag();
            getErrorHandler().fatalError(e);
        }
    };

    /**
     * {@link ErrorHandler} that does nothing.
     */
    final ErrorHandler noopHandler = new ErrorHandler() {
        public void warning(SAXParseException e) {
        }
        public void error(SAXParseException e) {
        }
        public void fatalError(SAXParseException e) {
            setErrorFlag();
        }
    };
}
