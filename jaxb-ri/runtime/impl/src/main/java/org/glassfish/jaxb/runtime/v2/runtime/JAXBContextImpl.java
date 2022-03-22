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

import com.sun.istack.NotNull;
import com.sun.istack.Pool;
import org.glassfish.jaxb.core.api.ErrorListener;
import org.glassfish.jaxb.runtime.api.*;
import org.glassfish.jaxb.core.unmarshaller.DOMScanner;
import org.glassfish.jaxb.core.util.Which;
import org.glassfish.jaxb.core.v2.WellKnownNamespace;
import org.glassfish.jaxb.runtime.v2.model.annotation.RuntimeAnnotationReader;
import org.glassfish.jaxb.runtime.v2.model.annotation.RuntimeInlineAnnotationReader;
import org.glassfish.jaxb.core.v2.model.core.Adapter;
import org.glassfish.jaxb.core.v2.model.core.NonElement;
import org.glassfish.jaxb.core.v2.model.core.Ref;
import org.glassfish.jaxb.runtime.v2.model.impl.RuntimeBuiltinLeafInfoImpl;
import org.glassfish.jaxb.runtime.v2.model.impl.RuntimeModelBuilder;
import org.glassfish.jaxb.core.v2.model.nav.Navigator;
import org.glassfish.jaxb.core.v2.runtime.RuntimeUtil;
import org.glassfish.jaxb.runtime.v2.runtime.output.Encoded;
import org.glassfish.jaxb.runtime.v2.runtime.property.AttributeProperty;
import org.glassfish.jaxb.runtime.v2.runtime.property.Property;
import org.glassfish.jaxb.runtime.v2.runtime.reflect.Accessor;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.Loader;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.TagName;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallerImpl;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.UnmarshallingContext;
import org.glassfish.jaxb.runtime.v2.schemagen.XmlSchemaGenerator;
import org.glassfish.jaxb.core.v2.util.EditDistance;
import org.glassfish.jaxb.runtime.v2.util.QNameMap;
import org.glassfish.jaxb.core.v2.util.XmlFactory;
import com.sun.xml.txw2.output.ResultFactory;
import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.XmlAttachmentRef;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.glassfish.jaxb.runtime.v2.model.runtime.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class provides the implementation of JAXBContext.
 *
 */
public final class JAXBContextImpl extends JAXBRIContext {

    /**
     * All the bridge classes.
     */
    private final Map<TypeReference, Bridge> bridges = new LinkedHashMap<>();

    /**
     * Shared instance of {@link DocumentBuilder}.
     * Lock before use. Lazily created.
     */
    private static DocumentBuilder db;

    private final QNameMap<JaxBeanInfo> rootMap = new QNameMap<>();
    private final HashMap<QName,JaxBeanInfo> typeMap = new HashMap<>();

    /**
     * Map from JAXB-bound {@link Class} to its {@link JaxBeanInfo}.
     */
    private final Map<Class,JaxBeanInfo> beanInfoMap = new LinkedHashMap<>();

    /**
     * All created {@link JaxBeanInfo}s.
     * Updated from each {@link JaxBeanInfo}s constructors to avoid infinite recursion
     * for a cyclic reference.
     *
     * <p>
     * This map is only used while the {@link JAXBContextImpl} is built and set to null
     * to avoid keeping references too long.
     */
    protected Map<RuntimeTypeInfo,JaxBeanInfo> beanInfos = new LinkedHashMap<>();

    private final Map<Class/*scope*/,Map<QName,ElementBeanInfoImpl>> elements = new LinkedHashMap<>();

    /**
     * Pool of {@link Marshaller}s.
     */
    public final Pool<Marshaller> marshallerPool = new Pool.Impl<>() {
        protected @NotNull
        @Override
        Marshaller create() {
            return createMarshaller();
        }
    };

    public final Pool<Unmarshaller> unmarshallerPool = new Pool.Impl<>() {
        protected @NotNull
        @Override
        Unmarshaller create() {
            return createUnmarshaller();
        }
    };

    /**
     * Used to assign indices to known names in this grammar.
     * Reset to null once the build phase is completed.
     */
    public NameBuilder nameBuilder = new NameBuilder();

    /**
     * Keeps the list of known names.
     * This field is set once the build pahse is completed.
     */
    public final NameList nameList;

    /**
     * Input to the JAXBContext.newInstance, so that we can recreate
     * {@link RuntimeTypeInfoSet} whenever we need.
     */
    private final String defaultNsUri;
    private final Class[] classes;

    /**
     * true to reorder attributes lexicographically in preparation of the c14n support.
     */
    protected final boolean c14nSupport;

