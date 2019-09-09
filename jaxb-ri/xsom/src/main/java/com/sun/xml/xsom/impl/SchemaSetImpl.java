/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.xsom.impl;

import com.sun.xml.xsom.SCD;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSListSimpleType;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSUnionSimpleType;
import com.sun.xml.xsom.XSVariety;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.impl.scd.Iterators;
import com.sun.xml.xsom.visitor.XSContentTypeFunction;
import com.sun.xml.xsom.visitor.XSContentTypeVisitor;
import com.sun.xml.xsom.visitor.XSFunction;
import com.sun.xml.xsom.visitor.XSSimpleTypeFunction;
import com.sun.xml.xsom.visitor.XSSimpleTypeVisitor;
import com.sun.xml.xsom.visitor.XSVisitor;
import org.xml.sax.Locator;

import javax.xml.namespace.NamespaceContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class SchemaSetImpl implements XSSchemaSet
{
    private final Map<String,XSSchema> schemas = new HashMap<String,XSSchema>();
    private final Vector<XSSchema> schemas2 = new Vector<XSSchema>();
    private final List<XSSchema> readonlySchemaList = Collections.unmodifiableList(schemas2);

    /**
     * Gets a reference to the existing schema or creates a new one
     * if none exists yet.
     */
    public SchemaImpl createSchema(String targetNamespace, Locator location) {
        SchemaImpl obj = (SchemaImpl)schemas.get(targetNamespace);
        if (obj == null) {
            obj = new SchemaImpl(this, location, targetNamespace);
            schemas.put(targetNamespace, obj);
            schemas2.add(obj);
        }
        return obj;
    }

    public int getSchemaSize() {
        return schemas.size();
    }
    public XSSchema getSchema(String targetNamespace) {
        return schemas.get(targetNamespace);
    }
    public XSSchema getSchema(int idx) {
        return schemas2.get(idx);
    }
    public Iterator<XSSchema> iterateSchema() {
        return schemas2.iterator();
    }

    public final Collection<XSSchema> getSchemas() {
        return readonlySchemaList;
    }

    public XSType getType(String ns, String localName) {
        XSSchema schema = getSchema(ns);
        if(schema==null)    return null;

        return schema.getType(localName);
    }

    public XSSimpleType getSimpleType( String ns, String localName ) {
        XSSchema schema = getSchema(ns);
        if(schema==null)    return null;

        return schema.getSimpleType(localName);
    }

    public XSElementDecl getElementDecl( String ns, String localName ) {
        XSSchema schema = getSchema(ns);
        if(schema==null)    return null;

        return schema.getElementDecl(localName);
    }

    public XSAttributeDecl getAttributeDecl( String ns, String localName ) {
        XSSchema schema = getSchema(ns);
        if(schema==null)    return null;

        return schema.getAttributeDecl(localName);
    }

    public XSModelGroupDecl getModelGroupDecl( String ns, String localName ) {
        XSSchema schema = getSchema(ns);
        if(schema==null)    return null;

        return schema.getModelGroupDecl(localName);
    }

    public XSAttGroupDecl getAttGroupDecl( String ns, String localName ) {
        XSSchema schema = getSchema(ns);
        if(schema==null)    return null;

        return schema.getAttGroupDecl(localName);
    }

    public XSComplexType getComplexType( String ns, String localName ) {
        XSSchema schema = getSchema(ns);
        if(schema==null)    return null;

        return schema.getComplexType(localName);
    }

    public XSIdentityConstraint getIdentityConstraint(String ns, String localName) {
        XSSchema schema = getSchema(ns);
        if(schema==null)    return null;

        return schema.getIdentityConstraint(localName);
    }

    public Iterator<XSElementDecl> iterateElementDecls() {
        return new Iterators.Map<XSElementDecl,XSSchema>(iterateSchema()) {
            protected Iterator<XSElementDecl> apply(XSSchema u) {
                return u.iterateElementDecls();
            }
        };
    }

    public Iterator<XSType> iterateTypes() {
        return new Iterators.Map<XSType,XSSchema>(iterateSchema()) {
            protected Iterator<XSType> apply(XSSchema u) {
                return u.iterateTypes();
            }
        };
    }

    public Iterator<XSAttributeDecl> iterateAttributeDecls() {
        return new Iterators.Map<XSAttributeDecl,XSSchema>(iterateSchema()) {
            protected Iterator<XSAttributeDecl> apply(XSSchema u) {
                return u.iterateAttributeDecls();
            }
        };
    }
    public Iterator<XSAttGroupDecl> iterateAttGroupDecls() {
        return new Iterators.Map<XSAttGroupDecl,XSSchema>(iterateSchema()) {
            protected Iterator<XSAttGroupDecl> apply(XSSchema u) {
                return u.iterateAttGroupDecls();
            }
        };
    }
    public Iterator<XSModelGroupDecl> iterateModelGroupDecls() {
        return new Iterators.Map<XSModelGroupDecl,XSSchema>(iterateSchema()) {
            protected Iterator<XSModelGroupDecl> apply(XSSchema u) {
                return u.iterateModelGroupDecls();
            }
        };
    }
    public Iterator<XSSimpleType> iterateSimpleTypes() {
        return new Iterators.Map<XSSimpleType,XSSchema>(iterateSchema()) {
            protected Iterator<XSSimpleType> apply(XSSchema u) {
                return u.iterateSimpleTypes();
            }
        };
    }
    public Iterator<XSComplexType> iterateComplexTypes() {
        return new Iterators.Map<XSComplexType,XSSchema>(iterateSchema()) {
            protected Iterator<XSComplexType> apply(XSSchema u) {
                return u.iterateComplexTypes();
            }
        };
    }
    public Iterator<XSNotation> iterateNotations() {
        return new Iterators.Map<XSNotation,XSSchema>(iterateSchema()) {
            protected Iterator<XSNotation> apply(XSSchema u) {
                return u.iterateNotations();
            }
        };
    }

    public Iterator<XSIdentityConstraint> iterateIdentityConstraints() {
        return new Iterators.Map<XSIdentityConstraint,XSSchema>(iterateSchema()) {
            protected Iterator<XSIdentityConstraint> apply(XSSchema u) {
                return u.getIdentityConstraints().values().iterator();
            }
        };
    }

    public Collection<XSComponent> select(String scd, NamespaceContext nsContext) {
        try {
            return SCD.create(scd,nsContext).select(this);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public XSComponent selectSingle(String scd, NamespaceContext nsContext) {
        try {
            return SCD.create(scd,nsContext).selectSingle(this);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public final EmptyImpl empty = new EmptyImpl();
    public XSContentType getEmpty() { return empty; }

    public XSSimpleType getAnySimpleType() { return anySimpleType; }
    public final AnySimpleType anySimpleType = new AnySimpleType();
    private class AnySimpleType extends DeclarationImpl
        implements XSRestrictionSimpleType, Ref.SimpleType {

        AnySimpleType() {
            super(null,null,null,null,"http://www.w3.org/2001/XMLSchema","anySimpleType",false);
        }
        public SchemaImpl getOwnerSchema() {
            return createSchema("http://www.w3.org/2001/XMLSchema",null);
        }
        public XSSimpleType asSimpleType() { return this; }
        public XSComplexType asComplexType() { return null; }

        public boolean isDerivedFrom(XSType t) {
            return t==this || t==anyType;
        }

        public boolean isSimpleType()       { return true; }
        public boolean isComplexType()      { return false; }
        public XSContentType asEmpty() { return null; }
        public XSParticle asParticle() { return null; }
        public XSType getBaseType() { return anyType; }
        public XSSimpleType getSimpleBaseType() { return null; }
        public int getDerivationMethod() { return RESTRICTION; }
        public Iterator<XSFacet> iterateDeclaredFacets() { return Iterators.empty(); }
        public Collection<? extends XSFacet> getDeclaredFacets() { return Collections.EMPTY_LIST; }
        public void visit( XSSimpleTypeVisitor visitor ) {visitor.restrictionSimpleType(this); }
        public void visit( XSContentTypeVisitor visitor ) {visitor.simpleType(this); }
        public void visit( XSVisitor visitor ) {visitor.simpleType(this); }
        public <T> T apply( XSSimpleTypeFunction<T> f ) {return f.restrictionSimpleType(this); }
        public <T> T apply( XSContentTypeFunction<T> f ) { return f.simpleType(this); }
        public <T> T apply( XSFunction<T> f ) { return f.simpleType(this); }
        public XSVariety getVariety() { return XSVariety.ATOMIC; }
        public XSSimpleType getPrimitiveType() {return this;}
        public boolean isPrimitive() {return true;}
        public XSListSimpleType getBaseListType() {return null;}
        public XSUnionSimpleType getBaseUnionType() {return null;}
        public XSFacet getFacet(String name) { return null; }
        public List<XSFacet> getFacets( String name ) { return Collections.EMPTY_LIST; }
        public XSFacet getDeclaredFacet(String name) { return null; }
        public List<XSFacet> getDeclaredFacets(String name) { return Collections.EMPTY_LIST; }

        public boolean isRestriction() { return true; }
        public boolean isList() { return false; }
        public boolean isUnion() { return false; }
        public boolean isFinal(XSVariety v) { return false; }
        public XSRestrictionSimpleType asRestriction() { return this; }
        public XSListSimpleType asList() { return null; }
        public XSUnionSimpleType asUnion() { return null; }
        public XSSimpleType getType() { return this; } // Ref.SimpleType implementation
        public XSSimpleType getRedefinedBy() { return null; }
        public int getRedefinedCount() { return 0; }

        public XSType[] listSubstitutables() {
            return ImplUtil.listSubstitutables(this);
        }
    }

    public XSComplexType getAnyType() { return anyType; }
    public final AnyType anyType = new AnyType();
    private class AnyType extends DeclarationImpl implements XSComplexType, Ref.Type {
        AnyType() {
            super(null,null,null,null,"http://www.w3.org/2001/XMLSchema","anyType",false);
        }
        public SchemaImpl getOwnerSchema() {
            return createSchema("http://www.w3.org/2001/XMLSchema",null);
        }
        public boolean isAbstract() { return false; }
        public XSWildcard getAttributeWildcard() { return anyWildcard; }
        public XSAttributeUse getAttributeUse( String nsURI, String localName ) { return null; }
        public Iterator<XSAttributeUse> iterateAttributeUses() { return Iterators.empty(); }
        public XSAttributeUse getDeclaredAttributeUse( String nsURI, String localName ) { return null; }
        public Iterator<XSAttributeUse> iterateDeclaredAttributeUses() { return Iterators.empty(); }
        public Iterator<XSAttGroupDecl> iterateAttGroups() { return Iterators.empty(); }
        public Collection<XSAttributeUse> getAttributeUses() { return Collections.EMPTY_LIST; }
        public Collection<? extends XSAttributeUse> getDeclaredAttributeUses() { return Collections.EMPTY_LIST; }
        public Collection<? extends XSAttGroupDecl> getAttGroups() { return Collections.EMPTY_LIST; }
        public boolean isFinal( int i ) { return false; }
        public boolean isSubstitutionProhibited( int i ) { return false; }
        public boolean isMixed() { return true; }
        public XSContentType getContentType() { return contentType; }
        public XSContentType getExplicitContent() { return null; }
        public XSType getBaseType() { return this; }
        public XSSimpleType asSimpleType() { return null; }
        public XSComplexType asComplexType() { return this; }

        public boolean isDerivedFrom(XSType t) {
            return t==this;
        }

        public boolean isSimpleType()       { return false; }
        public boolean isComplexType()      { return true; }
        public XSContentType asEmpty() { return null; }
        public int getDerivationMethod() { return XSType.RESTRICTION; }

        public XSElementDecl getScope() { return null; }
        public void visit( XSVisitor visitor ) { visitor.complexType(this); }
        public <T> T apply( XSFunction<T> f ) { return f.complexType(this); }

        public XSType getType() { return this; } // Ref.Type implementation

        public XSComplexType getRedefinedBy() { return null; }
        public int getRedefinedCount() { return 0; }

        public XSType[] listSubstitutables() {
            return ImplUtil.listSubstitutables(this);
        }

        private final WildcardImpl anyWildcard = new WildcardImpl.Any(null,null,null,null,XSWildcard.SKIP);
        private final XSContentType contentType = new ParticleImpl( null, null,
                new ModelGroupImpl(null, null, null, null,XSModelGroup.SEQUENCE, new ParticleImpl[]{
                    new ParticleImpl( null, null,
                        anyWildcard, null,
                        XSParticle.UNBOUNDED, 0 )
                })
                ,null,1,1);
        public List<XSComplexType> getSubtypes() {
            ArrayList subtypeList = new ArrayList();
            Iterator<XSComplexType> cTypes = getRoot().iterateComplexTypes();
            while (cTypes.hasNext()) {
                XSComplexType cType= cTypes.next();
                XSType base = cType.getBaseType();
                if ((base != null) && (base.equals(this))) {
                    subtypeList.add(cType);
                }
            }
            return subtypeList;
        }

        public List<XSElementDecl> getElementDecls() {
            ArrayList declList = new ArrayList();
            XSSchemaSet schemaSet = getRoot();
            for (XSSchema sch : schemaSet.getSchemas()) {
                for (XSElementDecl decl : sch.getElementDecls().values()) {
                    if (decl.getType().equals(this)) {
                        declList.add(decl);
                    }
                }
            }
            return declList;
        }
    }
}
