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

import org.glassfish.jaxb.core.v2.TODO;
import org.glassfish.jaxb.core.v2.model.annotation.AnnotationReader;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.*;
import org.glassfish.jaxb.core.v2.model.nav.Navigator;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.core.v2.runtime.Location;
import org.glassfish.jaxb.runtime.v2.runtime.SwaRefAdapter;
import jakarta.activation.MimeType;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Default partial implementation for {@link PropertyInfo}.
 *
 * @author Kohsuke Kawaguchi
 */
abstract class PropertyInfoImpl<T,C,F,M>
    implements PropertyInfo<T,C>, Locatable, Comparable<PropertyInfoImpl<T,C,F,M>> /*by their names*/ {

    /**
     * Object that reads annotations.
     */
    protected final PropertySeed<T,C,F,M> seed;

    private final boolean isCollection;

    private final ID id;

    private final MimeType expectedMimeType;
    private final boolean inlineBinary;
    private final QName schemaType;

    protected final ClassInfoImpl<T,C,F,M> parent;

    private final Adapter<T,C> adapter;

    protected PropertyInfoImpl(ClassInfoImpl<T,C,F,M> parent, PropertySeed<T,C,F,M> spi) {
        this.seed = spi;
        this.parent = parent;

        if(parent==null)
            /*
                Various people reported a bug where this parameter is somehow null.
                In an attempt to catch the error better, let's do an explicit check here.

                http://forums.java.net/jive/thread.jspa?threadID=18479
                http://forums.java.net/jive/thread.jspa?messageID=165946
             */
            throw new AssertionError();

        MimeType mt = Util.calcExpectedMediaType(seed,parent.builder);
        if(mt!=null && !kind().canHaveXmlMimeType) {
            parent.builder.reportError(new IllegalAnnotationException(
                Messages.ILLEGAL_ANNOTATION.format(XmlMimeType.class.getName()),
                seed.readAnnotation(XmlMimeType.class)
            ));
            mt = null;
        }
        this.expectedMimeType = mt;
        this.inlineBinary = seed.hasAnnotation(XmlInlineBinaryData.class);

        T t = seed.getRawType();

        // check if there's an adapter applicable to the whole property
        XmlJavaTypeAdapter xjta = getApplicableAdapter(t);
        if(xjta!=null) {
            isCollection = false;
            adapter = new Adapter<>(xjta,reader(),nav());
        } else {
            // check if the adapter is applicable to the individual item in the property

            this.isCollection = nav().isSubClassOf(t, nav().ref(Collection.class))
                             || nav().isArrayButNotByteArray(t);

            xjta = getApplicableAdapter(getIndividualType());
            if(xjta==null) {
                // ugly ugly hack, but we implement swaRef as adapter
                XmlAttachmentRef xsa = seed.readAnnotation(XmlAttachmentRef.class);
                if(xsa!=null) {
                    parent.builder.hasSwaRef = true;
                    adapter = new Adapter<>(nav().asDecl(SwaRefAdapter.class),nav());
                } else {
                    adapter = null;

                    // if this field has adapter annotation but not applicable,
                    // that must be an error of the user
                    xjta = seed.readAnnotation(XmlJavaTypeAdapter.class);
                    if(xjta!=null) {
                        T ad = reader().getClassValue(xjta,"value");
                        parent.builder.reportError(new IllegalAnnotationException(
                            Messages.UNMATCHABLE_ADAPTER.format(
                                    nav().getTypeName(ad), nav().getTypeName(t)),
                            xjta
                        ));
                    }
                }
            } else {
                adapter = new Adapter<>(xjta,reader(),nav());
            }
        }

        this.id = calcId();
        this.schemaType = Util.calcSchemaType(reader(),seed,parent.clazz,
                getIndividualType(),this);
    }


    @Override
    public ClassInfoImpl<T,C,F,M> parent() {
        return parent;
    }

    protected final Navigator<T,C,F,M> nav() {
        return parent.nav();
    }
    protected final AnnotationReader<T,C,F,M> reader() {
        return parent.reader();
    }

    public T getRawType() {
        return seed.getRawType();
    }

    public T getIndividualType() {
        if(adapter!=null)
            return adapter.defaultType;
        T raw = getRawType();
        if(!isCollection()) {
            return raw;
        } else {
            if(nav().isArrayButNotByteArray(raw))
                return nav().getComponentType(raw);

            T bt = nav().getBaseClass(raw, nav().asDecl(Collection.class) );
            if(nav().isParameterizedType(bt))
                return nav().getTypeArgument(bt,0);
            else
                return nav().ref(Object.class);
        }
    }

    @Override
    public final String getName() {
        return seed.getName();
    }

    /**
     * Checks if the given adapter is applicable to the declared property type.
     */
    private boolean isApplicable(XmlJavaTypeAdapter jta, T declaredType ) {
        if(jta==null)   return false;

        T type = reader().getClassValue(jta,"type");
        if(nav().isSameType(declaredType, type))
            return true;    // for types explicitly marked in XmlJavaTypeAdapter.type()
        
        T ad = reader().getClassValue(jta,"value");
        T ba = nav().getBaseClass(ad, nav().asDecl(XmlAdapter.class));
        if(!nav().isParameterizedType(ba))
            return true;   // can't check type applicability. assume Object, which means applicable to any.
        T inMemType = nav().getTypeArgument(ba, 1);

        return nav().isSubClassOf(declaredType,inMemType);
    }

    private XmlJavaTypeAdapter getApplicableAdapter(T type) {
        XmlJavaTypeAdapter jta = seed.readAnnotation(XmlJavaTypeAdapter.class);
        if(jta!=null && isApplicable(jta,type))
            return jta;

        // check the applicable adapters on the package
        XmlJavaTypeAdapters jtas = reader().getPackageAnnotation(XmlJavaTypeAdapters.class, parent.clazz, seed );
        if(jtas!=null) {
            for (XmlJavaTypeAdapter xjta : jtas.value()) {
                if(isApplicable(xjta,type))
                    return xjta;
            }
        }
        jta = reader().getPackageAnnotation(XmlJavaTypeAdapter.class, parent.clazz, seed );
        if(isApplicable(jta,type))
            return jta;

        // then on the target class
        C refType = nav().asDecl(type);
        if(refType!=null) {
            jta = reader().getClassAnnotation(XmlJavaTypeAdapter.class, refType, seed );
            if(jta!=null && isApplicable(jta,type)) // the one on the type always apply.
                return jta;
        }

        return null;
    }

    /**
     * This is the default implementation of the getAdapter method
     * defined on many of the {@link PropertyInfo}-derived classes.
     */
    @Override
    public Adapter<T,C> getAdapter() {
        return adapter;
    }


    @Override
    public final String displayName() {
        return nav().getClassName(parent.getClazz())+'#'+getName();
    }

    @Override
    public final ID id() {
        return id;
    }

    private ID calcId() {
        if(seed.hasAnnotation(XmlID.class)) {
            // check the type
            if(!nav().isSameType(getIndividualType(), nav().ref(String.class)))
                parent.builder.reportError(new IllegalAnnotationException(
                    Messages.ID_MUST_BE_STRING.format(getName()), seed )
                );
            return ID.ID;
        } else
        if(seed.hasAnnotation(XmlIDREF.class)) {
            return ID.IDREF;
        } else {
            return ID.NONE;
        }
    }

    @Override
    public final MimeType getExpectedMimeType() {
        return expectedMimeType;
    }

    @Override
    public final boolean inlineBinaryData() {
        return inlineBinary;
    }

    @Override
    public final QName getSchemaType() {
        return schemaType;
    }

    @Override
    public final boolean isCollection() {
        return isCollection;
    }

    /**
     * Called after all the {@link TypeInfo}s are collected into the governing {@link TypeInfoSet}.
     *
     * Derived class can do additional actions to complete the model.
     */
    protected void link() {
        if(id==ID.IDREF) {
            // make sure that the refereced type has ID
            for (TypeInfo<T,C> ti : ref()) {
                if(!ti.canBeReferencedByIDREF())
                    parent.builder.reportError(new IllegalAnnotationException(
                    Messages.INVALID_IDREF.format(
                        parent.builder.nav.getTypeName(ti.getType())), this ));
            }
        }
    }

    /**
     * A  is always referenced by its enclosing class,
     * so return that as the upstream.
     */
    @Override
    public Locatable getUpstream() {
        return parent;
    }

    @Override
    public Location getLocation() {
        return seed.getLocation();
    }


//
//
// convenience methods for derived classes
//
//


    /**
     * Computes the tag name from a {@link XmlElement} by taking the defaulting into account.
     */
    protected final QName calcXmlName(XmlElement e) {
        if(e!=null)
            return calcXmlName(e.namespace(),e.name());
        else
            return calcXmlName("##default","##default");
    }

    /**
     * Computes the tag name from a {@link XmlElementWrapper} by taking the defaulting into account.
     */
    protected final QName calcXmlName(XmlElementWrapper e) {
        if(e!=null)
            return calcXmlName(e.namespace(),e.name());
        else
            return calcXmlName("##default","##default");
    }

    private QName calcXmlName(String uri,String local) {
        // compute the default
        TODO.checkSpec();
        if(local.length()==0 || local.equals("##default"))
            local = seed.getName();
        if(uri.equals("##default")) {
            XmlSchema xs = reader().getPackageAnnotation( XmlSchema.class, parent.getClazz(), this );
            // JAX-RPC doesn't want the default namespace URI swapping to take effect to
            // local "unqualified" elements. UGLY.
            if(xs!=null) {
                switch(xs.elementFormDefault()) {
                case QUALIFIED:
                    QName typeName = parent.getTypeName();
                    if(typeName!=null)
                        uri = typeName.getNamespaceURI();
                    else
                        uri = xs.namespace();
                    if(uri.length()==0)
                        uri = parent.builder.defaultNsUri;
                    break;
                case UNQUALIFIED:
                case UNSET:
                    uri = "";
                }
            } else {
                uri = parent.builder.defaultNsUri;
            }
        }
        return new QName(uri.intern(),local.intern());
    }

    @Override
    public int compareTo(PropertyInfoImpl<T,C,F,M> that) {
        return this.getName().compareTo(that.getName());
    }

    @Override
    public final <A extends Annotation> A readAnnotation(Class<A> annotationType) {
        return seed.readAnnotation(annotationType);
    }

    @Override
    public final boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return seed.hasAnnotation(annotationType);
    }
}