    /**
     * Flag that user has provided a custom AccessorFactory for JAXB to use
     */
    public final boolean xmlAccessorFactorySupport;

    /**
     * @see JAXBRIContext#TREAT_EVERYTHING_NILLABLE
     */
    public final boolean allNillable;

    /**
     * Store properties, so that they can be recovered in the run (is here because of JSON encoding of Jersey).
     */
    public final boolean retainPropertyInfo;

    /**
     * Suppress reflection accessor warnings.
     */
    public final boolean supressAccessorWarnings;

    /**
     * Improved xsi type handling.
     */
    public final boolean improvedXsiTypeHandling;

    /**
     * Disable security processing.
     */
    public final boolean disableSecurityProcessing;

    private WeakReference<RuntimeTypeInfoSet> typeInfoSetCache;

    private @NotNull
    RuntimeAnnotationReader annotationReader;

    private /*almost final*/ boolean hasSwaRef;
    private final @NotNull Map<Class,Class> subclassReplacements;

    /**
     * If true, we aim for faster {@link JAXBContext} instantiation performance,
     * instead of going after efficient sustained unmarshalling/marshalling performance.
     *
     * @since 2.0.4
     */
    public final boolean fastBoot;

    private Set<XmlNs> xmlNsSet = null;

    /**
     * If true, despite the specification, unmarshall child element with parent namespace, if child namespace is not specified.
     * The default value is null for System {@code org.glassfish.jaxb.backupWithParentNamespace} property to be used,
     * and false is assumed if it's not set either.
     *
     * Boolean
     * @since 2.3.0
     */
    public Boolean backupWithParentNamespace = null;

    /**
     * The maximum number of errors unmarshall operation reports.  Use negative value to report all errors.
     * The default value is 10.
     *
     * @since 2.3.3
     */
    public final int maxErrorsCount;

    /**
     * Returns declared XmlNs annotations (from package-level annotation XmlSchema
     *
     * @return set of all present XmlNs annotations
     */
    public Set<XmlNs> getXmlNsSet() {
        return xmlNsSet;
    }

