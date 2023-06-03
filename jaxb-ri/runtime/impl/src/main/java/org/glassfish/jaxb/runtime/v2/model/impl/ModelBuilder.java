/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAttachmentRef;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;
import org.glassfish.jaxb.core.v2.model.annotation.AnnotationReader;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.ArrayInfo;
import org.glassfish.jaxb.core.v2.model.core.ClassInfo;
import org.glassfish.jaxb.core.v2.model.core.ElementInfo;
import org.glassfish.jaxb.core.v2.model.core.EnumLeafInfo;
import org.glassfish.jaxb.core.v2.model.core.ErrorHandler;
import org.glassfish.jaxb.core.v2.model.core.LeafInfo;
import org.glassfish.jaxb.core.v2.model.core.NonElement;
import org.glassfish.jaxb.core.v2.model.core.PropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.core.v2.model.core.Ref;
import org.glassfish.jaxb.core.v2.model.core.RegistryInfo;
import org.glassfish.jaxb.core.v2.model.core.TypeInfo;
import org.glassfish.jaxb.core.v2.model.core.TypeInfoSet;
import org.glassfish.jaxb.core.v2.model.impl.ModelBuilderI;
import org.glassfish.jaxb.core.v2.model.nav.Navigator;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.runtime.v2.model.annotation.ClassLocatable;
import org.glassfish.jaxb.runtime.v2.model.runtime.RuntimePropertyInfo;

