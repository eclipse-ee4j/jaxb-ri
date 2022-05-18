/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.model.impl;

import com.sun.istack.FinalArrayList;
import jakarta.xml.bind.annotation.XmlAccessOrder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorOrder;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttachmentRef;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlInlineBinaryData;
import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.XmlMimeType;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import org.glassfish.jaxb.core.annotation.OverrideAnnotationOf;
import org.glassfish.jaxb.core.v2.model.annotation.Locatable;
import org.glassfish.jaxb.core.v2.model.core.AttributePropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.ClassInfo;
import org.glassfish.jaxb.core.v2.model.core.Element;
import org.glassfish.jaxb.core.v2.model.core.ElementPropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.ID;
import org.glassfish.jaxb.core.v2.model.core.MapPropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.NonElement;
import org.glassfish.jaxb.core.v2.model.core.PropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.PropertyKind;
import org.glassfish.jaxb.core.v2.model.core.ReferencePropertyInfo;
import org.glassfish.jaxb.core.v2.model.core.ValuePropertyInfo;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;
import org.glassfish.jaxb.core.v2.runtime.Location;
import org.glassfish.jaxb.core.v2.util.EditDistance;
import org.glassfish.jaxb.runtime.v2.model.annotation.MethodLocatable;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


/**
 * A part of the {@link ClassInfo} that doesn't depend on a particular
 * reflection library.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class ClassInfoImpl<T,C,F,M> extends TypeInfoImpl<T,C,F,M>
    implements ClassInfo<T,C>, Element<T,C> {

    protected final C clazz;

    /**
     * @see #getElementName()
     */
    private final QName elementName;

    /**
     * @see #getTypeName()
     */
    private final QName typeName;

    /**
     * Lazily created.
     *
     * @see #getProperties()
     */
    private FinalArrayList<PropertyInfoImpl<T,C,F,M>> properties;

    /**
     * The property order.
     *
     * null if unordered. {@link #DEFAULT_ORDER} if ordered but the order is defaulted
     *
     * @see #isOrdered()
     */
    private /*final*/ String[] propOrder;

    /**
     * Lazily computed.
     *
     * To avoid the cyclic references of the form C1 --base--{@literal >} C2 --property--{@literal >} C1.
     */
    private ClassInfoImpl<T,C,F,M> baseClass;

    private boolean baseClassComputed = false;

    private boolean hasSubClasses = false;

    /**
     * If this class has a declared (not inherited) attribute wildcard,  keep the reference
     * to it.
     *
     * This parameter is initialized at the construction time and never change.
     */
    protected /*final*/ PropertySeed<T,C,F,M> attributeWildcard;


    /**
     * @see #getFactoryMethod()
     */
    private M factoryMethod = null;
    
    ClassInfoImpl(ModelBuilder<T,C,F,M> builder, Locatable upstream, C clazz) {
        super(builder,upstream);
        this.clazz = clazz;
        assert clazz!=null;

        // compute the element name
        elementName = parseElementName(clazz);

        // compute the type name
        XmlType t = reader().getClassAnnotation(XmlType.class,clazz,this);
        typeName = parseTypeName(clazz,t);

        if(t!=null) {
            String[] propOrder = t.propOrder();
            if(propOrder.length==0)
                this.propOrder = null;   // unordered
            else {
                if(propOrder[0].length()==0)
                    this.propOrder = DEFAULT_ORDER;
                else
                    this.propOrder = propOrder;
            }
        } else {
            propOrder = DEFAULT_ORDER;
        }

        // obtain XmlAccessorOrder and  set proporder (XmlAccessorOrder can be defined for whole package)
        // (<xs:all> vs <xs:sequence>)
        XmlAccessorOrder xao = reader().getPackageAnnotation(XmlAccessorOrder.class, clazz, this);
        if((xao != null) && (xao.value() == XmlAccessOrder.UNDEFINED)) {
            propOrder = null;
        }

        // obtain XmlAccessorOrder and  set proporder (<xs:all> vs <xs:sequence>)
        xao = reader().getClassAnnotation(XmlAccessorOrder.class, clazz, this);
        if((xao != null) && (xao.value() == XmlAccessOrder.UNDEFINED)) {
            propOrder = null;
        }

        if(nav().isInterface(clazz)) {
            builder.reportError(new IllegalAnnotationException(
                Messages.CANT_HANDLE_INTERFACE.format(nav().getClassName(clazz)), this ));
        }

        // the class must have the default constructor
        if (!hasFactoryConstructor(t)){
            if(!nav().hasDefaultConstructor(clazz)){
                if(nav().isInnerClass(clazz)) {
                    builder.reportError(new IllegalAnnotationException(
                        Messages.CANT_HANDLE_INNER_CLASS.format(nav().getClassName(clazz)), this ));
                } else if (elementName != null) {
                    builder.reportError(new IllegalAnnotationException(
                        Messages.NO_DEFAULT_CONSTRUCTOR.format(nav().getClassName(clazz)), this ));
                }
            }
        }
    }        

    @Override
    @SuppressWarnings({"unchecked"})
    public ClassInfoImpl<T,C,F,M> getBaseClass() {
        if (!baseClassComputed) {
            // compute the base class
            C s = nav().getSuperClass(clazz);
            if(s==null || s==nav().asDecl(Object.class)) {
                baseClass = null;
            } else {
                NonElement<T,C> b = builder.getClassInfo(s, true, this);
                if(b instanceof ClassInfoImpl) {
                    baseClass = (ClassInfoImpl<T,C,F,M>) b;
                    baseClass.hasSubClasses = true;
                } else {
                    baseClass = null;
                }
            }
            baseClassComputed = true;
        }
        return baseClass;
    }

    /**
     * {@inheritDoc}
     *
     * The substitution hierarchy is the same as the inheritance hierarchy.
     */
    @Override
    public final Element<T,C> getSubstitutionHead() {
        ClassInfoImpl<T,C,F,M> c = getBaseClass();
        while(c!=null && !c.isElement())
            c = c.getBaseClass();
        return c;
    }

    @Override
    public final C getClazz() {
        return clazz;
    }

    /**
     * When a bean binds to an element, it's always through {@link XmlRootElement},
     * so this method always return null.
     *
     * @deprecated
     *      you shouldn't be invoking this method on .
     */
    @Override
    @Deprecated
    public ClassInfoImpl<T,C,F,M> getScope() {
        return null;
    }

    @Override
    public final T getType() {
        return nav().use(clazz);
    }

    /**
     * A {@link ClassInfo} can be referenced by {@link XmlIDREF} if
     * it has an ID property.
     */
    @Override
    public boolean canBeReferencedByIDREF() {
        for (PropertyInfo<T,C> p : getProperties()) {
            if(p.id()== ID.ID)
                return true;
        }
        ClassInfoImpl<T,C,F,M> base = getBaseClass();
        if(base!=null)
            return base.canBeReferencedByIDREF();
        else
            return false;
    }

    @Override
    public final String getName() {
        return nav().getClassName(clazz);
    }

    public <A extends Annotation> A readAnnotation(Class<A> a) {
        return reader().getClassAnnotation(a,clazz,this);
    }

    @Override
    public Element<T,C> asElement() {
        if(isElement())
            return this;
        else
            return null;
    }

    @Override
    public List<? extends PropertyInfo<T,C>> getProperties() {
        if(properties!=null)    return properties;

        // check the access type first
        XmlAccessType at = getAccessType();

        properties = new FinalArrayList<>();

        findFieldProperties(clazz,at);

        findGetterSetterProperties(at);

        if(propOrder==DEFAULT_ORDER || propOrder==null) {
            XmlAccessOrder ao = getAccessorOrder();
            if(ao==XmlAccessOrder.ALPHABETICAL)
                Collections.sort(properties);
        } else {
            //sort them as specified
            PropertySorter sorter = new PropertySorter();
            for (PropertyInfoImpl p : properties) {
                sorter.checkedGet(p);   // have it check for errors
            }
            Collections.sort(properties,sorter);
            sorter.checkUnusedProperties();
        }

        {// additional error checks
            PropertyInfoImpl vp=null; // existing value property
            PropertyInfoImpl ep=null; // existing element property

            for (PropertyInfoImpl p : properties) {
                switch(p.kind()) {
                case ELEMENT:
                case REFERENCE:
                case MAP:
                    ep = p;
                    break;
                case VALUE:
                    if(vp!=null) {
                        // can't have multiple value properties.
                        builder.reportError(new IllegalAnnotationException(
                            Messages.MULTIPLE_VALUE_PROPERTY.format(),
                            vp, p ));
                    }
                    if(getBaseClass()!=null) {
                        builder.reportError(new IllegalAnnotationException(
                            Messages.XMLVALUE_IN_DERIVED_TYPE.format(), p ));
                    }
                    vp = p;
                    break;
                case ATTRIBUTE:
                    break;  // noop
                default:
                    assert false;
                }
            }

            if(ep!=null && vp!=null) {
                // can't have element and value property at the same time
                builder.reportError(new IllegalAnnotationException(
                    Messages.ELEMENT_AND_VALUE_PROPERTY.format(),
                    vp, ep
                ));
            }
        }

        return properties;
    }

    private void findFieldProperties(C c, XmlAccessType at) {

        // always find properties from the super class first
        C sc = nav().getSuperClass(c);
        if (shouldRecurseSuperClass(sc)) {
            findFieldProperties(sc,at);
        }

        for( F f : nav().getDeclaredFields(c) ) {
            Annotation[] annotations = reader().getAllFieldAnnotations(f,this);
            boolean isDummy = reader().hasFieldAnnotation(OverrideAnnotationOf.class, f);

            if( nav().isTransient(f) ) {
                // it's an error for transient field to have any binding annotation
                if(hasJAXBAnnotation(annotations))
                    builder.reportError(new IllegalAnnotationException(
                        Messages.TRANSIENT_FIELD_NOT_BINDABLE.format(nav().getFieldName(f)),
                            getSomeJAXBAnnotation(annotations)));
            } else
            if( nav().isStaticField(f) ) {
                // static fields are bound only when there's explicit annotation.
                if(hasJAXBAnnotation(annotations))
                    addProperty(createFieldSeed(f),annotations, false);
            } else {
                if(at==XmlAccessType.FIELD
                ||(at==XmlAccessType.PUBLIC_MEMBER && nav().isPublicField(f))
                || hasJAXBAnnotation(annotations)) {
                    if (isDummy) {
                        ClassInfo<T, C> top = getBaseClass();
                        while ((top != null) && (top.getProperty("content") == null)) {
                            top = top.getBaseClass();
                        }
                        DummyPropertyInfo prop = (DummyPropertyInfo) top.getProperty("content");
                        PropertySeed seed = createFieldSeed(f);
                        prop.addType(createReferenceProperty(seed));
                    } else {
                        addProperty(createFieldSeed(f), annotations, false);
                    }
                }
                checkFieldXmlLocation(f);
            }
        }
    }

    @Override
    public final boolean hasValueProperty() {
        ClassInfoImpl<T, C, F, M> bc = getBaseClass();
        if(bc!=null && bc.hasValueProperty())
            return true;

        for (PropertyInfo p : getProperties()) {
            if (p instanceof ValuePropertyInfo) return true;
        }

        return false;
        }

    @Override
    public PropertyInfo<T,C> getProperty(String name) {
        for( PropertyInfo<T,C> p: getProperties() ) {
            if(p.getName().equals(name))
                return p;
        }
        return null;
    }

    /**
     * This hook is used by {@link RuntimeClassInfoImpl} to look for {@link org.glassfish.jaxb.core.annotation.XmlLocation}.
     */
    protected void checkFieldXmlLocation(F f) {
    }

    /**
     * Gets an annotation that are allowed on both class and type.
     */
    private <T extends Annotation> T getClassOrPackageAnnotation(Class<T> type) {
        T t = reader().getClassAnnotation(type,clazz,this);
        if(t!=null)
            return t;
        // defaults to the package level
        return reader().getPackageAnnotation(type,clazz,this);
    }

    /**
     * Computes the {@link XmlAccessType} on this class by looking at {@link XmlAccessorType}
     * annotations.
     */
    private XmlAccessType getAccessType() {
        XmlAccessorType xat = getClassOrPackageAnnotation(XmlAccessorType.class);
        if(xat!=null)
            return xat.value();
        else
            return XmlAccessType.PUBLIC_MEMBER;
    }

    /**
     * Gets the accessor order for this class by consulting {@link XmlAccessorOrder}.
     */
    private XmlAccessOrder getAccessorOrder() {
        XmlAccessorOrder xao = getClassOrPackageAnnotation(XmlAccessorOrder.class);
        if(xao!=null)
            return xao.value();
        else
            return XmlAccessOrder.UNDEFINED;
    }

    /**
     * Compares orders among {@link PropertyInfoImpl} according to {@link ClassInfoImpl#propOrder}.
     *
     * <p>
     * extends {@link HashMap} to save memory.
     */
    private final class PropertySorter extends HashMap<String,Integer> implements Comparator<PropertyInfoImpl> {
        private static final long serialVersionUID = 8074459976041391290L;
        /**
         * Mark property names that are used, so that we can report unused property names in the propOrder array.
         */
        PropertyInfoImpl[] used = new PropertyInfoImpl[propOrder.length];

        /**
         * If any name collides, it will be added to this set.
         * This is used to avoid repeating the same error message.
         */
        private Set<String> collidedNames;

        PropertySorter() {
            super(propOrder.length);
            for( String name : propOrder )
                if(put(name,size())!=null) {
                    // two properties with the same name
                    builder.reportError(new IllegalAnnotationException(
                        Messages.DUPLICATE_ENTRY_IN_PROP_ORDER.format(name),ClassInfoImpl.this));
                }
        }

        @Override
        public int compare(PropertyInfoImpl o1, PropertyInfoImpl o2) {
            int lhs = checkedGet(o1);
            int rhs = checkedGet(o2);

            return lhs-rhs;
        }

        private int checkedGet(PropertyInfoImpl p) {
            Integer i = get(p.getName());
            if(i==null) {
                // missing
                if (p.kind().isOrdered)
                    builder.reportError(new IllegalAnnotationException(
                        Messages.PROPERTY_MISSING_FROM_ORDER.format(p.getName()),p));

                // give it an order to recover from an error
                i = size();
                put(p.getName(),i);
            }

            // mark the used field
            int ii = i;
            if(ii<used.length) {
                if(used[ii]!=null && used[ii]!=p) {
                    if(collidedNames==null) collidedNames = new HashSet<>();

                    if(collidedNames.add(p.getName()))
                        // report the error only on the first time
                        builder.reportError(new IllegalAnnotationException(
                            Messages.DUPLICATE_PROPERTIES.format(p.getName()),p,used[ii]));
                }
                used[ii] = p;
            }

            return i;
        }

        /**
         * Report errors for unused propOrder entries.
         */
        public void checkUnusedProperties() {
            for( int i=0; i<used.length; i++ )
                if(used[i]==null) {
                    String unusedName = propOrder[i];
                    String nearest = EditDistance.findNearest(unusedName, new AbstractList<>() {
                        @Override
                        public String get(int index) {
                            return properties.get(index).getName();
                        }

                        @Override
                        public int size() {
                            return properties.size();
                        }
                    });
                    boolean isOverriding = i <= (properties.size() - 1) && properties.get(i).hasAnnotation(OverrideAnnotationOf.class);
                    if (!isOverriding) {
                        builder.reportError(new IllegalAnnotationException(
                        Messages.PROPERTY_ORDER_CONTAINS_UNUSED_ENTRY.format(unusedName,nearest),ClassInfoImpl.this));
                    }
                }
        }
    }

    @Override
    public boolean hasProperties() {
        return !properties.isEmpty();
    }


    /**
     * Picks the first non-null argument, or null if all arguments are null.
     */
    @SafeVarargs
    private static <T> T pickOne(T... args ) {
        for( T arg : args )
            if(arg!=null)
                return arg;
        return null;
    }

    @SafeVarargs
    private static <T> List<T> makeSet(T... args ) {
        List<T> l = new FinalArrayList<>();
        for( T arg : args )
            if(arg!=null)   l.add(arg);
        return l;
    }

    private static final class ConflictException extends Exception {
        private static final long serialVersionUID = -8261248191127673032L;
        final List<Annotation> annotations;

        public ConflictException(List<Annotation> one) {
            this.annotations = one;
        }
    }

    private static final class DuplicateException extends Exception {
        private static final long serialVersionUID = -2996855754364938240L;
        final Annotation a1,a2;
        public DuplicateException(Annotation a1, Annotation a2) {
            this.a1 = a1;
            this.a2 = a2;
        }
    }

    /**
     * Represents 6 groups of secondary annotations
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private enum SecondaryAnnotation {
        JAVA_TYPE       (0x01, new Class[] {XmlJavaTypeAdapter.class}),
        ID_IDREF        (0x02, new Class[] {XmlID.class, XmlIDREF.class}),
        BINARY          (0x04, new Class[] {XmlInlineBinaryData.class, XmlMimeType.class, XmlAttachmentRef.class}),
        ELEMENT_WRAPPER (0x08, new Class[] {XmlElementWrapper.class}),
        LIST            (0x10, new Class[] {XmlList.class}),
        SCHEMA_TYPE     (0x20, new Class[] {XmlSchemaType.class});

        /**
         * Each constant gets an unique bit mask so that the presence/absence
         * of them can be represented in a single byte.
         */
        final int bitMask;
        /**
         * List of annotations that belong to this member.
         */
        final Class<? extends Annotation>[] members;

        SecondaryAnnotation(int bitMask, Class<? extends Annotation>[] members) {
            this.bitMask = bitMask;
            this.members = members;
        }
    }

    private static final SecondaryAnnotation[] SECONDARY_ANNOTATIONS = SecondaryAnnotation.values();

    /**
     * Represents 7 groups of properties.
     *
     * Each instance is also responsible for rejecting annotations
     * that are not allowed on that kind.
     */
    private enum PropertyGroup {
        TRANSIENT       (false,false,false,false,false,false),
        ANY_ATTRIBUTE   (true, false,false,false,false,false),
        ATTRIBUTE       (true, true, true, false,true, true ),
        VALUE           (true, true, true, false,true, true ),
        ELEMENT         (true, true, true, true, true, true ),
        ELEMENT_REF     (true, false,false,true, false,false),
        MAP             (false,false,false,true, false,false);

        /**
         * Bit mask that represents secondary annotations that are allowed on this group.
         *
         * T = not allowed, F = allowed
         */
        final int allowedsecondaryAnnotations;

        PropertyGroup(boolean... bits) {
            int mask = 0;
            assert bits.length==SECONDARY_ANNOTATIONS.length;
            for( int i=0; i<bits.length; i++ ) {
                if(bits[i])
                    mask |= SECONDARY_ANNOTATIONS[i].bitMask;
            }
            allowedsecondaryAnnotations = ~mask;
        }

        boolean allows(SecondaryAnnotation a) {
            return (allowedsecondaryAnnotations&a.bitMask)==0;
        }
    }

    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    /**
     * All the annotations in JAXB to their internal index.
     */
    private static final HashMap<Class,Integer> ANNOTATION_NUMBER_MAP = new HashMap<>();
    static {
        Class[] annotations = {
            XmlTransient.class,     // 0
            XmlAnyAttribute.class,  // 1
            XmlAttribute.class,     // 2
            XmlValue.class,         // 3
            XmlElement.class,       // 4
            XmlElements.class,      // 5
            XmlElementRef.class,    // 6
            XmlElementRefs.class,   // 7
            XmlAnyElement.class,    // 8
            XmlMixed.class,         // 9
            OverrideAnnotationOf.class,// 10
        };

        HashMap<Class,Integer> m = ANNOTATION_NUMBER_MAP;

        // characterizing annotations
        for( Class c : annotations )
            m.put(c, m.size() );

        // secondary annotations
        int index = 20;
        for( SecondaryAnnotation sa : SECONDARY_ANNOTATIONS ) {
            for( Class member : sa.members )
                m.put(member,index);
            index++;
        }
    }

    private void checkConflict(Annotation a, Annotation b) throws DuplicateException {
        assert b!=null;
        if(a!=null)
            throw new DuplicateException(a,b);
    }

    /**
     * Called only from {@link #getProperties()}.
     *
     * <p>
     * This is where we decide the type of the property and checks for annotations
     * that are not allowed.
     *
     * @param annotations
     *      all annotations on this property. It's the same as
     *      {@code seed.readAllAnnotation()}, but taken as a parameter
     *      because the caller should know it already.
     */
    @SuppressWarnings({"unchecked"})
    private void addProperty( PropertySeed<T,C,F,M> seed, Annotation[] annotations, boolean dummy ) {
        // since typically there's a very few annotations on a method,
        // this runs faster than checking for each annotation via readAnnotation(A)


        // characterizing annotations. these annotations (or lack thereof) decides
        // the kind of the property it goes to.
        // I wish I could use an array...
        XmlTransient t = null;
        XmlAnyAttribute aa = null;
        XmlAttribute a = null;
        XmlValue v = null;
        XmlElement e1 = null;
        XmlElements e2 = null;
        XmlElementRef r1 = null;
        XmlElementRefs r2 = null;
        XmlAnyElement xae = null;
        XmlMixed mx = null;
        OverrideAnnotationOf ov = null;

        // encountered secondary annotations are accumulated into a bit mask
        int secondaryAnnotations = 0;

        try {
            for( Annotation ann : annotations ) {
                Integer index = ANNOTATION_NUMBER_MAP.get(ann.annotationType());
                if(index==null) continue;
                switch(index) {
                case 0:     checkConflict(t  ,ann); t   = (XmlTransient) ann; break;
                case 1:     checkConflict(aa ,ann); aa  = (XmlAnyAttribute) ann; break;
                case 2:     checkConflict(a  ,ann); a   = (XmlAttribute) ann; break;
                case 3:     checkConflict(v  ,ann); v   = (XmlValue) ann; break;
                case 4:     checkConflict(e1 ,ann); e1  = (XmlElement) ann; break;
                case 5:     checkConflict(e2 ,ann); e2  = (XmlElements) ann; break;
                case 6:     checkConflict(r1 ,ann); r1  = (XmlElementRef) ann; break;
                case 7:     checkConflict(r2 ,ann); r2  = (XmlElementRefs) ann; break;
                case 8:     checkConflict(xae,ann); xae = (XmlAnyElement) ann; break;
                case 9:     checkConflict(mx, ann); mx  = (XmlMixed) ann; break;
                case 10:    checkConflict(ov, ann); ov  = (OverrideAnnotationOf) ann; break;
                default:
                    // secondary annotations
                    secondaryAnnotations |= (1<<(index-20));
                    break;
                }
            }

            // determine the group kind, and also count the numbers, since
            // characterizing annotations are mutually exclusive.
            PropertyGroup group = null;
            int groupCount = 0;

            if(t!=null) {
                group = PropertyGroup.TRANSIENT;
                groupCount++;
            }
            if(aa!=null) {
                group = PropertyGroup.ANY_ATTRIBUTE;
                groupCount++;
            }
            if(a!=null) {
                group = PropertyGroup.ATTRIBUTE;
                groupCount++;
            }
            if(v!=null) {
                group = PropertyGroup.VALUE;
                groupCount++;
            }
            if(e1!=null || e2!=null) {
                group = PropertyGroup.ELEMENT;
                groupCount++;
            }
            if(r1!=null || r2!=null || xae!=null || mx!=null || ov != null) {
                group = PropertyGroup.ELEMENT_REF;
                groupCount++;
            }

            if(groupCount>1) {
                // collision between groups
                List<Annotation> err = makeSet(t,aa,a,v,pickOne(e1,e2),pickOne(r1,r2,xae));
                throw new ConflictException(err);
            }

            if(group==null) {
                // if no characterizing annotation was found, it's either element or map
                // sniff the signature and then decide.
                assert groupCount==0;

                // UGLY: the presence of XmlJavaTypeAdapter makes it an element property. ARGH.
                if (nav().isSubClassOf(seed.getRawType(), nav().ref(Map.class))
                        && !hasApplicableAdapter(seed, seed.getRawType())) {
                    group = PropertyGroup.MAP;
                } else {
                    group = PropertyGroup.ELEMENT;
                }
            } else if (group.equals(PropertyGroup.ELEMENT)) {
                // see issue 791 - make sure @XmlElement annotated map property is mapped to map
                // see issue 971 - unless it has applicable XmlJavaTypeAdapter
                if (nav().isSubClassOf( seed.getRawType(), nav().ref(Map.class))
                        && !hasApplicableAdapter(seed, seed.getRawType())) {
                    group = PropertyGroup.MAP;
                }
            }

            // group determined by now
            // make sure that there are no prohibited secondary annotations
            if( (secondaryAnnotations&group.allowedsecondaryAnnotations)!=0 ) {
                // uh oh. find the offending annotation
                for( SecondaryAnnotation sa : SECONDARY_ANNOTATIONS ) {
                    if(group.allows(sa))
                        continue;
                    for( Class<? extends Annotation> m : sa.members ) {
                        Annotation offender = seed.readAnnotation(m);
                        if(offender!=null) {
                            // found it
                            builder.reportError(new IllegalAnnotationException(
                                Messages.ANNOTATION_NOT_ALLOWED.format(m.getSimpleName()),offender));
                            return;
                        }
                    }
                }
                // there must have been an offender
                assert false;
            }

            // actually create annotations
            switch(group) {
            case TRANSIENT:
                return;
            case ANY_ATTRIBUTE:
                // an attribute wildcard property
                if(attributeWildcard!=null) {
                    builder.reportError(new IllegalAnnotationException(
                        Messages.TWO_ATTRIBUTE_WILDCARDS.format(
                            nav().getClassName(getClazz())),aa,attributeWildcard));
                    return; // recover by ignore
                }
                attributeWildcard = seed;

                if(inheritsAttributeWildcard()) {
                    builder.reportError(new IllegalAnnotationException(
                        Messages.SUPER_CLASS_HAS_WILDCARD.format(),
                            aa,getInheritedAttributeWildcard()));
                    return;
                }

                // check the signature and make sure it's assignable to Map
                if(!nav().isSubClassOf(seed.getRawType(),nav().ref(Map.class))) {
                    builder.reportError(new IllegalAnnotationException(
                        Messages.INVALID_ATTRIBUTE_WILDCARD_TYPE.format(nav().getTypeName(seed.getRawType())),
                            aa,getInheritedAttributeWildcard()));
                    return;
                }


                return;
            case ATTRIBUTE:
                properties.add((PropertyInfoImpl<T, C, F, M>) createAttributeProperty(seed));
                return;
            case VALUE:
                properties.add((PropertyInfoImpl<T, C, F, M>) createValueProperty(seed));
                return;
            case ELEMENT:
                properties.add((PropertyInfoImpl<T, C, F, M>) createElementProperty(seed));
                return;
            case ELEMENT_REF:
                properties.add((PropertyInfoImpl<T, C, F, M>) createReferenceProperty(seed));
                return;
            case MAP:
                properties.add((PropertyInfoImpl<T, C, F, M>) createMapProperty(seed));
                return;
            default:
                assert false;
            }
        } catch( ConflictException x ) {
            // report a conflicting annotation
            List<Annotation> err = x.annotations;

            builder.reportError(new IllegalAnnotationException(
                Messages.MUTUALLY_EXCLUSIVE_ANNOTATIONS.format(
                    nav().getClassName(getClazz())+'#'+seed.getName(),
                    err.get(0).annotationType().getName(), err.get(1).annotationType().getName()),
                    err.get(0), err.get(1) ));

            // recover by ignoring this property
        } catch( DuplicateException e ) {
            // both are present
            builder.reportError(new IllegalAnnotationException(
                Messages.DUPLICATE_ANNOTATIONS.format(e.a1.annotationType().getName()),
                e.a1, e.a2 ));
            // recover by ignoring this property

        }
    }

    protected ReferencePropertyInfo<T,C> createReferenceProperty(PropertySeed<T,C,F,M> seed) {
        return new ReferencePropertyInfoImpl<>(this,seed);
    }

    protected AttributePropertyInfo<T,C> createAttributeProperty(PropertySeed<T,C,F,M> seed) {
        return new AttributePropertyInfoImpl<>(this,seed);
    }

    protected ValuePropertyInfo<T,C> createValueProperty(PropertySeed<T,C,F,M> seed) {
        return new ValuePropertyInfoImpl<>(this,seed);
    }

    protected ElementPropertyInfo<T,C> createElementProperty(PropertySeed<T,C,F,M> seed) {
        return new ElementPropertyInfoImpl<>(this,seed);
    }

    protected MapPropertyInfo<T,C> createMapProperty(PropertySeed<T,C,F,M> seed) {
        return new MapPropertyInfoImpl<>(this,seed);
    }


    /**
     * Adds properties that consists of accessors.
     */
    private void findGetterSetterProperties(XmlAccessType at) {
        // in the first step we accumulate getters and setters
        // into this map keyed by the property name.
        Map<String,M> getters = new LinkedHashMap<>();
        Map<String,M> setters = new LinkedHashMap<>();

        C c = clazz;
        do {
            collectGetterSetters(clazz, getters, setters);

            // take super classes into account if they have @XmlTransient
            c = nav().getSuperClass(c);
        } while(shouldRecurseSuperClass(c));


        // compute the intersection
        Set<String> complete = new TreeSet<>(getters.keySet());
        complete.retainAll(setters.keySet());

        resurrect(getters, complete);
        resurrect(setters, complete);

        // then look for read/write properties.
        for (String name : complete) {
            M getter = getters.get(name);
            M setter = setters.get(name);

            Annotation[] ga = getter!=null ? reader().getAllMethodAnnotations(getter,new MethodLocatable<>(this,getter,nav())) : EMPTY_ANNOTATIONS;
            Annotation[] sa = setter!=null ? reader().getAllMethodAnnotations(setter,new MethodLocatable<>(this,setter,nav())) : EMPTY_ANNOTATIONS;

            boolean hasAnnotation = hasJAXBAnnotation(ga) || hasJAXBAnnotation(sa);
            boolean isOverriding = false;
            if(!hasAnnotation) {
                // checking if the method is overriding others isn't free,
                // so we don't compute it if it's not necessary.
                isOverriding = (getter!=null && nav().isOverriding(getter,c))
                            && (setter!=null && nav().isOverriding(setter,c));
            }

            if((at==XmlAccessType.PROPERTY && !isOverriding)
                || (at==XmlAccessType.PUBLIC_MEMBER && isConsideredPublic(getter) && isConsideredPublic(setter) && !isOverriding)
            || hasAnnotation) {
                // make sure that the type is consistent
                if(getter!=null && setter!=null
                && !nav().isSameType(nav().getReturnType(getter), nav().getMethodParameters(setter)[0])) {
                    // inconsistent
                    builder.reportError(new IllegalAnnotationException(
                        Messages.GETTER_SETTER_INCOMPATIBLE_TYPE.format(
                            nav().getTypeName(nav().getReturnType(getter)),
                            nav().getTypeName(nav().getMethodParameters(setter)[0])
                        ),
                        new MethodLocatable<>( this, getter, nav()),
                        new MethodLocatable<>( this, setter, nav())));
                    continue;
                }

                // merge annotations from two list
                Annotation[] r;
                if(ga.length==0) {
                    r = sa;
                } else
                if(sa.length==0) {
                    r = ga;
                } else {
                    r = new Annotation[ga.length+sa.length];
                    System.arraycopy(ga,0,r,0,ga.length);
                    System.arraycopy(sa,0,r,ga.length,sa.length);
                }

                addProperty(createAccessorSeed(getter, setter), r, false);
            }
        }
        // done with complete pairs
        getters.keySet().removeAll(complete);
        setters.keySet().removeAll(complete);

        // TODO: think about
        // class Foo {
        //   int getFoo();
        // }
        // class Bar extends Foo {
        //   void setFoo(int x);
        // }
        // and how it will be XML-ized.
    }

    private void collectGetterSetters(C c, Map<String,M> getters, Map<String,M> setters) {
        // take super classes into account if they have @XmlTransient.
        // always visit them first so that
        //   1) order is right
        //   2) overriden properties are handled accordingly
        C sc = nav().getSuperClass(c);
        if(shouldRecurseSuperClass(sc))
            collectGetterSetters(sc,getters,setters);

        Collection<? extends M> methods = nav().getDeclaredMethods(c);
        Map<String,List<M>> allSetters = new LinkedHashMap<>();
        for( M method : methods ) {
            boolean used = false;   // if this method is added to getters or setters

            if(nav().isBridgeMethod(method))
                continue;   // ignore

            String name = nav().getMethodName(method);
            int arity = nav().getMethodParameters(method).length;

            if(nav().isStaticMethod(method)) {
                ensureNoAnnotation(method);
                continue;
            }

            // is this a get method?
            String propName = getPropertyNameFromGetMethod(name);
            if(propName!=null && arity==0) {
                    getters.put(propName,method);
                used = true;
            }

            // is this a set method?
            propName = getPropertyNameFromSetMethod(name);
            if(propName!=null && arity==1) {
                List<M> propSetters = allSetters.computeIfAbsent(propName, k -> new ArrayList<>());
                propSetters.add(method);
                used = true; // used check performed later
            }

            if(!used)
                ensureNoAnnotation(method);
        }

        // Match getter with setters by comparing getter return type to setter param
        for (Map.Entry<String,M> entry : getters.entrySet()) {
            String propName = entry.getKey();
            M getter = entry.getValue();
            List<M> propSetters = allSetters.remove(propName);
            if (null == propSetters) {
                //no matching setter
                continue;
            }
            T getterType = nav().getReturnType(getter);
            for (M setter : propSetters) {
                T setterType = nav().getMethodParameters(setter)[0];
                if (nav().isSameType(setterType, getterType)) {
                    setters.put(propName, setter);
                    break;
                }
            }
        }

        // also allow set-only properties
        for (Map.Entry<String,List<M>> e : allSetters.entrySet()) {
            setters.put(e.getKey(),e.getValue().get(0));
        }
    }
    
    /**
     * Checks if the properties in this given super class should be aggregated into this class.
     */
    private boolean shouldRecurseSuperClass(C sc) {
        return sc!=null
            && (builder.isReplaced(sc) || reader().hasClassAnnotation(sc, XmlTransient.class));
    }

    /**
     * Returns true if the method is considered 'public'.
     */
    private boolean isConsideredPublic(M m) {
        return m ==null || nav().isPublicMethod(m);
    }

    /**
     * If the method has an explicit annotation, allow it to participate
     * to the processing even if it lacks the setter or the getter.
     */
    private void resurrect(Map<String, M> methods, Set<String> complete) {
        for (Map.Entry<String, M> e : methods.entrySet()) {
            if(complete.contains(e.getKey()))
                continue;
            if(hasJAXBAnnotation(reader().getAllMethodAnnotations(e.getValue(),this)))
                complete.add(e.getKey());
        }
    }

    /**
     * Makes sure that the method doesn't have any annotation, if it does,
     * report it as an error
     */
    private void ensureNoAnnotation(M method) {
        Annotation[] annotations = reader().getAllMethodAnnotations(method,this);
        for( Annotation a : annotations ) {
            if(isJAXBAnnotation(a)) {
                builder.reportError(new IllegalAnnotationException(
                    Messages.ANNOTATION_ON_WRONG_METHOD.format(),
                    a));
                return;
            }
        }
    }

    /**
     * Returns true if a given annotation is a JAXB annotation.
     */
    private static boolean isJAXBAnnotation(Annotation a) {
        return ANNOTATION_NUMBER_MAP.containsKey(a.annotationType());
    }

    /**
     * Returns true if the array contains a JAXB annotation.
     */
    private static boolean hasJAXBAnnotation(Annotation[] annotations) {
        return getSomeJAXBAnnotation(annotations)!=null;
    }

    private static Annotation getSomeJAXBAnnotation(Annotation[] annotations) {
        for( Annotation a : annotations )
            if(isJAXBAnnotation(a))
                return a;
        return null;
    }


    /**
     * Returns "Foo" from "getFoo" or "isFoo".
     *
     * @return null
     *      if the method name doesn't look like a getter.
     */
    private static String getPropertyNameFromGetMethod(String name) {
        if(name.startsWith("get") && name.length()>3)
            return name.substring(3);
        if(name.startsWith("is") && name.length()>2)
            return name.substring(2);
        return null;
    }

    /**
     * Returns "Foo" from "setFoo".
     *
     * @return null
     *      if the method name doesn't look like a setter.
     */
    private static String getPropertyNameFromSetMethod(String name) {
        if(name.startsWith("set") && name.length()>3)
            return name.substring(3);
        return null;
    }

    /**
     * Creates a new {@link FieldPropertySeed} object.
     *
     * <p>
     * Derived class can override this method to create a sub-class.
     */
    protected PropertySeed<T,C,F,M> createFieldSeed(F f) {
        return new FieldPropertySeed<>(this, f);
    }

    /**
     * Creates a new {@link GetterSetterPropertySeed} object.
     */
    protected PropertySeed<T,C,F,M> createAccessorSeed(M getter, M setter) {
        return new GetterSetterPropertySeed<>(this, getter,setter);
    }

    @Override
    public final boolean isElement() {
        return elementName!=null;
    }

    @Override
    public boolean isAbstract() {
        return nav().isAbstract(clazz);
    }

    @Override
    public boolean isOrdered() {
        return propOrder!=null;
    }

    @Override
    public final boolean isFinal() {
        return nav().isFinal(clazz);
    }

    @Override
    public final boolean hasSubClasses() {
        return hasSubClasses;
    }

    @Override
    public final boolean hasAttributeWildcard() {
        return declaresAttributeWildcard() || inheritsAttributeWildcard();
    }

    @Override
    public final boolean inheritsAttributeWildcard() {
        return getInheritedAttributeWildcard()!=null;
    }

    @Override
    public final boolean declaresAttributeWildcard() {
        return attributeWildcard!=null;
    }

    /**
     * Gets the {@link PropertySeed} object for the inherited attribute wildcard.
     */
    private PropertySeed<T,C,F,M> getInheritedAttributeWildcard() {
        for( ClassInfoImpl<T,C,F,M> c=getBaseClass(); c!=null; c=c.getBaseClass() )
            if(c.attributeWildcard!=null)
                return c.attributeWildcard;
        return null;
    }

    @Override
    public final QName getElementName() {
        return elementName;
    }

    @Override
    public final QName getTypeName() {
        return typeName;
    }

    @Override
    public final boolean isSimpleType() {
        List<? extends PropertyInfo> props = getProperties();
        if(props.size()!=1)     return false;
        return props.get(0).kind()==PropertyKind.VALUE;
    }

    /**
     * Called after all the {@link org.glassfish.jaxb.core.v2.model.core.TypeInfo}s are collected into the {@link #owner}.
     */
    @Override
    /*package*/ void link() {
        getProperties();    // make sure properties!=null

        // property name collision cehck
        Map<String,PropertyInfoImpl> names = new HashMap<>();
        for( PropertyInfoImpl<T,C,F,M> p : properties ) {
            p.link();
            PropertyInfoImpl old = names.put(p.getName(),p);
            if(old!=null) {
                builder.reportError(new IllegalAnnotationException(
                    Messages.PROPERTY_COLLISION.format(p.getName()),
                    p, old ));
            }
        }
        super.link();
    }

    @Override
    public Location getLocation() {
        return nav().getClassLocation(clazz);
    }

    /**
     *  XmlType allows specification of factoryClass and
     *  factoryMethod.  There are to be used if no default
     *  constructor is found.
     *
     * @return
     *      true if the factory method was found. False if not.
     */
    private  boolean hasFactoryConstructor(XmlType t){
        if (t == null) return false;

        String method = t.factoryMethod();
        T fClass = reader().getClassValue(t, "factoryClass");
        if (method.length() > 0){
            if(nav().isSameType(fClass, nav().ref(XmlType.DEFAULT.class))){
                fClass = nav().use(clazz);
            }
            for(M m: nav().getDeclaredMethods(nav().asDecl(fClass))){
                //- Find the zero-arg public static method with the required return type
                if (nav().getMethodName(m).equals(method) &&
                    nav().isSameType(nav().getReturnType(m), nav().use(clazz)) &&
                    nav().getMethodParameters(m).length == 0 &&
                    nav().isStaticMethod(m)){
                    factoryMethod = m;
                    break;
                }
            }
            if (factoryMethod == null){
                builder.reportError(new IllegalAnnotationException(
                Messages.NO_FACTORY_METHOD.format(nav().getClassName(nav().asDecl(fClass)), method), this ));
            }
        } else if(!nav().isSameType(fClass, nav().ref(XmlType.DEFAULT.class))){
            builder.reportError(new IllegalAnnotationException(
                Messages.FACTORY_CLASS_NEEDS_FACTORY_METHOD.format(nav().getClassName(nav().asDecl(fClass))), this ));
        }
        return factoryMethod != null;
    }

    public Method getFactoryMethod(){
        return (Method) factoryMethod;
    }

    private boolean hasApplicableAdapter(PropertySeed<T, C, F, M> seed, T type) {
        XmlJavaTypeAdapter jta = seed.readAnnotation(XmlJavaTypeAdapter.class);
        if (jta != null && isApplicable(jta, type)) {
            return true;
        }
        // check the applicable adapters on the package
        XmlJavaTypeAdapters jtas = reader().getPackageAnnotation(XmlJavaTypeAdapters.class, clazz, seed);
        if (jtas != null) {
            for (XmlJavaTypeAdapter xjta : jtas.value()) {
                if (isApplicable(xjta, type)) {
                    return true;
                }
            }
        }
        jta = reader().getPackageAnnotation(XmlJavaTypeAdapter.class, clazz, seed);
        if (isApplicable(jta, type)) {
            return true;
        }
        // then on the target class
        C refType = nav().asDecl(type);
        if (refType != null) {
            jta = reader().getClassAnnotation(XmlJavaTypeAdapter.class, refType, seed);
            if (jta != null && isApplicable(jta, type)) {// the one on the type always apply.
                return true;
            }
        }

        return false;
    }

    private boolean isApplicable(XmlJavaTypeAdapter jta, T declaredType) {
        if (jta == null) {return false; }

        T type = reader().getClassValue(jta, "type");
        if (nav().isSameType(declaredType, type)) {
            return true;    // for types explicitly marked in XmlJavaTypeAdapter.type()
        }
        T ad = reader().getClassValue(jta, "value");
        T ba = nav().getBaseClass(ad, nav().asDecl(XmlAdapter.class));
        if (!nav().isParameterizedType(ba)) {
            return true;   // can't check type applicability. assume Object, which means applicable to any.
        }
        T inMemType = nav().getTypeArgument(ba, 1);

        return nav().isSubClassOf(declaredType, inMemType);
    }

    @Override
    public String toString() {
        return "ClassInfo("+clazz+')';
    }

    private static final String[] DEFAULT_ORDER = new String[0];
}
