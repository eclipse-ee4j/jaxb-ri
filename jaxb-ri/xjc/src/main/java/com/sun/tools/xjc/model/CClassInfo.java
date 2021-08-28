/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.istack.Nullable;
import com.sun.tools.xjc.Language;
import com.sun.tools.xjc.model.nav.NClass;
import com.sun.tools.xjc.model.nav.NType;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIFactoryMethod;
import org.glassfish.jaxb.core.v2.model.core.ClassInfo;
import org.glassfish.jaxb.core.v2.model.core.Element;
import com.sun.xml.xsom.ForeignAttributes;
import com.sun.xml.xsom.XSComponent;

import org.xml.sax.Locator;

/**
 * Mutable {@link ClassInfo} representation.
 *
 * <p>
 * Schema parsers build these objects.
 *
 * @author Kohsuke Kawaguchi
 */
public final class CClassInfo extends AbstractCElement implements ClassInfo<NType,NClass>, CClassInfoParent, CClass, NClass {

    @XmlIDREF
    private CClass baseClass;

    /**
     * List of all subclasses, together with {@link #nextSibling}.
     *
     * If this class has no sub-class, this field is null. Otherwise,
     * this field points to a sub-class of this class. From there you can enumerate
     * all the sub-classes by using {@link #nextSibling}.
     */
    private CClassInfo firstSubclass;

    /**
     * @see #firstSubclass
     */
    private CClassInfo nextSibling = null;

    /**
     * @see #getTypeName()
     */
    private final QName typeName;

    /**
     * Custom {@link #getSqueezedName() squeezed name}, if any.
     */
    private /*almost final*/ @Nullable String squeezedName;
    
    /**
     * If this class also gets {@link XmlRootElement}, the class name.
     */
    private final @Nullable QName elementName;

    private boolean isOrdered = true;

    private final List<CPropertyInfo> properties = new ArrayList<>();

    /**
     * TODO: revisit this design.
     * we should at least do a basic encapsulation to avoid careless
     * mistakes. Maybe we should even differ the javadoc generation
     * by queueing runners.
     */
    public String javadoc;

    @XmlIDREF
    private final CClassInfoParent parent;

    /**
     * short name.
     */
    public final String shortName;

    /**
     * Optional user-specified implementation override class.
     */
    private @Nullable String implClass;

    /**
     * The {@link Model} object to which this bean belongs.
     */
    public final Model model;

    /**
     * @see #hasAttributeWildcard()
     */
    private boolean hasAttributeWildcard;


    public CClassInfo(Model model,JPackage pkg, String shortName, Locator location, QName typeName, QName elementName, XSComponent source, CCustomizations customizations) {
        this(model,model.getPackage(pkg),shortName,location,typeName,elementName,source,customizations);
    }

    public CClassInfo(Model model,CClassInfoParent p, String shortName, Locator location, QName typeName, QName elementName, XSComponent source, CCustomizations customizations) {
        super(model,source,location,customizations);
        this.model = model;
        this.parent = p;
        this.shortName = model.allocator.assignClassName(parent,shortName);
        this.typeName = typeName;
        this.elementName = elementName;

        Language schemaLanguage = model.options.getSchemaLanguage();
        if ((schemaLanguage != null) &&
            (schemaLanguage.equals(Language.XMLSCHEMA) || schemaLanguage.equals(Language.WSDL))) {
            BIFactoryMethod factoryMethod = Ring.get(BGMBuilder.class).getBindInfo(source).get(BIFactoryMethod.class);
            if(factoryMethod!=null) {
                factoryMethod.markAsAcknowledged();
                this.squeezedName = factoryMethod.name;
            }
        }
        
        model.add(this);
    }

