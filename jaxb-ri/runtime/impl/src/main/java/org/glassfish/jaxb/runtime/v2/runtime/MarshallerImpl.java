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

import org.glassfish.jaxb.core.marshaller.*;
import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import org.glassfish.jaxb.runtime.marshaller.NamespacePrefixMapper;
import org.glassfish.jaxb.runtime.marshaller.NioEscapeHandler;
import org.glassfish.jaxb.runtime.v2.runtime.output.*;
import org.glassfish.jaxb.runtime.v2.util.FatalAdapter;
import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.attachment.AttachmentMarshaller;
import jakarta.xml.bind.helpers.AbstractMarshallerImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link Marshaller} interface for the JAXB RI.
 *
 * <p>
 * Eventually all the {@link #marshal} methods call into
 * the {@link #write} method.
 *
 * @author Kohsuke Kawaguchi
 * @author Vivek Pandey
 */
public /*to make unit tests happy*/ final class MarshallerImpl extends AbstractMarshallerImpl implements ValidationEventHandler
{
    private static final Logger LOGGER = Logger.getLogger(MarshallerImpl.class.getName());

    /** Indentation string. Default is four whitespaces. */
    private String indent = "    ";

    /** Used to assign prefixes to namespace URIs. */
    private NamespacePrefixMapper prefixMapper = null;

    /** Object that handles character escaping. */
    private CharacterEscapeHandler escapeHandler = null;

    /** XML BLOB written after the XML declaration. */
    private String header=null;

    /** reference to the context that created this object */
    final JAXBContextImpl context;

    protected final XMLSerializer serializer;

    /**
     * Non-null if we do the marshal-time validation.
     */
    private Schema schema;

    /** Marshaller.Listener */
    private Listener externalListener = null;

    /** Configured for c14n? */
    private boolean c14nSupport;

    // while createing XmlOutput those values may be set.
    // if these are non-null they need to be cleaned up
    private Flushable toBeFlushed;
    private Closeable toBeClosed;

    /**
     * @param assoc
     *      non-null if the marshaller is working inside {@link BinderImpl}.
     */
    public MarshallerImpl( JAXBContextImpl c, AssociationMap assoc ) {
        context = c;
        serializer = new XMLSerializer(this);
        c14nSupport = context.c14nSupport;

        try {
            setEventHandler(this);
        } catch (JAXBException e) {
            throw new AssertionError(e);    // impossible
        }
    }

    public JAXBContextImpl getContext() {
        return context;
    }

    /**
     * Marshals to {@link OutputStream} with the given in-scope namespaces
     * taken into account.
     *
     * @since 2.1.5
     */
    public void marshal(Object obj, OutputStream out, NamespaceContext inscopeNamespace) throws JAXBException {
        write(obj, createWriter(out), new StAXPostInitAction(inscopeNamespace,serializer));
    }

    @Override
    public void marshal(Object obj, XMLStreamWriter writer) throws JAXBException {
        write(obj, XMLStreamWriterOutput.create(writer,context, escapeHandler), new StAXPostInitAction(writer,serializer));
    }

    @Override
    public void marshal(Object obj, XMLEventWriter writer) throws JAXBException {
        write(obj, new XMLEventWriterOutput(writer), new StAXPostInitAction(writer,serializer));
    }

    public void marshal(Object obj, XmlOutput output) throws JAXBException {
        write(obj, output, null );
    }

    /**
     * Creates {@link XmlOutput} from the given {@link Result} object.
     */
    XmlOutput createXmlOutput(Result result) throws JAXBException {
        if (result instanceof SAXResult)
            return new SAXOutput(((SAXResult) result).getHandler());

        if (result instanceof DOMResult) {
            final Node node = ((DOMResult) result).getNode();

            if (node == null) {
                Document doc = JAXBContextImpl.createDom(getContext().disableSecurityProcessing);
                ((DOMResult) result).setNode(doc);
                return new SAXOutput(new SAX2DOMEx(doc));
            } else {
                return new SAXOutput(new SAX2DOMEx(node));
            }
        }
        if (result instanceof StreamResult) {
            StreamResult sr = (StreamResult) result;

            if (sr.getWriter() != null)
                return createWriter(sr.getWriter());
            else if (sr.getOutputStream() != null)
                return createWriter(sr.getOutputStream());
            else if (sr.getSystemId() != null) {
                String fileURL = sr.getSystemId();

                try {
                    fileURL = new URI(fileURL).getPath();
                } catch (URISyntaxException use) {
                    // otherwise assume that it's a file name
                }

                try {
                    FileOutputStream fos = new FileOutputStream(fileURL);
                    assert toBeClosed==null;
                    toBeClosed = fos;
                    return createWriter(fos);
                } catch (IOException e) {
                    throw new MarshalException(e);
                }
            }
        }

        // unsupported parameter type
        throw new MarshalException(Messages.UNSUPPORTED_RESULT.format());
    }

    /**
     * Creates an appropriate post-init action object.
     */
    Runnable createPostInitAction(Result result) {
        if (result instanceof DOMResult) {
            Node node = ((DOMResult) result).getNode();
            return new DomPostInitAction(node,serializer);
        }
        return null;
    }

    @Override
    public void marshal(Object target,Result result) throws JAXBException {
        write(target, createXmlOutput(result), createPostInitAction(result));
    }


    /**
     * Used by {@link BridgeImpl} to write an arbitrary object as a fragment.
     */
    protected <T> void write(Name rootTagName, JaxBeanInfo<T> bi, T obj, XmlOutput out, Runnable postInitAction) throws JAXBException {
        try {
            try {
                prewrite(out, true, postInitAction);
                serializer.startElement(rootTagName,null);
                if(bi.jaxbType==Void.class || bi.jaxbType==void.class) {
                    // special case for void
                    serializer.endNamespaceDecls(null);
                    serializer.endAttributes();
                } else { // normal cases
                    if(obj==null)
                        serializer.writeXsiNilTrue();
                    else
                        serializer.childAsXsiType(obj,"root",bi, false);
                }
                serializer.endElement();
                postwrite();
            } catch( SAXException | XMLStreamException | IOException e ) {
                throw new MarshalException(e);
            } finally {
                serializer.close();
            }
        } finally {
            cleanUp();
        }
    }

    /**
     * All the marshal method invocation eventually comes down to this call.
     */
    private void write(Object obj, XmlOutput out, Runnable postInitAction) throws JAXBException {
        try {
            if( obj == null )
                throw new IllegalArgumentException(Messages.NOT_MARSHALLABLE.format());

            if( schema!=null ) {
                // send the output to the validator as well
                ValidatorHandler validator = schema.newValidatorHandler();
                validator.setErrorHandler(new FatalAdapter(serializer));
                // work around a bug in JAXP validator in Tiger
                XMLFilterImpl f = new XMLFilterImpl() {
                    @Override
                    public void startPrefixMapping(String prefix, String uri) throws SAXException {
                        super.startPrefixMapping(prefix.intern(), uri.intern());
                    }
                };
                f.setContentHandler(validator);
                out = new ForkXmlOutput(new SAXOutput(f) {
                    @Override
                    public void startDocument(XMLSerializer serializer, boolean fragment, int[] nsUriIndex2prefixIndex, NamespaceContextImpl nsContext) throws SAXException, IOException, XMLStreamException {
                        super.startDocument(serializer, false, nsUriIndex2prefixIndex, nsContext);
                    }
                    @Override
                    public void endDocument(boolean fragment) throws SAXException, IOException, XMLStreamException {
                        super.endDocument(false);
                    }
                }, out );
            }

            try {
                prewrite(out,isFragment(),postInitAction);
                serializer.childAsRoot(obj);
                postwrite();
            } catch( SAXException | XMLStreamException | IOException e ) {
                throw new MarshalException(e);
            } finally {
                serializer.close();
            }
        } finally {
            cleanUp();
        }
    }

    private void cleanUp() {
        if(toBeFlushed!=null)
            try {
                toBeFlushed.flush();
            } catch (IOException e) {
                // ignore
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        if(toBeClosed!=null)
            try {
                toBeClosed.close();
            } catch (IOException e) {
                // ignore
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        toBeFlushed = null;
        toBeClosed = null;
    }

    // common parts between two write methods.

    private void prewrite(XmlOutput out, boolean fragment, Runnable postInitAction) throws IOException, SAXException, XMLStreamException {
        serializer.startDocument(out,fragment,getSchemaLocation(),getNoNSSchemaLocation());
        if(postInitAction!=null)    postInitAction.run();
        if(prefixMapper!=null) {
            // be defensive as we work with the user's code
            String[] decls = prefixMapper.getContextualNamespaceDecls();
            if(decls!=null) { // defensive check
                for( int i=0; i<decls.length; i+=2 ) {
                    String prefix = decls[i];
                    String nsUri = decls[i+1];
                    if(nsUri!=null && prefix!=null) // defensive check
                        serializer.addInscopeBinding(nsUri,prefix);
                }
            }
        }
        serializer.setPrefixMapper(prefixMapper);
    }

    private void postwrite() throws IOException, SAXException, XMLStreamException {
        serializer.endDocument();
        serializer.reconcileID();   // extra check
    }


    /**
     * Returns escape handler provided with JAXB context parameters.
     *
     * @return escape handler
     */
    CharacterEscapeHandler getEscapeHandler() {
        return escapeHandler;
    }

    //
    //
    // create XMLWriter by specifing various type of output.
    //
    //

    protected CharacterEscapeHandler createEscapeHandler( String encoding ) {
        if( escapeHandler!=null )
            // user-specified one takes precedence.
            return escapeHandler;

        if( encoding.startsWith("UTF") )
            // no need for character reference. Use the handler
            // optimized for that pattern.
            return MinimumEscapeHandler.theInstance;

        // otherwise try to find one from the encoding
        try {
            // try new JDK1.4 NIO
            return new NioEscapeHandler( getJavaEncoding(encoding) );
        } catch( Throwable e ) {
            // if that fails, fall back to the dumb mode
            return DumbEscapeHandler.theInstance;
        }
    }

    public XmlOutput createWriter( Writer w, String encoding ) {
        // XMLWriter doesn't do buffering, so do it here if it looks like a good idea
        if(!(w instanceof BufferedWriter))
            w = new BufferedWriter(w);

        assert toBeFlushed==null;
        toBeFlushed = w;

        CharacterEscapeHandler ceh = createEscapeHandler(encoding);
        XMLWriter xw;

        if(isFormattedOutput()) {
            DataWriter d = new DataWriter(w,encoding,ceh);
            d.setIndentStep(indent);
            xw=d;
        } else
            xw = new XMLWriter(w,encoding,ceh);

        xw.setXmlDecl(!isFragment());
        xw.setHeader(header);
        return new SAXOutput(xw);   // TODO: don't we need a better writer?
    }

    public XmlOutput createWriter(Writer w) {
        return createWriter(w, getEncoding());
    }

    public XmlOutput createWriter( OutputStream os ) throws JAXBException {
        return createWriter(os, getEncoding());
    }

    public XmlOutput createWriter( OutputStream os, String encoding ) throws JAXBException {
        // UTF8XmlOutput does buffering on its own, and
        // otherwise createWriter(Writer) inserts a buffering,
        // so no point in doing a buffering here.

        if(encoding.equals("UTF-8")) {
            Encoded[] table = context.getUTF8NameTable();
            final UTF8XmlOutput out;
            CharacterEscapeHandler ceh = createEscapeHandler(encoding);
            if(isFormattedOutput())
                out = new IndentingUTF8XmlOutput(os, indent, table, ceh);
            else {
                if(c14nSupport)
                    out = new C14nXmlOutput(os, table, context.c14nSupport, ceh);
                else
                    out = new UTF8XmlOutput(os, table, ceh);
            }
            if(header!=null)
                out.setHeader(header);
            return out;
        }

        try {
            return createWriter(
                new OutputStreamWriter(os,getJavaEncoding(encoding)),
                encoding );
        } catch( UnsupportedEncodingException e ) {
            throw new MarshalException(
                Messages.UNSUPPORTED_ENCODING.format(encoding),
                e );
        }
    }


    @Override
    public Object getProperty(String name) throws PropertyException {
        if( INDENT_STRING.equals(name) )
            return indent;
        if( ENCODING_HANDLER.equals(name) || ENCODING_HANDLER2.equals(name) )
            return escapeHandler;
        if( PREFIX_MAPPER.equals(name) )
            return prefixMapper;
        if( XMLDECLARATION.equals(name) )
            return !isFragment();
        if( XML_HEADERS.equals(name) )
            return header;
        if( C14N.equals(name) )
            return c14nSupport;
        if ( OBJECT_IDENTITY_CYCLE_DETECTION.equals(name)) 
        	return serializer.getObjectIdentityCycleDetection();

        return super.getProperty(name);
    }

    @Override
    public void setProperty(String name, Object value) throws PropertyException {
        if( INDENT_STRING.equals(name) ) {
            checkString(name, value);
            indent = (String)value;
            return;
        }
        if( ENCODING_HANDLER.equals(name) || ENCODING_HANDLER2.equals(name)) {
            if(!(value instanceof CharacterEscapeHandler))
                throw new PropertyException(
                    Messages.MUST_BE_X.format(
                            name,
                            CharacterEscapeHandler.class.getName(),
                            value.getClass().getName() ) );
            escapeHandler = (CharacterEscapeHandler)value;
            return;
        }
        if( PREFIX_MAPPER.equals(name) ) {
            if(!(value instanceof NamespacePrefixMapper))
                throw new PropertyException(
                    Messages.MUST_BE_X.format(
                            name,
                            NamespacePrefixMapper.class.getName(),
                            value.getClass().getName() ) );
            prefixMapper = (NamespacePrefixMapper)value;
            return;
        }
        if( XMLDECLARATION.equals(name) ) {
            checkBoolean(name, value);
            // org.glassfish.jaxb.xmlDeclaration is an alias for JAXB_FRAGMENT
            // setting it to false is treated the same as setting fragment to true.
            super.setProperty(JAXB_FRAGMENT, !(Boolean)value);
            return;
        }
        if( XML_HEADERS.equals(name) ) {
            checkString(name, value);
            header = (String)value;
            return;
        }
        if( C14N.equals(name) ) {
            checkBoolean(name,value);
            c14nSupport = (Boolean)value;
            return;
        }
        if (OBJECT_IDENTITY_CYCLE_DETECTION.equals(name)) {
        	checkBoolean(name,value);
            serializer.setObjectIdentityCycleDetection((Boolean)value);
            return;
        }

        super.setProperty(name, value);
    }

    /*
     * assert that the given object is a Boolean
     */
    private void checkBoolean( String name, Object value ) throws PropertyException {
        if(!(value instanceof Boolean))
            throw new PropertyException(
                Messages.MUST_BE_X.format(
                        name,
                        Boolean.class.getName(),
                        value.getClass().getName() ) );
    }

    /*
     * assert that the given object is a String
     */
    private void checkString( String name, Object value ) throws PropertyException {
        if(!(value instanceof String))
            throw new PropertyException(
                Messages.MUST_BE_X.format(
                        name,
                        String.class.getName(),
                        value.getClass().getName() ) );
    }

    @Override
    public <A extends XmlAdapter<?, ?>> void setAdapter(Class<A> type, A adapter) {
        if(type==null)
            throw new IllegalArgumentException();
        serializer.putAdapter(type,adapter);
    }

    @Override
    public <A extends XmlAdapter<?, ?>> A getAdapter(Class<A> type) {
        if(type==null)
            throw new IllegalArgumentException();
        if(serializer.containsAdapter(type))
            // so as not to create a new instance when this method is called
            return serializer.getAdapter(type);
        else
            return null;
    }

    @Override
    public void setAttachmentMarshaller(AttachmentMarshaller am) {
        serializer.attachmentMarshaller = am;
    }

    @Override
    public AttachmentMarshaller getAttachmentMarshaller() {
        return serializer.attachmentMarshaller;
    }

    @Override
    public Schema getSchema() {
        return schema;
    }

    @Override
    public void setSchema(Schema s) {
        this.schema = s;
    }

    /**
     * Default error handling behavior fot {@link Marshaller}.
     */
    @Override
    public boolean handleEvent(ValidationEvent event) {
        // draconian by default
        return false;
    }

    @Override
    public Listener getListener() {
        return externalListener;
    }

    @Override
    public void setListener(Listener listener) {
        externalListener = listener;
    }

    // features supported
    protected static final String INDENT_STRING = "org.glassfish.jaxb.indentString";
    protected static final String PREFIX_MAPPER = "org.glassfish.jaxb.namespacePrefixMapper";
    protected static final String ENCODING_HANDLER = "org.glassfish.jaxb.characterEscapeHandler";
    protected static final String ENCODING_HANDLER2 = "org.glassfish.jaxb.marshaller.CharacterEscapeHandler";
    protected static final String XMLDECLARATION = "org.glassfish.jaxb.xmlDeclaration";
    protected static final String XML_HEADERS = "org.glassfish.jaxb.xmlHeaders";
    protected static final String C14N = JAXBRIContext.CANONICALIZATION_SUPPORT;
    protected static final String OBJECT_IDENTITY_CYCLE_DETECTION = "org.glassfish.jaxb.objectIdentitityCycleDetection";
}