import javax.xml.namespace.QName;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builds a {@link TypeInfoSet} (a set of JAXB properties)
 * by using {@link ElementInfoImpl} and {@link ClassInfoImpl}.
 * from annotated Java classes.
 *
 * <p>
 * This class uses {@link Navigator} and {@link AnnotationReader} to
 * work with arbitrary annotation source and arbitrary Java model.
 * For this purpose this class is parameterized.
 *
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ModelBuilder<T,C,F,M> implements ModelBuilderI<T,C,F,M> {
    private static final Logger logger;

    /**
     * {@link TypeInfo}s that are built will go into this set.
     */
    final TypeInfoSetImpl<T,C,F,M> typeInfoSet;

    public final AnnotationReader<T,C,F,M> reader;

    public final Navigator<T,C,F,M> nav;

    /**
     * Used to detect collisions among global type names.
     */
    private final Map<QName,TypeInfo> typeNames = new HashMap<>();

    /**
     * JAXB doesn't want to use namespaces unless we are told to, but WS-I BP
     * conformace requires JAX-RPC to always use a non-empty namespace URI.
     * (see http://www.ws-i.org/Profiles/BasicProfile-1.0-2004-04-16.html#WSDLTYPES R2105)
     *
     * <p>
     * To work around this issue, we allow the use of the empty namespaces to be
     * replaced by a particular designated namespace URI.
     *
     * <p>
     * This field keeps the value of that replacing namespace URI.
     * When there's no replacement, this field is set to "".
     */
    public final String defaultNsUri;


    /**
     * Packages whose registries are already added.
     */
    /*package*/ final Map<String,RegistryInfoImpl<T,C,F,M>> registries
            = new HashMap<>();

    private final Map<C,C> subclassReplacements;

    /**
     * @see #setErrorHandler
     */
    private ErrorHandler errorHandler;
    private boolean hadError;

    /**
     * Set to true if the model includes {@link XmlAttachmentRef}. JAX-WS
     * needs to know this information.
     */
    public boolean hasSwaRef;

    private final ErrorHandler proxyErrorHandler = new ErrorHandler() {
        @Override
        public void error(IllegalAnnotationException e) {
            reportError(e);
        }
    };

    public ModelBuilder(
            AnnotationReader<T, C, F, M> reader,
            Navigator<T, C, F, M> navigator,
            Map<C, C> subclassReplacements,
            String defaultNamespaceRemap
    ) {

        this.reader = reader;
        this.nav = navigator;
        this.subclassReplacements = subclassReplacements;
        if(defaultNamespaceRemap==null)
            defaultNamespaceRemap = "";
        this.defaultNsUri = defaultNamespaceRemap;
        reader.setErrorHandler(proxyErrorHandler);
        typeInfoSet = (TypeInfoSetImpl<T, C, F, M>) createTypeInfoSet();
    }

    /*
     * Logger init
     */
    static {
        logger = Logger.getLogger(ModelBuilder.class.getName());
    }

    protected TypeInfoSet<T,C,F,M> createTypeInfoSet() {
        return new TypeInfoSetImpl<>(nav,reader,BuiltinLeafInfoImpl.createLeaves(nav));
    }

    /**
     * Builds a JAXB {@link ClassInfo} model from a given class declaration
     * and adds that to this model owner.
     *
     * <p>
     * Return type is either {@link ClassInfo} or {@link LeafInfo} (for types like
     * {@link String} or {@link Enum}-derived ones)
     */
    public NonElement<T,C> getClassInfo( C clazz, Locatable upstream ) {
        return getClassInfo(clazz,false,upstream);
    }

    /**
     * For limited cases where the caller needs to search for a super class.
     * This is necessary because we don't want {@link #subclassReplacements}
     * to kick in for the super class search, which will cause infinite recursion.
     */
    public NonElement<T,C> getClassInfo( C clazz, boolean searchForSuperClass, Locatable upstream ) {
        assert clazz!=null;
        NonElement<T,C> r = typeInfoSet.getClassInfo(clazz);
        if(r!=null)
            return r;

        if(nav.isEnum(clazz)) {
            EnumLeafInfoImpl<T,C,F,M> li = (EnumLeafInfoImpl<T, C, F, M>) createEnumLeafInfo(clazz,upstream);
            typeInfoSet.add(li);
            r = li;
            addTypeName(r);
        } else {
            boolean isReplaced = subclassReplacements.containsKey(clazz);
            if(isReplaced && !searchForSuperClass) {
                // handle it as if the replacement was specified
                r = getClassInfo(subclassReplacements.get(clazz),upstream);
            } else
            if(reader.hasClassAnnotation(clazz,XmlTransient.class) || isReplaced) {
                // handle it as if the base class was specified
                r = getClassInfo( nav.getSuperClass(clazz), searchForSuperClass,
                        new ClassLocatable<>(upstream,clazz,nav) );
            } else {
                ClassInfoImpl<T,C,F,M> ci = (ClassInfoImpl<T, C, F, M>) createClassInfo(clazz,upstream);
                typeInfoSet.add(ci);

                // compute the closure by eagerly expanding references
                for( PropertyInfo<T,C> p : ci.getProperties() ) {
                    if(p.kind()== PropertyKind.REFERENCE) {
                        // make sure that we have a registry for this package
                        addToRegistry(clazz, (Locatable) p);
                        Class[] prmzdClasses = getParametrizedTypes(p);
                        if (prmzdClasses != null) {
                            for (Class prmzdClass : prmzdClasses) {
                                if (prmzdClass != clazz) {
                                    addToRegistry((C) prmzdClass, (Locatable) p);
                                }
                            }
                        }
                    }

                    for( TypeInfo<T,C> t : p.ref() )
                        ; // just compute a reference should be suffice
                }
                ci.getBaseClass(); // same as above.

                r = ci;
                addTypeName(r);
            }
        }


        // more reference closure expansion. @XmlSeeAlso
        XmlSeeAlso sa = reader.getClassAnnotation(XmlSeeAlso.class, clazz, upstream);
        if(sa!=null) {
            for( T t : reader.getClassArrayValue(sa,"value") ) {
                getTypeInfo(t,(Locatable)sa);
            }
        }


        return r;
    }

    /**
     * Adding package's ObjectFactory methods to registry
     * @param clazz which package will be used
     * @param p location
     */
    private void addToRegistry(C clazz, Locatable p) {
        String pkg = nav.getPackageName(clazz);
        if (!registries.containsKey(pkg)) {
            // insert the package's object factory
            C c = nav.loadObjectFactory(clazz, pkg);
            if (c != null)
                addRegistry(c, p);
        }
    }

    /**
     * Getting parametrized classes of {@code JAXBElement<...>} property
     * @param p property which parametrized types we will try to get
     * @return null - if it's not JAXBElement property, or it's not parametrized, and array of parametrized classes in other case
     */
    private Class[] getParametrizedTypes(PropertyInfo p) {
        try {
            Type pType = ((RuntimePropertyInfo) p).getIndividualType();
            if (pType instanceof ParameterizedType) {
                ParameterizedType prmzdType = (ParameterizedType) pType;
                if (prmzdType.getRawType() == JAXBElement.class) {
                    Type[] actualTypes = prmzdType.getActualTypeArguments();
                    Class[] result = new Class[actualTypes.length];
                    for (int i = 0; i < actualTypes.length; i++) {
                        result[i] = (Class) actualTypes[i];
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            logger.log(Level.FINE, "Error in ModelBuilder.getParametrizedTypes. " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks the uniqueness of the type name.
     */
    private void addTypeName(NonElement<T, C> r) {
        QName t = r.getTypeName();
        if(t==null)     return;

        TypeInfo old = typeNames.put(t,r);
        if(old!=null) {
            // collision
            reportError(new IllegalAnnotationException(
                    Messages.CONFLICTING_XML_TYPE_MAPPING.format(r.getTypeName()),
                    old, r ));
        }
    }

    /**
     * Have the builder recognize the type (if it hasn't done so yet),
     * and returns a {@link NonElement} that represents it.
     *
     * @return
     *      always non-null.
     */
    public NonElement<T,C> getTypeInfo(T t,Locatable upstream) {
        NonElement<T,C> r = typeInfoSet.getTypeInfo(t);
        if(r!=null)     return r;

        if(nav.isArray(t)) { // no need for checking byte[], because above typeInfoset.getTypeInfo() would return non-null
            ArrayInfoImpl<T,C,F,M> ai = (ArrayInfoImpl<T, C, F, M>) createArrayInfo(upstream, t);
            addTypeName(ai);
            typeInfoSet.add(ai);
            return ai;
        }

        C c = nav.asDecl(t);
        assert c!=null : t.toString()+" must be a leaf, but we failed to recognize it.";
        return getClassInfo(c,upstream);
    }

    /**
     * This method is used to add a root reference to a model.
     */
    public NonElement<T,C> getTypeInfo(Ref<T,C> ref) {
        // TODO: handle XmlValueList
        assert !ref.valueList;
        C c = nav.asDecl(ref.type);
        if(c!=null && reader.getClassAnnotation(XmlRegistry.class,c,null/*TODO: is this right?*/)!=null) {
            if(!registries.containsKey(nav.getPackageName(c)))
                addRegistry(c,null);
            return null;    // TODO: is this correct?
        } else
            return getTypeInfo(ref.type,null);
    }


    protected EnumLeafInfo<T,C> createEnumLeafInfo(C clazz,Locatable upstream) {
        return new EnumLeafInfoImpl<>(this,upstream,clazz,nav.use(clazz));
    }

    protected ClassInfo<T,C> createClassInfo(C clazz, Locatable upstream ) {
        return new ClassInfoImpl<>(this,upstream,clazz);
    }

    protected ElementInfo<T,C> createElementInfo(
            RegistryInfo<T,C> registryInfo, M m) throws IllegalAnnotationException {
        return new ElementInfoImpl<>(this, (RegistryInfoImpl<T, C, F, M>) registryInfo,m);
    }

    protected ArrayInfo<T,C> createArrayInfo(Locatable upstream, T arrayType) {
        return new ArrayInfoImpl<>(this,upstream,arrayType);
    }


    /**
     * Visits a class with {@link XmlRegistry} and records all the element mappings
     * in it.
     */
    public RegistryInfo<T,C> addRegistry(C registryClass, Locatable upstream ) {
        return new RegistryInfoImpl<>(this,upstream,registryClass);
    }

    /**
     * Gets a {@link RegistryInfo} for the given package.
     *
     * @return
     *      null if no registry exists for the package.
     *      unlike other getXXX methods on this class,
     *      this method is side-effect free.
     */
    public RegistryInfo<T,C> getRegistry(String packageName) {
        return registries.get(packageName);
    }

    private boolean linked;

    /**
     * Called after all the classes are added to the type set
     * to "link" them together.
     *
     * <p>
     * Don't expose implementation classes in the signature.
     *
     * @return
     *      fully built {@link TypeInfoSet} that represents the model,
     *      or null if there was an error.
     */
    public TypeInfoSet<T,C,F,M> link() {

        assert !linked;
        linked = true;

        for( ElementInfoImpl ei : typeInfoSet.getAllElements() )
            ei.link();

        for( ClassInfoImpl ci : typeInfoSet.beans().values() )
            ci.link();

        for( EnumLeafInfoImpl li : typeInfoSet.enums().values() )
            li.link();

        if(hadError)
            return null;
        else
            return typeInfoSet;
    }

//
//
// error handling
//
//

    /**
     * Sets the error handler that receives errors discovered during the model building.
     *
     * @param errorHandler
     *      can be null.
     */
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public final void reportError(IllegalAnnotationException e) {
        hadError = true;
        if(errorHandler!=null)
            errorHandler.error(e);
    }

    public boolean isReplaced(C sc) {
        return subclassReplacements.containsKey(sc);
    }

    @Override
    public Navigator<T, C, F, M> getNavigator() {
        return nav;
    }

    @Override
    public AnnotationReader<T, C, F, M> getReader() {
        return reader;
    }
}
