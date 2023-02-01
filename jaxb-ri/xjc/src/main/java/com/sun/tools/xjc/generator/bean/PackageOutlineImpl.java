/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.tools.xjc.generator.annotation.spec.XmlSchemaWriter;
import com.sun.tools.xjc.model.CAttributePropertyInfo;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElement;
import com.sun.tools.xjc.model.CElementPropertyInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CPropertyVisitor;
import com.sun.tools.xjc.model.CReferencePropertyInfo;
import com.sun.tools.xjc.model.CTypeRef;
import com.sun.tools.xjc.model.CValuePropertyInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.PackageOutline;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link PackageOutline} enhanced with schema2java specific
 * information.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com), Martin Grebac (martin.grebac@oracle.com)
 */
public final class PackageOutlineImpl implements PackageOutline {
    private final Model _model;
    private final JPackage _package;
    private final ObjectFactoryGenerator objectFactoryGenerator;

    /*package*/ final Set<ClassOutlineImpl> classes = new LinkedHashSet<>();
    private final Set<ClassOutlineImpl> classesView = Collections.unmodifiableSet(classes);

    private String mostUsedNamespaceURI;
    private XmlNsForm elementFormDefault;
    private XmlNsForm attributeFormDefault;

    /**
     * The namespace URI most commonly used in classes in this package.
     * This should be used as the namespace URI for {@link XmlSchema#namespace()}.
     *
     * <p>
     * Null if no default
     *
     * @see #calcDefaultValues()
     */
    @Override
    public String getMostUsedNamespaceURI() {
        return mostUsedNamespaceURI;
    }

    /**
     * The attribute form default for this package.
     * <p>
     * The value is computed by examining what would yield the smallest generated code.
     */
    @Override
    public XmlNsForm getAttributeFormDefault() {
        assert attributeFormDefault!=null;
        return attributeFormDefault;
    }

    /**
     * The element form default for this package.
     * <p>
     * The value is computed by examining what would yield the smallest generated code.
     */
    @Override
    public XmlNsForm getElementFormDefault() {
        assert elementFormDefault!=null;
        return elementFormDefault;
    }

    @Override
    public JPackage _package() {
        return _package;
    }

    @Override
    public ObjectFactoryGenerator objectFactoryGenerator() {
        return objectFactoryGenerator;
    }

    @Override
    public Set<ClassOutlineImpl> getClasses() {
        return classesView;
    }

    @Override
    public JDefinedClass objectFactory() {
        return objectFactoryGenerator.getObjectFactory();
    }

    protected PackageOutlineImpl( BeanGenerator outline, Model model, JPackage _pkg ) {
        this._model = model;
        this._package = _pkg;
        switch(model.strategy) {
        case BEAN_ONLY:
            objectFactoryGenerator = new PublicObjectFactoryGenerator(outline,model,_pkg);
            break;
        case INTF_AND_IMPL:
            objectFactoryGenerator = new DualObjectFactoryGenerator(outline,model,_pkg);
            break;
        default:
            throw new IllegalStateException();
        }
    }

