/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import com.sun.istack.FinalArrayList;
import org.glassfish.jaxb.core.v2.TODO;
import org.glassfish.jaxb.core.v2.model.annotation.AnnotationSource;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.*;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.core.v2.runtime.Location;
import org.glassfish.jaxb.runtime.v2.runtime.SwaRefAdapter;
import jakarta.activation.MimeType;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * {@link ElementInfo} implementation.
 *
 * @author Kohsuke Kawaguchi
 */
class ElementInfoImpl<T,C,F,M> extends TypeInfoImpl<T,C,F,M> implements ElementInfo<T,C> {

    private final QName tagName;

    private final NonElement<T,C> contentType;

    private final T tOfJAXBElementT;

    private final T elementType;

    private final ClassInfo<T,C> scope;

    /**
     * Annotation that controls the binding.
     */
    private final XmlElementDecl anno;

    /**
     * If this element can substitute another element, the element name.
     * @see #link()
     */
    private ElementInfoImpl<T,C,F,M> substitutionHead;

    /**
     * Lazily constructed list of {@link ElementInfo}s that can substitute this element.
     * This could be null.
     * @see #link()
     */
    private FinalArrayList<ElementInfoImpl<T,C,F,M>> substitutionMembers;

    /**
     * The factory method from which this mapping was created.
     */
    private final M method;

    /**
     * If the content type is adapter, return that adapter.
     */
    private final Adapter<T,C> adapter;

    private final boolean isCollection;

    private final ID id;

    private final PropertyImpl property;
    private final MimeType expectedMimeType;
    private final boolean inlineBinary;
    private final QName schemaType;

