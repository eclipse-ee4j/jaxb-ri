/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElement;
import javax.xml.namespace.QName;

import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.istack.Nullable;
import static com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode.NOT_REPEATED;
import static com.sun.tools.xjc.model.CElementPropertyInfo.CollectionMode.REPEATED_VALUE;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.model.nav.NavigatorImpl;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIInlineBinaryData;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIFactoryMethod;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.Ring;
import org.glassfish.jaxb.core.v2.model.core.ElementInfo;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XmlString;

import org.xml.sax.Locator;

/**
 * {@link ElementInfo} implementation for the compile-time model.
 *
 * <p>
 * As an NType, it represents the Java representation of this element
 * (either {@code JAXBElement<T>} or Foo).
 *
 * @author Kohsuke Kawaguchi
 */
public final class CElementInfo extends AbstractCElement
    implements ElementInfo<NType,NClass>, NType, CClassInfoParent {

    private final QName tagName;

    /**
     * Represents {@code JAXBElement<ContentType>}.
     */
    private NType type;

    /**
     * If this element produces its own class, the short name of that class.
     * Otherwise null.
     */
    private String className;

    /**
     * If this element is global, the element info is considered to be
     * package-level, and this points to the package in which this element
     * lives in.
     *
     * <p>
     * For local elements, this points to the parent {@link CClassInfo}.
     */
    public final CClassInfoParent parent;

    private CElementInfo substitutionHead;

    /**
     * Lazily computed.
     */
    private Set<CElementInfo> substitutionMembers;

    /**
     * {@link Model} that owns this object.
     */
    private final Model model;

    private CElementPropertyInfo property;

    /**
     * Custom {@link #getSqueezedName() squeezed name}, if any.
     */
    private /*almost final*/ @Nullable String squeezedName;

    /**
     * Creates an element in the given parent.
     *
     * <p>
     * When using this construction, {@link #initContentType(TypeUse, XSElementDecl, XmlString)}
     * must not be invoked.
     */
    public CElementInfo(Model model,QName tagName, CClassInfoParent parent, TypeUse contentType, XmlString defaultValue, XSElementDecl source, CCustomizations customizations, Locator location ) {
        super(model,source,location,customizations);
        this.tagName = tagName;
        this.model = model;
        this.parent = parent;
        if(contentType!=null)
            initContentType(contentType, source, defaultValue);

        model.add(this);
    }

    /**
     * Creates an element with a class in the given parent.
     *
     * <p>
     * When using this construction, the caller must use
     * {@link #initContentType(TypeUse, XSElementDecl, XmlString)} to fill in the content type
     * later.
     *
     * This is to avoid a circular model construction dependency between buidling a type
     * inside an element and element itself. To build a content type, you need to have
     * {@link CElementInfo} for a parent, so we can't take it as a constructor parameter.
     */
    public CElementInfo(Model model,QName tagName, CClassInfoParent parent, String className, CCustomizations customizations, Locator location ) {
        this(model,tagName,parent,null,null,null,customizations,location);
        this.className = className;
    }

    public void initContentType(TypeUse contentType, @Nullable XSElementDecl source, XmlString defaultValue) {
        assert this.property==null; // must not be called twice

        this.property = new CElementPropertyInfo("Value",
                contentType.isCollection()?REPEATED_VALUE:NOT_REPEATED,
                contentType.idUse(),
                contentType.getExpectedMimeType(),
                source,null,getLocator(),true);
        this.property.setAdapter(contentType.getAdapterUse());
        BIInlineBinaryData.handle(source,property);
        property.getTypes().add(new CTypeRef(contentType.getInfo(),tagName,CTypeRef.getSimpleTypeName(source), true,defaultValue));
        this.type = NavigatorImpl.createParameterizedType(
            NavigatorImpl.theInstance.ref(JAXBElement.class),
            getContentInMemoryType() );

        BIFactoryMethod factoryMethod = Ring.get(BGMBuilder.class).getBindInfo(source).get(BIFactoryMethod.class);
        if(factoryMethod!=null) {
            factoryMethod.markAsAcknowledged();
            this.squeezedName = factoryMethod.name;
        }

    }

    public final String getDefaultValue() {
        return getProperty().getTypes().get(0).getDefaultValue();
    }

    public final JPackage _package() {
        return parent.getOwnerPackage();
    }

    public CNonElement getContentType() {
        return getProperty().ref().get(0);
    }

    public NType getContentInMemoryType() {
        if(getProperty().getAdapter()==null) {
            NType itemType = getContentType().getType();
            if(!property.isCollection())
                return itemType;

            return NavigatorImpl.createParameterizedType(List.class,itemType);
        } else {
            return getProperty().getAdapter().customType;
        }
    }

    public CElementPropertyInfo getProperty() {
        return property;
    }

    public CClassInfo getScope() {
        if(parent instanceof CClassInfo)
            return (CClassInfo)parent;
        return null;
    }

    /**
     * @deprecated why are you calling a method that returns this?
     */
    public NType getType() {
        return this;
    }

    public QName getElementName() {
        return tagName;
    }

    public JType toType(Outline o, Aspect aspect) {
        if(className==null)
            return type.toType(o,aspect);
        else
            return o.getElement(this).implClass;
    }

    /**
     * Returns the "squeezed name" of this element.
     *
     * @see CClassInfo#getSqueezedName()
     */
    @XmlElement
    public String getSqueezedName() {
        if(squeezedName!=null)  return squeezedName;
        
        StringBuilder b = new StringBuilder();
        CClassInfo s = getScope();
        if(s!=null)
            b.append(s.getSqueezedName());
        if(className!=null)
            b.append(className);
        else
            b.append( model.getNameConverter().toClassName(tagName.getLocalPart()));
        return b.toString();
    }

    public CElementInfo getSubstitutionHead() {
        return substitutionHead;
    }

    public Collection<CElementInfo> getSubstitutionMembers() {
        if(substitutionMembers==null)
            return Collections.emptyList();
        else
            return substitutionMembers;
    }

    public void setSubstitutionHead(CElementInfo substitutionHead) {
        // don't set it twice
        assert this.substitutionHead==null;
        assert substitutionHead!=null;
        this.substitutionHead = substitutionHead;

        if(substitutionHead.substitutionMembers==null)
            substitutionHead.substitutionMembers = new HashSet<CElementInfo>();
        substitutionHead.substitutionMembers.add(this);
    }

    public boolean isBoxedType() {
        return false;
    }

    public String fullName() {
        if(className==null)
            return type.fullName();
        else {
            String r = parent.fullName();
            if(r.length()==0)   return className;
            else                return r+'.'+className;
        }
    }

    public <T> T accept(Visitor<T> visitor) {
        return visitor.onElement(this);
    }

    public JPackage getOwnerPackage() {
        return parent.getOwnerPackage();
    }

    public String shortName() {
        return className;
    }

    /**
     * True if this element has its own class
     * (as opposed to be represented as an instance of {@link JAXBElement}.
     */
    public boolean hasClass() {
        return className!=null;
    }
}