    private JAXBContextImpl(JAXBContextBuilder builder) throws JAXBException {

        this.defaultNsUri = builder.defaultNsUri;
        this.retainPropertyInfo = builder.retainPropertyInfo;
        this.annotationReader = builder.annotationReader;
        this.subclassReplacements = builder.subclassReplacements;
        this.c14nSupport = builder.c14nSupport;
        this.classes = builder.classes;
        this.xmlAccessorFactorySupport = builder.xmlAccessorFactorySupport;
        this.allNillable = builder.allNillable;
        this.supressAccessorWarnings = builder.supressAccessorWarnings;
        this.improvedXsiTypeHandling = builder.improvedXsiTypeHandling;
        this.disableSecurityProcessing = builder.disableSecurityProcessing;
        this.backupWithParentNamespace = builder.backupWithParentNamespace;
        this.maxErrorsCount = builder.maxErrorsCount;

        Collection<TypeReference> typeRefs = builder.typeRefs;

        boolean fastB;
        try {
            fastB = Boolean.getBoolean(JAXBContextImpl.class.getName()+".fastBoot");
        } catch (SecurityException e) {
            fastB = false;
        }
        this.fastBoot = fastB;

        RuntimeTypeInfoSet typeSet = getTypeInfoSet();

        // at least prepare the empty table so that we don't have to check for null later
        elements.put(null,new LinkedHashMap<>());

        // recognize leaf bean infos
        for( RuntimeBuiltinLeafInfo leaf : RuntimeBuiltinLeafInfoImpl.builtinBeanInfos ) {
            LeafBeanInfoImpl<?> bi = new LeafBeanInfoImpl(this,leaf);
            beanInfoMap.put(leaf.getClazz(),bi);
            for( QName t : bi.getTypeNames() )
                typeMap.put(t,bi);
        }

        for (RuntimeEnumLeafInfo e : typeSet.enums().values()) {
            JaxBeanInfo<?> bi = getOrCreate(e);
            for (QName qn : bi.getTypeNames())
                typeMap.put( qn, bi );
            if(e.isElement())
                rootMap.put( e.getElementName(), bi );
        }

        for (RuntimeArrayInfo a : typeSet.arrays().values()) {
            JaxBeanInfo<?> ai = getOrCreate(a);
            for (QName qn : ai.getTypeNames())
                typeMap.put( qn, ai );
        }

        for( Entry<Class, ? extends RuntimeClassInfo> e : typeSet.beans().entrySet() ) {
            ClassBeanInfoImpl<?> bi = getOrCreate(e.getValue());

            XmlSchema xs = this.annotationReader.getPackageAnnotation(XmlSchema.class, e.getKey(), null);
            if(xs != null) {
                if(xs.xmlns() != null && xs.xmlns().length > 0) {
                    if(xmlNsSet == null)
                        xmlNsSet = new HashSet<>();
                    xmlNsSet.addAll(Arrays.asList(xs.xmlns()));
                }
            }

            if(bi.isElement())
                rootMap.put( e.getValue().getElementName(), bi );

            for (QName qn : bi.getTypeNames())
                typeMap.put( qn, bi );
        }

        // fill in element mappings
        for( RuntimeElementInfo n : typeSet.getAllElements() ) {
            ElementBeanInfoImpl bi = getOrCreate(n);
            if(n.getScope()==null)
                rootMap.put(n.getElementName(),bi);

            RuntimeClassInfo scope = n.getScope();
            Class scopeClazz = scope==null?null:scope.getClazz();
            Map<QName, ElementBeanInfoImpl> m = elements.computeIfAbsent(scopeClazz, k -> new LinkedHashMap<>());
            m.put(n.getElementName(),bi);
        }

        // this one is so that we can handle plain JAXBElements.
        beanInfoMap.put(JAXBElement.class,new ElementBeanInfoImpl(this));
        // another special BeanInfoImpl just for marshalling
        beanInfoMap.put(CompositeStructure.class,new CompositeStructureBeanInfo(this));

        getOrCreate(typeSet.getAnyTypeInfo());

        // then link them all!
        for (JaxBeanInfo bi : beanInfos.values())
            bi.link(this);

        // register primitives for boxed types just to make GrammarInfo fool-proof
        for( Map.Entry<Class<?>,Class<?>> e : RuntimeUtil.primitiveToBox.entrySet() )
            beanInfoMap.put( e.getKey(), beanInfoMap.get(e.getValue()) );

        // build bridges
        Navigator<Type, Class, Field, Method> nav = typeSet.getNavigator();

        for (TypeReference tr : typeRefs) {
            XmlJavaTypeAdapter xjta = tr.get(XmlJavaTypeAdapter.class);
            Adapter<Type,Class> a=null;
            XmlList xl = tr.get(XmlList.class);

            // eventually compute the in-memory type
            Class erasedType = (Class) nav.erasure(tr.type);

            if(xjta!=null) {
                a = new Adapter<>(xjta.value(),nav);
            }
            if(tr.get(XmlAttachmentRef.class)!=null) {
                a = new Adapter<>(SwaRefAdapter.class,nav);
                hasSwaRef = true;
            }

            if(a!=null) {
                erasedType = (Class) nav.erasure(a.defaultType);
            }

            Name name = nameBuilder.createElementName(tr.tagName);

            InternalBridge bridge;
            if(xl==null)
                bridge = new BridgeImpl(this, name,getBeanInfo(erasedType,true),tr);
            else
                bridge = new BridgeImpl(this, name,new ValueListBeanInfoImpl(this,erasedType),tr);

            if(a!=null)
                bridge = new BridgeAdapter(bridge,a.adapterType);

            bridges.put(tr,bridge);
        }

        this.nameList = nameBuilder.conclude();

        for (JaxBeanInfo bi : beanInfos.values())
            bi.wrapUp();

        // no use for them now
        nameBuilder = null;
        beanInfos = null;
    }

    /**
     * True if this JAXBContext has {@link XmlAttachmentRef}.
     */
    @Override
    public boolean hasSwaRef() {
        return hasSwaRef;
    }

    @Override
    public RuntimeTypeInfoSet getRuntimeTypeInfoSet() {
        try {
            return getTypeInfoSet();
        } catch (IllegalAnnotationsException e) {
            // impossible, once the model is constructred
            throw new AssertionError(e);
        }
    }

    /**
     * Creates a {@link RuntimeTypeInfoSet}.
     */
    public RuntimeTypeInfoSet getTypeInfoSet() throws IllegalAnnotationsException {

        // check cache
        if(typeInfoSetCache!=null) {
            RuntimeTypeInfoSet r = typeInfoSetCache.get();
            if(r!=null)
                return r;
        }

        final RuntimeModelBuilder builder = new RuntimeModelBuilder(this,annotationReader,subclassReplacements,defaultNsUri);

        IllegalAnnotationsException.Builder errorHandler = new IllegalAnnotationsException.Builder();
        builder.setErrorHandler(errorHandler);

        for( Class c : classes ) {
            if(c==CompositeStructure.class)
                // CompositeStructure doesn't have TypeInfo, so skip it.
                // We'll add JaxBeanInfo for this later automatically
                continue;
            builder.getTypeInfo(new Ref<>(c));
        }

        this.hasSwaRef |= builder.hasSwaRef;
        RuntimeTypeInfoSet r = builder.link();

        errorHandler.check();
        assert r!=null : "if no error was reported, the link must be a success";

        typeInfoSetCache = new WeakReference<>(r);

        return r;
    }