    /**
     * Compute the most common namespace URI in this package
     * (to put into {@link XmlSchema#namespace()} and what value
     * we should put into {@link XmlSchema#elementFormDefault()}.
     *
     * This method is called after {@link #classes} field is filled up.
     */
    public void calcDefaultValues() {
        // short-circuit if xjc was told not to generate package level annotations in
        // package-info.java
        if(!_model.isPackageLevelAnnotations()) {
            mostUsedNamespaceURI = "";
            elementFormDefault = XmlNsForm.UNQUALIFIED;
            return;
        }

        // used to visit properties
        CPropertyVisitor<Void> propVisitor = new CPropertyVisitor<Void>() {
            public Void onElement(CElementPropertyInfo p) {
                for (CTypeRef tr : p.getTypes()) {
                    countURI(propUriCountMap, tr.getTagName());
                }
                return null;
            }

            @Override
            public Void onReference(CReferencePropertyInfo p) {
                for (CElement e : p.getElements()) {
                    countURI(propUriCountMap, e.getElementName());
                }
                return null;
            }

            @Override
            public Void onAttribute(CAttributePropertyInfo p) {
                return null;
            }

            @Override
            public Void onValue(CValuePropertyInfo p) {
                return null;
            }
        };


        for (ClassOutlineImpl co : classes) {
            CClassInfo ci = co.target;
            countURI(uriCountMap, ci.getTypeName());
            countURI(uriCountMap, ci.getElementName());

            for( CPropertyInfo p : ci.getProperties() )
                p.accept(propVisitor);
        }
        mostUsedNamespaceURI = getMostUsedURI(uriCountMap);
        
        elementFormDefault = getFormDefault();
        attributeFormDefault = XmlNsForm.UNQUALIFIED;
        try {
            attributeFormDefault = _model.getAttributeFormDefault(mostUsedNamespaceURI);
        } catch (Exception e) {
            // ignore and accept default
        }
        
        // generate package-info.java
        // we won't get this far if the user specified -npa
        if(!mostUsedNamespaceURI.equals("") || elementFormDefault==XmlNsForm.QUALIFIED || (attributeFormDefault == XmlNsForm.QUALIFIED)) {
            XmlSchemaWriter w = _model.strategy.getPackage(_package, Aspect.IMPLEMENTATION).annotate2(XmlSchemaWriter.class);
            if(!mostUsedNamespaceURI.equals(""))
                w.namespace(mostUsedNamespaceURI);
            if(elementFormDefault==XmlNsForm.QUALIFIED)
                w.elementFormDefault(elementFormDefault);
            if(attributeFormDefault==XmlNsForm.QUALIFIED)
                w.attributeFormDefault(attributeFormDefault);
        }
    }

    // Map to keep track of how often each type or element uri is used in this package
    // mostly used to calculate mostUsedNamespaceURI
    private Map<String, Integer> uriCountMap = new LinkedHashMap<>();

    // Map to keep track of how often each property uri is used in this package
    // used to calculate elementFormDefault
    private Map<String, Integer> propUriCountMap = new LinkedHashMap<>();

    /**
     * pull the uri out of the specified QName and keep track of it in the
     * specified hash map
     *
     */
    private void countURI(Map<String, Integer> map, QName qname) {
        if (qname == null) return;

        String uri = qname.getNamespaceURI();

        if (map.containsKey(uri)) {
            map.put(uri, map.get(uri) + 1);
        } else {
            map.put(uri, 1);
        }
    }

    /**
     * Iterate through the hash map looking for the namespace used
     * most frequently.  Ties are arbitrarily broken by the order
     * in which the map keys are iterated over.
     *
     * <p>
     * Because JAX-WS often reassigns the "" namespace URI,
     * and when that happens it unintentionally also renames (normally
     * unqualified) local elements, prefer non-"" URI when there's a tie. 
     */
    private String getMostUsedURI(Map<String, Integer> map) {
        String mostPopular = null;
        int count = 0;

        for (Map.Entry<String,Integer> e : map.entrySet()) {
            String uri = e.getKey();
            int uriCount = e.getValue();
            if (mostPopular == null) {
                mostPopular = uri;
                count = uriCount;
            } else {
                if (uriCount > count || (uriCount==count && mostPopular.equals(""))) {
                    mostPopular = uri;
                    count = uriCount;
                }
            }
        }

        if (mostPopular == null) return "";
        return mostPopular;
    }

    /**
     * Calculate the element form defaulting.
     *
     * Compare the most frequently used property URI to the most frequently used
     * element/type URI.  If they match, then return QUALIFIED
     */
    private XmlNsForm getFormDefault() {
        if (getMostUsedURI(propUriCountMap).equals("")) return XmlNsForm.UNQUALIFIED;
        else return XmlNsForm.QUALIFIED;
    }
    
}
