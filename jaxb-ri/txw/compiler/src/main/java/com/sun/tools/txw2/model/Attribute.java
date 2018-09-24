/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.txw2.model;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.tools.txw2.NameUtil;
import com.sun.tools.txw2.model.prop.AttributeProp;
import com.sun.tools.txw2.model.prop.Prop;
import com.sun.xml.txw2.annotation.XmlAttribute;
import org.xml.sax.Locator;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.Set;

/**
 * Attribute declaration.
 * 
 * @author Kohsuke Kawaguchi
 */
public class Attribute extends XmlNode {
    public Attribute(Locator location, QName name, Leaf leaf) {
        super(location, name, leaf);
    }

    void declare(NodeSet nset) {
        ; // attributes won't produce a class
    }

    void generate(NodeSet nset) {
        ; // nothing
    }

    void generate(JDefinedClass clazz, NodeSet nset, Set<Prop> props) {
        Set<JType> types = new HashSet<JType>();

        for( Leaf l : collectChildren() ) {
            if (l instanceof Text) {
                types.add(((Text)l).getDatatype(nset));
            }
        }

        String methodName = NameUtil.toMethodName(name.getLocalPart());

        for( JType t : types ) {
            if(!props.add(new AttributeProp(name,t)))
                continue;

            JMethod m = clazz.method(JMod.PUBLIC,
                nset.opts.chainMethod? (JType)clazz : nset.codeModel.VOID,
                methodName);
            m.param(t,"value");

            JAnnotationUse a = m.annotate(XmlAttribute.class);
            if(!methodName.equals(name.getLocalPart()))
                a.param("value",name.getLocalPart());
            if(!name.getNamespaceURI().equals(""))
                a.param("ns",name.getNamespaceURI());

        }
    }

    public String toString() {
        return "Attribute "+name;
    }
}