    public ElementBeanInfoImpl getElement(Class scope, QName name) {
        Map<QName,ElementBeanInfoImpl> m = elements.get(scope);
        if(m!=null) {
            ElementBeanInfoImpl bi = m.get(name);
            if(bi!=null)
                return bi;
        }
        m = elements.get(null);
        return m.get(name);
    }





    private ElementBeanInfoImpl getOrCreate( RuntimeElementInfo rei ) {
        JaxBeanInfo bi = beanInfos.get(rei);
        if(bi!=null)    return (ElementBeanInfoImpl)bi;

        // all elements share the same type, so we can't register them to beanInfoMap
        return new ElementBeanInfoImpl(this, rei);
    }

    protected JaxBeanInfo getOrCreate( RuntimeEnumLeafInfo eli ) {
        JaxBeanInfo bi = beanInfos.get(eli);
        if(bi!=null)    return bi;
        bi = new LeafBeanInfoImpl(this,eli);
        beanInfoMap.put(bi.jaxbType,bi);
        return bi;
    }

    protected ClassBeanInfoImpl getOrCreate( RuntimeClassInfo ci ) {
        ClassBeanInfoImpl bi = (ClassBeanInfoImpl)beanInfos.get(ci);
        if(bi!=null)    return bi;
        bi = new ClassBeanInfoImpl(this,ci);
        beanInfoMap.put(bi.jaxbType,bi);
        return bi;
    }

    protected JaxBeanInfo getOrCreate( RuntimeArrayInfo ai ) {
        JaxBeanInfo abi = beanInfos.get(ai);
        if(abi!=null)   return abi;

        abi = new ArrayBeanInfoImpl(this,ai);

        beanInfoMap.put(ai.getType(),abi);
        return abi;
    }

    public JaxBeanInfo getOrCreate(RuntimeTypeInfo e) {
        if(e instanceof RuntimeElementInfo)
            return getOrCreate((RuntimeElementInfo)e);
        if(e instanceof RuntimeClassInfo)
            return getOrCreate((RuntimeClassInfo)e);
        if(e instanceof RuntimeLeafInfo) {
            JaxBeanInfo bi = beanInfos.get(e); // must have been created
            assert bi!=null;
            return bi;
        }
        if(e instanceof RuntimeArrayInfo)
            return getOrCreate((RuntimeArrayInfo)e);
        if(e.getType()==Object.class) {
            // anyType
            JaxBeanInfo bi = beanInfoMap.get(Object.class);
            if(bi==null) {
                bi = new AnyTypeBeanInfo(this,e);
                beanInfoMap.put(Object.class,bi);
            }
            return bi;
        }

        throw new IllegalArgumentException();
    }

    /**
     * Gets the {@link JaxBeanInfo} object that can handle
     * the given JAXB-bound object.
     *
     * <p>
     * This method traverses the base classes of the given object.
     *
     * @return null
     *      if {@code c} isn't a JAXB-bound class and {@code fatal==false}.
     */
    public JaxBeanInfo getBeanInfo(Object o) {
        // don't allow xs:anyType beanInfo to handle all the unbound objects
        for( Class c=o.getClass(); c!=Object.class; c=c.getSuperclass()) {
            JaxBeanInfo bi = beanInfoMap.get(c);
            if(bi!=null)    return bi;
        }
        if(o instanceof Element)
            return beanInfoMap.get(Object.class);   // return the BeanInfo for xs:anyType
        for( Class c : o.getClass().getInterfaces()) {
            JaxBeanInfo bi = beanInfoMap.get(c);
            if(bi!=null)    return bi;
        }
        return null;
    }

    /**
     * Gets the {@link JaxBeanInfo} object that can handle
     * the given JAXB-bound object.
     *
     * @param fatal
     *      if true, the failure to look up will throw an exception.
     *      Otherwise it will just return null.
     */
    public JaxBeanInfo getBeanInfo(Object o, boolean fatal) throws JAXBException {
        JaxBeanInfo bi = getBeanInfo(o);
        if(bi!=null)    return bi;
        if(fatal) {
            if(o instanceof Document)
                throw new JAXBException(Messages.ELEMENT_NEEDED_BUT_FOUND_DOCUMENT.format(o.getClass()));
            throw new JAXBException(Messages.UNKNOWN_CLASS.format(o.getClass()));
        }
        return null;
    }