    public CClassInfo(Model model,JCodeModel cm, String fullName, Locator location, QName typeName, QName elementName, XSComponent source, CCustomizations customizations) {
        super(model,source,location,customizations);
        this.model = model;
        int idx = fullName.lastIndexOf('.');
        if(idx<0) {
            this.parent = model.getPackage(cm.rootPackage());
            this.shortName = model.allocator.assignClassName(parent,fullName);
        } else {
            this.parent = model.getPackage(cm._package(fullName.substring(0,idx)));
            this.shortName = model.allocator.assignClassName(parent,fullName.substring(idx+1));
        }
        this.typeName = typeName;
        this.elementName = elementName;

        model.add(this);
    }

    @Override
    public boolean hasAttributeWildcard() {
        return hasAttributeWildcard;
    }

    public void hasAttributeWildcard(boolean hasAttributeWildcard) {
        this.hasAttributeWildcard = hasAttributeWildcard;
    }

    @Override
    public boolean hasSubClasses() {
        return firstSubclass!=null;
    }

    /**
     * Returns true if a new attribute wildcard property needs to be
     * declared on this class.
     */
    @Override
    public boolean declaresAttributeWildcard() {
        return hasAttributeWildcard && !inheritsAttributeWildcard();
    }

    /**
     * Returns true if this class inherits a wildcard attribute property
     * from its ancestor classes.
     */
    @Override
    public boolean inheritsAttributeWildcard() {
        if (getRefBaseClass() != null) {
            CClassRef cref = (CClassRef)baseClass;

            for(ForeignAttributes foreignAttributes: cref.getSchemaComponent().getForeignAttributes()) {
                if(foreignAttributes.getLength() > 0) {
                    return true;
                }
            }
        } else {
            for( CClassInfo c=getBaseClass(); c!=null; c=c.getBaseClass() ) {
                if(c.hasAttributeWildcard)
                    return true;
            }
        }
        return false;
    }


    @Override
    public NClass getClazz() {
        return this;
    }

    @Override
    public CClassInfo getScope() {
        return null;
    }

    @XmlID
    @Override
    public String getName() {
        return fullName();
    }

    /**
     * Returns the "squeezed name" of this bean token.
     * <p>
     * The squeezed name of a bean is the concatenation of
     * the names of its outer classes and itself.
     * <p>
     * Thus if the bean is "org.acme.foo.Bean", then the squeezed name is "Bean",
     * if the bean is "org.acme.foo.Outer1.Outer2.Bean", then "Outer1Outer2Bean".
     * <p>
     * This is used by the code generator
     */
    @XmlElement
    public String getSqueezedName() {
        if (squeezedName != null)  return squeezedName;
        return calcSqueezedName.onBean(this);
    }

    private static final CClassInfoParent.Visitor<String> calcSqueezedName = new Visitor<String>() {
        @Override
        public String onBean(CClassInfo bean) {
            return bean.parent.accept(this) + bean.shortName;
        }

        @Override
        public String onElement(CElementInfo element) {
            return element.parent.accept(this) + element.shortName();
        }

        @Override
        public String onPackage(JPackage pkg) {
            return "";
        }
    };

    /**
     * Returns a mutable list.
     */
    @Override
    public List<CPropertyInfo> getProperties() {
        return properties;
    }