    /**
     * Singleton instance of {@link ElementPropertyInfo} for this element.
     */
    protected class PropertyImpl implements
            ElementPropertyInfo<T,C>,
            TypeRef<T,C>,
            AnnotationSource {
        //
        // TypeRef impl
        //
        @Override
        public NonElement<T,C> getTarget() {
            return contentType;
        }
        @Override
        public QName getTagName() {
            return tagName;
        }

        @Override
        public List<? extends TypeRef<T,C>> getTypes() {
            return Collections.singletonList(this);
        }

        @Override
        public List<? extends NonElement<T,C>> ref() {
            return Collections.singletonList(contentType);
        }

        @Override
        public QName getXmlName() {
            return tagName;
        }

        @Override
        public boolean isCollectionRequired() {
            return false;
        }

        @Override
        public boolean isCollectionNillable() {
            return true;
        }

        @Override
        public boolean isNillable() {
            return true;
        }

        @Override
        public String getDefaultValue() {
            String v = anno.defaultValue();
            if(v.equals("\u0000"))
                return null;
            else
                return v;
        }

        @Override
        public ElementInfoImpl<T,C,F,M> parent() {
            return ElementInfoImpl.this;
        }

        @Override
        public String getName() {
            return "value";
        }

        @Override
        public String displayName() {
            return "JAXBElement#value";
        }

        @Override
        public boolean isCollection() {
            return isCollection;
        }

        /**
         * For {@link ElementInfo}s, a collection always means a list of values.
         */
        @Override
        public boolean isValueList() {
            return isCollection;
        }

        @Override
        public boolean isRequired() {
            return true;
        }

        @Override
        public PropertyKind kind() {
            return PropertyKind.ELEMENT;
        }

        @Override
        public Adapter<T,C> getAdapter() {
            return adapter;
        }

        @Override
        public ID id() {
            return id;
        }

        @Override
        public MimeType getExpectedMimeType() {
            return expectedMimeType;
        }

        @Override
        public QName getSchemaType() {
            return schemaType;
        }

        @Override
        public boolean inlineBinaryData() {
            return inlineBinary;
        }

        @Override
        public PropertyInfo<T,C> getSource() {
            return this;
        }

        //
        //
        // AnnotationSource impl
        //
        //
        @Override
        public <A extends Annotation> A readAnnotation(Class<A> annotationType) {
            return reader().getMethodAnnotation(annotationType,method,ElementInfoImpl.this);
        }

        @Override
        public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
            return reader().hasMethodAnnotation(annotationType,method);
        }
    }

    /**
     * @param m
     *      The factory method on ObjectFactory that comes with {@link XmlElementDecl}.
     */
    public ElementInfoImpl(ModelBuilder<T,C,F,M> builder,
                           RegistryInfoImpl<T,C,F,M> registry, M m ) throws IllegalAnnotationException {
        super(builder,registry);

        this.method = m;
        anno = reader().getMethodAnnotation( XmlElementDecl.class, m, this );
        assert anno!=null;  // the caller should check this
        assert anno instanceof Locatable;

        elementType = nav().getReturnType(m);
        T baseClass = nav().getBaseClass(elementType,nav().asDecl(JAXBElement.class));
        if(baseClass==null)
            throw new IllegalAnnotationException(
                Messages.XML_ELEMENT_MAPPING_ON_NON_IXMLELEMENT_METHOD.format(nav().getMethodName(m)),
                anno );

        tagName = parseElementName(anno);
        T[] methodParams = nav().getMethodParameters(m);

        // adapter
        Adapter<T,C> a = null;
        if(methodParams.length>0) {
            XmlJavaTypeAdapter adapter = reader().getMethodAnnotation(XmlJavaTypeAdapter.class,m,this);
            if(adapter!=null)
                a = new Adapter<>(adapter,reader(),nav());
            else {
                XmlAttachmentRef xsa = reader().getMethodAnnotation(XmlAttachmentRef.class,m,this);
                if(xsa!=null) {
                    TODO.prototype("in Annotation Processing swaRefAdapter isn't avaialble, so this returns null");
                    a = new Adapter<>(owner.nav.asDecl(SwaRefAdapter.class),owner.nav);
                }
            }
        }
        this.adapter = a;

        // T of JAXBElement<T>
        tOfJAXBElementT =
            methodParams.length>0 ? methodParams[0] // this is more reliable, as it works even for ObjectFactory that sometimes have to return public types
            : nav().getTypeArgument(baseClass,0); // fall back to infer from the return type if no parameter.
        
        if(adapter==null) {
            T list = nav().getBaseClass(tOfJAXBElementT,nav().asDecl(List.class));
            if(list==null) {
                isCollection = false;
                contentType = builder.getTypeInfo(tOfJAXBElementT,this);  // suck this type into the current set.
            } else {
                isCollection = true;
                contentType = builder.getTypeInfo(nav().getTypeArgument(list,0),this);
            }
        } else {
            // but if adapted, use the adapted type
            contentType = builder.getTypeInfo(this.adapter.defaultType,this);
            isCollection = false;
        }

        // scope
        T s = reader().getClassValue(anno,"scope");
        if(nav().isSameType(s, nav().ref(XmlElementDecl.GLOBAL.class)))
            scope = null;
        else {
            // TODO: what happens if there's an error?
            NonElement<T,C> scp = builder.getClassInfo(nav().asDecl(s),this);
            if(!(scp instanceof ClassInfo)) {
                throw new IllegalAnnotationException(
                    Messages.SCOPE_IS_NOT_COMPLEXTYPE.format(nav().getTypeName(s)),
                    anno );
            }
            scope = (ClassInfo<T,C>)scp;
        }

        id = calcId();

        property = createPropertyImpl();

        this.expectedMimeType = Util.calcExpectedMediaType(property,builder);
        this.inlineBinary = reader().hasMethodAnnotation(XmlInlineBinaryData.class,method);
        this.schemaType = Util.calcSchemaType(reader(),property,registry.registryClass,
                getContentInMemoryType(),this);
    }

    final QName parseElementName(XmlElementDecl e) {
        String local = e.name();
        String nsUri = e.namespace();
        if(nsUri.equals("##default")) {
            // if defaulted ...
            XmlSchema xs = reader().getPackageAnnotation(XmlSchema.class,
                nav().getDeclaringClassForMethod(method),this);
            if(xs!=null)
                nsUri = xs.namespace();
            else {
                nsUri = builder.defaultNsUri;
            }
        }

        return new QName(nsUri.intern(),local.intern());
    }

    protected PropertyImpl createPropertyImpl() {
        return new PropertyImpl();
    }

    @Override
    public ElementPropertyInfo<T,C> getProperty() {
        return property;
    }

    @Override
    public NonElement<T,C> getContentType() {
        return contentType;
    }

    @Override
    public T getContentInMemoryType() {
        if(adapter==null) {
            return tOfJAXBElementT;
        } else {
            return adapter.customType;
        }
    }

    @Override
    public QName getElementName() {
        return tagName;
    }

    @Override
    public T getType() {
        return elementType;
    }

    /**
     * Leaf-type cannot be referenced from IDREF.
     *
     * @deprecated
     *      why are you calling a method whose return value is always known?
     */
    @Override
    @Deprecated
    public final boolean canBeReferencedByIDREF() {
        return false;
    }

    private ID calcId() {
        // TODO: share code with PropertyInfoImpl
        if(reader().hasMethodAnnotation(XmlID.class,method)) {
            return ID.ID;
        } else
        if(reader().hasMethodAnnotation(XmlIDREF.class,method)) {
            return ID.IDREF;
        } else {
            return ID.NONE;
        }
    }

    @Override
    public ClassInfo<T, C> getScope() {
        return scope;
    }

    @Override
    public ElementInfo<T,C> getSubstitutionHead() {
        return substitutionHead;
    }

    @Override
    public Collection<? extends ElementInfoImpl<T,C,F,M>> getSubstitutionMembers() {
        if(substitutionMembers==null)
            return Collections.emptyList();
        else
            return substitutionMembers;
    }

    /**
     * Called after all the {@link TypeInfo}s are collected into the {@link #owner}.
     */
    /*package*/@Override
 void link() {
        // substitution head
        if(anno.substitutionHeadName().length()!=0) {
            QName name = new QName(
                anno.substitutionHeadNamespace(), anno.substitutionHeadName() );
            substitutionHead = owner.getElementInfo(null,name);
            if(substitutionHead==null) {
                builder.reportError(
                    new IllegalAnnotationException(Messages.NON_EXISTENT_ELEMENT_MAPPING.format(
                        name.getNamespaceURI(),name.getLocalPart()), anno));
                // recover by ignoring this substitution declaration
            } else
                substitutionHead.addSubstitutionMember(this);
        } else
            substitutionHead = null;
        super.link();
    }

    private void addSubstitutionMember(ElementInfoImpl<T,C,F,M> child) {
        if(substitutionMembers==null)
            substitutionMembers = new FinalArrayList<>();
        substitutionMembers.add(child);
    }

    @Override
    public Location getLocation() {
        return nav().getMethodLocation(method);
    }
}