    /**
     * Gets the {@link JaxBeanInfo} object that can handle
     * the given JAXB-bound class.
     *
     * <p>
     * This method doesn't look for base classes.
     *
     * @return null
     *      if {@code c} isn't a JAXB-bound class and {@code fatal==false}.
     */
    public <T> JaxBeanInfo<T> getBeanInfo(Class<T> clazz) {
        return (JaxBeanInfo<T>)beanInfoMap.get(clazz);
    }

    /**
     * Gets the {@link JaxBeanInfo} object that can handle
     * the given JAXB-bound class.
     *
     * @param fatal
     *      if true, the failure to look up will throw an exception.
     *      Otherwise it will just return null.
     */
    public <T> JaxBeanInfo<T> getBeanInfo(Class<T> clazz, boolean fatal) throws JAXBException {
        JaxBeanInfo<T> bi = getBeanInfo(clazz);
        if(bi!=null)    return bi;
        if(fatal)
            throw new JAXBException(clazz.getName()+" is not known to this context");
        return null;
    }

    /**
     * Based on the tag name, determine what object to unmarshal,
     * and then set a new object and its loader to the current unmarshaller state.
     *
     * @return
     *      null if the given name pair is not recognized.
     */
    public Loader selectRootLoader(UnmarshallingContext.State state, TagName tag ) {
        JaxBeanInfo beanInfo = rootMap.get(tag.uri,tag.local);
        if(beanInfo==null)
            return null;

        return beanInfo.getLoader(this,true);
    }

    /**
     * Gets the {@link JaxBeanInfo} for the given named XML Schema type.
     *
     * @return
     *      null if the type name is not recognized. For schema
     *      languages other than XML Schema, this method always
     *      returns null.
     */
    public JaxBeanInfo getGlobalType(QName name) {
        return typeMap.get(name);
    }

    /**
     * Finds a type name that this context recognizes which is
     * "closest" to the given type name.
     *
     * <p>
     * This method is used for error recovery.
     */
    public String getNearestTypeName(QName name) {
        String[] all = new String[typeMap.size()];
        int i=0;
        for (QName qn : typeMap.keySet()) {
            if(qn.getLocalPart().equals(name.getLocalPart()))
                return qn.toString();  // probably a match, as people often gets confused about namespace.
            all[i++] = qn.toString();
        }

        String nearest = EditDistance.findNearest(name.toString(), all);

        if(EditDistance.editDistance(nearest,name.toString())>10)
            return null;    // too far apart.

        return nearest;
    }

    /**
     * Returns the set of valid root tag names.
     * For diagnostic use.
     */
    public Set<QName> getValidRootNames() {
        Set<QName> r = new TreeSet<>(QNAME_COMPARATOR);
        for (QNameMap.Entry e : rootMap.entrySet()) {
            r.add(e.createQName());
        }
        return r;
    }

    /**
     * Cache of UTF-8 encoded local names to improve the performance for the marshalling.
     */
    private Encoded[] utf8nameTable;

    public synchronized Encoded[] getUTF8NameTable() {
        if(utf8nameTable==null) {
            Encoded[] x = new Encoded[nameList.localNames.length];
            for( int i=0; i<x.length; i++ ) {
                Encoded e = new Encoded(nameList.localNames[i]);
                e.compact();
                x[i] = e;
            }
            utf8nameTable = x;
        }
        return utf8nameTable;
    }

    public int getNumberOfLocalNames() {
        return nameList.localNames.length;
    }

    public int getNumberOfElementNames() {
        return nameList.numberOfElementNames;
    }

    public int getNumberOfAttributeNames() {
        return nameList.numberOfAttributeNames;
    }

    /**
     * Creates a new identity transformer.
     */
    static Transformer createTransformer(boolean disableSecureProcessing) {
        try {
            SAXTransformerFactory tf = (SAXTransformerFactory)XmlFactory.createTransformerFactory(disableSecureProcessing);
            return tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new Error(e); // impossible
        }
    }

    /**
     * Creates a new identity transformer.
     */
    public static TransformerHandler createTransformerHandler(boolean disableSecureProcessing) {
        try {
            SAXTransformerFactory tf = (SAXTransformerFactory)XmlFactory.createTransformerFactory(disableSecureProcessing);
            return tf.newTransformerHandler();
        } catch (TransformerConfigurationException e) {
            throw new Error(e); // impossible
        }
    }