    @Override
    public boolean hasValueProperty() {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a propery by name.
     */
    @Override
    public CPropertyInfo getProperty(String name) {
        // TODO: does this method need to be fast?
        for( CPropertyInfo p : properties )
            if(p.getName(false).equals(name))
                return p;
        return null;
    }

    @Override
    public boolean hasProperties() {
        return !getProperties().isEmpty();
    }

    @Override
    public boolean isElement() {
        return elementName!=null;
    }

    /**
     * Guaranteed to return this.
     */
    @Deprecated
    @Override
    public CNonElement getInfo() {
        return this;
    }
    
    @Override
    public Element<NType,NClass> asElement() {
        if(isElement())
            return this;
        else
            return null;
    }

    @Override
    public boolean isOrdered() {
        return isOrdered;
    }

    /**
     * @deprecated
     *      if you are calling this method directly, you must be doing something wrong.
     */
    @Deprecated
    @Override
    public boolean isFinal() {
        return false;
    }

    public void setOrdered(boolean value) {
        isOrdered = value;
    }

    @Override
    public QName getElementName() {
        return elementName;
    }

    @Override
    public QName getTypeName() {
        return typeName;
    }

    @Override
    public boolean isSimpleType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the FQCN of this bean.
     */
    @Override
    public String fullName() {
        String r = parent.fullName();
        if(r.length()==0)   return shortName;
        else                return r+'.'+shortName;
    }

    public CClassInfoParent parent() {
        return parent;
    }

    public void setUserSpecifiedImplClass(String implClass) {
        assert this.implClass==null;
        assert implClass!=null;
        this.implClass = implClass;
    }

    public String getUserSpecifiedImplClass() {
        return implClass;
    }


    /**
     * Adds a new property.
     */
    public void addProperty(CPropertyInfo prop) {
        if(prop.ref().isEmpty())
            // this property isn't contributing anything
            // this happens when you try to map an empty sequence to a property
            return;
        prop.setParent(this);
        properties.add(prop);
    }

    /**
     * This method accepts both {@link CClassInfo} (which means the base class
     * is also generated), or {@link CClassRef} (which means the base class is
     * already generated and simply referenced.)
     *
     * The latter is treated somewhat special --- from the rest of the model
     * this external base class is invisible. This modeling might need more
     * thoughts to get right.
     */
    public void setBaseClass(CClass base) {
        assert baseClass==null;
        assert base!=null;
        baseClass = base;

        assert nextSibling==null;
        if (base instanceof CClassInfo) {
            CClassInfo realBase = (CClassInfo) base;
            this.nextSibling = realBase.firstSubclass;
            realBase.firstSubclass = this;
        }
    }

    /**
     * This inherited version returns null if this class extends from {@link CClassRef}.
     *
     * @see #getRefBaseClass()
     */
    @Override
    public CClassInfo getBaseClass() {
        if (baseClass instanceof CClassInfo) {
            return (CClassInfo) baseClass;
        } else {
            return null;
        }
    }
    
    public CClassRef getRefBaseClass() {
        if (baseClass instanceof CClassRef) {
            return (CClassRef) baseClass;
        } else {
            return null;
        }
    }

    /**
     * Enumerates all the sub-classes of this class.
     */
    public Iterator<CClassInfo> listSubclasses() {
        return new Iterator<CClassInfo>() {
            CClassInfo cur = firstSubclass;

            @Override
            public boolean hasNext() {
                return cur != null;
            }

            @Override
            public CClassInfo next() {
                CClassInfo r = cur;
                cur = cur.nextSibling;
                return r;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public CClassInfo getSubstitutionHead() {
        CClassInfo c=getBaseClass();
        while(c!=null && !c.isElement())
            c=c.getBaseClass();
        return c;
    }


    /**
     * Interfaces to be implemented.
     * Lazily constructed.
     */
    private Set<JClass> _implements = null;

    public void _implements(JClass c) {
        if(_implements==null)
            _implements = new HashSet<>();
        _implements.add(c);
    }


    /** Constructor declarations. array of {@link Constructor}s. */
    private final List<Constructor> constructors = new ArrayList<>(1);

    /** Creates a new constructor declaration and adds it. */
    public void addConstructor( String... fieldNames ) {
        constructors.add(new Constructor(fieldNames));
    }

    /** list all constructor declarations. */
    public Collection<? extends Constructor> getConstructors() {
        return constructors;
    }

    @Override
    public final <T> T accept(Visitor<T> visitor) {
        return visitor.onBean(this);
    }

    @Override
    public JPackage getOwnerPackage() {
        return parent.getOwnerPackage();
    }

    @Override
    public final NClass getType() {
        return this;
    }

    @Override
    public final JClass toType(Outline o, Aspect aspect) {
        switch(aspect) {
        case IMPLEMENTATION:
            return o.getClazz(this).implRef;
        case EXPOSED:
            return o.getClazz(this).ref;
        default:
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isBoxedType() {
        return false;
    }

    @Override
    public String toString() {
        return fullName();
    }
}