    /**
     * Creates a new DOM document.
     */
    static Document createDom(boolean disableSecurityProcessing) {
        synchronized(JAXBContextImpl.class) {
            if(db==null) {
                try {
                    DocumentBuilderFactory dbf = XmlFactory.createDocumentBuilderFactory(disableSecurityProcessing);
                    db = dbf.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    // impossible
                    throw new FactoryConfigurationError(e);
                }
            }
            return db.newDocument();
        }
    }

    @Override
    public MarshallerImpl createMarshaller() {
        return new MarshallerImpl(this,null);
    }

    @Override
    public UnmarshallerImpl createUnmarshaller() {
        return new UnmarshallerImpl(this,null);
    }

    @Override
    public JAXBIntrospector createJAXBIntrospector() {
        return new JAXBIntrospector() {
            @Override
            public boolean isElement(Object object) {
                return getElementName(object)!=null;
            }

            @Override
            public QName getElementName(Object jaxbElement) {
                try {
                    return JAXBContextImpl.this.getElementName(jaxbElement);
                } catch (JAXBException e) {
                    return null;
                }
            }
        };
    }

    private NonElement<Type,Class> getXmlType(RuntimeTypeInfoSet tis, TypeReference tr) {
        if(tr==null)
            throw new IllegalArgumentException();

        XmlJavaTypeAdapter xjta = tr.get(XmlJavaTypeAdapter.class);
        XmlList xl = tr.get(XmlList.class);

        Ref<Type,Class> ref = new Ref<>(annotationReader, tis.getNavigator(), tr.type, xjta, xl );

        return tis.getTypeInfo(ref);
    }

    @Override
    public void generateEpisode(Result output) {
        if(output==null)
            throw new IllegalArgumentException();
        createSchemaGenerator().writeEpisodeFile(ResultFactory.createSerializer(output));
    }

    @Override
    @SuppressWarnings("ThrowableInitCause")
    public void generateSchema(SchemaOutputResolver outputResolver) throws IOException {
        if(outputResolver==null)
            throw new IOException(Messages.NULL_OUTPUT_RESOLVER.format());

        final SAXParseException[] e = new SAXParseException[1];
        final SAXParseException[] w = new SAXParseException[1];

        createSchemaGenerator().write(outputResolver, new ErrorListener() {
            @Override
            public void error(SAXParseException exception) {
                e[0] = exception;
            }

            @Override
            public void fatalError(SAXParseException exception) {
                e[0] = exception;
            }

            @Override
            public void warning(SAXParseException exception) {
                w[0] = exception;
            }

            @Override
            public void info(SAXParseException exception) {}
        });

        if (e[0]!=null) {
            throw new IOException(Messages.FAILED_TO_GENERATE_SCHEMA.format(), e[0]);
        }
        if (w[0]!=null) {
            throw new IOException(Messages.ERROR_PROCESSING_SCHEMA.format(), w[0]);
        }
    }

    private XmlSchemaGenerator<Type,Class,Field,Method> createSchemaGenerator() {
        RuntimeTypeInfoSet tis;
        try {
            tis = getTypeInfoSet();
        } catch (IllegalAnnotationsException e) {
            // this shouldn't happen because we've already
            throw new AssertionError(e);
        }

        XmlSchemaGenerator<Type,Class,Field,Method> xsdgen =
                new XmlSchemaGenerator<>(tis.getNavigator(),tis);

        // JAX-RPC uses Bridge objects that collide with
        // @XmlRootElement.
        // we will avoid collision here
        Set<QName> rootTagNames = new HashSet<>();
        for (RuntimeElementInfo ei : tis.getAllElements()) {
            rootTagNames.add(ei.getElementName());
        }
        for (RuntimeClassInfo ci : tis.beans().values()) {
            if(ci.isElement())
                rootTagNames.add(ci.asElement().getElementName());
        }

        for (TypeReference tr : bridges.keySet()) {
            if(rootTagNames.contains(tr.tagName))
                continue;

            if(tr.type==void.class || tr.type==Void.class) {
                xsdgen.add(tr.tagName,false,null);
            } else
            if(tr.type==CompositeStructure.class) {
                // this is a special class we introduced for JAX-WS that we *don't* want in the schema
            } else {
                NonElement<Type,Class> typeInfo = getXmlType(tis,tr);
                xsdgen.add(tr.tagName, !tis.getNavigator().isPrimitive(tr.type),typeInfo);
            }
        }
        return xsdgen;
    }

    @Override
    public QName getTypeName(TypeReference tr) {
        try {
            NonElement<Type,Class> xt = getXmlType(getTypeInfoSet(),tr);
            if(xt==null)    throw new IllegalArgumentException();
            return xt.getTypeName();
        } catch (IllegalAnnotationsException e) {
            // impossible given that JAXBRIContext has been successfully built in the first place
            throw new AssertionError(e);
        }
    }

    @Override
    public <T> Binder<T> createBinder(Class<T> domType) {
        if(domType==Node.class)
            return (Binder<T>)createBinder();
        else
            return super.createBinder(domType);
    }

    @Override
    public Binder<Node> createBinder() {
        return new BinderImpl<>(this,new DOMScanner());
    }

    @Override
    public QName getElementName(Object o) throws JAXBException {
        JaxBeanInfo bi = getBeanInfo(o,true);
        if(!bi.isElement())
            return null;
        return new QName(bi.getElementNamespaceURI(o),bi.getElementLocalName(o));
    }

    @Override
    public QName getElementName(Class o) throws JAXBException {
        JaxBeanInfo bi = getBeanInfo(o,true);
        if(!bi.isElement())
            return null;
        return new QName(bi.getElementNamespaceURI(o),bi.getElementLocalName(o));
    }

    @Override
    public Bridge createBridge(TypeReference ref) {
        return bridges.get(ref);
    }

    @Override
    public RawAccessor getElementPropertyAccessor(Class wrapperBean, String nsUri, String localName) throws JAXBException {
        JaxBeanInfo bi = getBeanInfo(wrapperBean,true);
        if(!(bi instanceof ClassBeanInfoImpl))
            throw new JAXBException(wrapperBean+" is not a bean");

        for( ClassBeanInfoImpl cb = (ClassBeanInfoImpl) bi; cb!=null; cb=cb.superClazz) {
            for (Property p : cb.properties) {
                final Accessor acc = p.getElementPropertyAccessor(nsUri,localName);
                if(acc!=null)
                    return new RawAccessor() {
                        // Accessor.set/get are designed for unmarshaller/marshaller, and hence
                        // they go through an adapter behind the scene.
                        // this isn't desirable for JAX-WS, which essentially uses this method
                        // just as a reflection library. So use the "unadapted" version to
                        // achieve the desired semantics
                    @Override
                        public Object get(Object bean) throws AccessorException {
                            return acc.getUnadapted(bean);
                        }

                    @Override
                        public void set(Object bean, Object value) throws AccessorException {
                            acc.setUnadapted(bean,value);
                        }
                    };
            }
        }
        throw new JAXBException(new QName(nsUri,localName)+" is not a valid property on "+wrapperBean);
    }

    @Override
    public List<String> getKnownNamespaceURIs() {
        return Arrays.asList(nameList.namespaceURIs);
    }

    @Override
    public String getBuildId() {
        Package pkg = getClass().getPackage();
        if(pkg==null)   return null;
        return pkg.getImplementationVersion();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(Which.which(getClass()) + " Build-Id: " + getBuildId());
        buf.append("\nClasses known to this context:\n");

        Set<String> names = new TreeSet<>();  // sort them so that it's easy to read

        for (Class key : beanInfoMap.keySet())
            names.add(key.getName());

        for(String name: names)
            buf.append("  ").append(name).append('\n');

        return buf.toString();
    }

    /**
     * Gets the value of the xmime:contentType attribute on the given object, or null
     * if for some reason it couldn't be found, including any error.
     */
    public String getXMIMEContentType( Object o ) {
        JaxBeanInfo bi = getBeanInfo(o);
        if(!(bi instanceof ClassBeanInfoImpl))
            return null;

        ClassBeanInfoImpl cb = (ClassBeanInfoImpl) bi;
        for (Property p : cb.properties) {
            if (p instanceof AttributeProperty) {
                AttributeProperty ap = (AttributeProperty) p;
                if(ap.attName.equals(WellKnownNamespace.XML_MIME_URI,"contentType"))
                    try {
                        return (String)ap.xacc.print(o);
                    } catch (AccessorException | ClassCastException | SAXException e) {
                        return null;
                    }
            }
        }
        return null;
    }

    /**
     * Creates a  that includes the specified additional classes.
     */
    public JAXBContextImpl createAugmented(Class<?> clazz) throws JAXBException {
        Class[] newList = new Class[classes.length+1];
        System.arraycopy(classes,0,newList,0,classes.length);
        newList[classes.length] = clazz;

        JAXBContextBuilder builder = new JAXBContextBuilder(this);
        builder.setClasses(newList);
        return builder.build();
    }

    private static final Comparator<QName> QNAME_COMPARATOR = new Comparator<>() {
        @Override
        public int compare(QName lhs, QName rhs) {
            int r = lhs.getLocalPart().compareTo(rhs.getLocalPart());
            if (r != 0) return r;

            return lhs.getNamespaceURI().compareTo(rhs.getNamespaceURI());
        }
    };

    public static class JAXBContextBuilder {

        private boolean retainPropertyInfo = false;
        private boolean supressAccessorWarnings = false;
        private String defaultNsUri = "";
        private @NotNull RuntimeAnnotationReader annotationReader = new RuntimeInlineAnnotationReader();
        private @NotNull Map<Class,Class> subclassReplacements = Collections.emptyMap();
        private boolean c14nSupport = false;
        private Class[] classes;
        private Collection<TypeReference> typeRefs;
        private boolean xmlAccessorFactorySupport = false;
        private boolean allNillable;
        private boolean improvedXsiTypeHandling = true;
        private boolean disableSecurityProcessing = true;
        private Boolean backupWithParentNamespace = null; // null for System property to be used
        private int maxErrorsCount;

        public JAXBContextBuilder() {}

        public JAXBContextBuilder(JAXBContextImpl baseImpl) {
            this.supressAccessorWarnings = baseImpl.supressAccessorWarnings;
            this.retainPropertyInfo = baseImpl.retainPropertyInfo;
            this.defaultNsUri = baseImpl.defaultNsUri;
            this.annotationReader = baseImpl.annotationReader;
            this.subclassReplacements = baseImpl.subclassReplacements;
            this.c14nSupport = baseImpl.c14nSupport;
            this.classes = baseImpl.classes;
            this.typeRefs = baseImpl.bridges.keySet();
            this.xmlAccessorFactorySupport = baseImpl.xmlAccessorFactorySupport;
            this.allNillable = baseImpl.allNillable;
            this.disableSecurityProcessing = baseImpl.disableSecurityProcessing;
            this.backupWithParentNamespace = baseImpl.backupWithParentNamespace;
            this.maxErrorsCount = baseImpl.maxErrorsCount;
        }

        public JAXBContextBuilder setRetainPropertyInfo(boolean val) {
            this.retainPropertyInfo = val;
            return this;
        }

        public JAXBContextBuilder setSupressAccessorWarnings(boolean val) {
            this.supressAccessorWarnings = val;
            return this;
        }

        public JAXBContextBuilder setC14NSupport(boolean val) {
            this.c14nSupport = val;
            return this;
        }

        public JAXBContextBuilder setXmlAccessorFactorySupport(boolean val) {
            this.xmlAccessorFactorySupport = val;
            return this;
        }

        public JAXBContextBuilder setDefaultNsUri(String val) {
            this.defaultNsUri = val;
            return this;
        }

        public JAXBContextBuilder setAllNillable(boolean val) {
            this.allNillable = val;
            return this;
        }

        public JAXBContextBuilder setClasses(Class[] val) {
            this.classes = val;
            return this;
        }

        public JAXBContextBuilder setAnnotationReader(RuntimeAnnotationReader val) {
            this.annotationReader = val;
            return this;
        }

        public JAXBContextBuilder setSubclassReplacements(Map<Class,Class> val) {
            this.subclassReplacements = val;
            return this;
        }

        public JAXBContextBuilder setTypeRefs(Collection<TypeReference> val) {
            this.typeRefs = val;
            return this;
        }

        public JAXBContextBuilder setImprovedXsiTypeHandling(boolean val) {
            this.improvedXsiTypeHandling = val;
            return this;
        }

        public JAXBContextBuilder setDisableSecurityProcessing(boolean val) {
            this.disableSecurityProcessing = val;
            return this;
        }

        public JAXBContextBuilder setBackupWithParentNamespace(Boolean backupWithParentNamespace) {
            this.backupWithParentNamespace = backupWithParentNamespace;
            return this;
        }

        public JAXBContextBuilder setMaxErrorsCount(int maxErrorsCount) {
            this.maxErrorsCount = maxErrorsCount;
            return this;
        }

        public JAXBContextImpl build() throws JAXBException {

            // fool-proof
            if (this.defaultNsUri == null) {
                this.defaultNsUri = "";
            }

            if (this.subclassReplacements == null) {
                this.subclassReplacements = Collections.emptyMap();
            }

            if (this.annotationReader == null) {
                this.annotationReader = new RuntimeInlineAnnotationReader();
            }

            if (this.typeRefs == null) {
                this.typeRefs = Collections.emptyList();
            }

            return new JAXBContextImpl(this);
        }

    }

}
