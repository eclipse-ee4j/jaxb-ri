/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import com.sun.codemodel.util.ClassNameComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * JavaDoc comment.
 *
 * <p>
 * A javadoc comment consists of multiple parts. There's the main part (that comes the first in
 * in the comment section), then the parameter parts (@param), the return part (@return),
 * and the throws parts (@throws).
 *
 * TODO: it would be nice if we have JComment class and we can derive this class from there.
 */
public class JDocComment extends JCommentPart implements JGenerable {

	private static final long serialVersionUID = 1L;

	/** list of @param tags */
    private final transient Map<String,JCommentPart> atParams = new TreeMap<>();
    
    /** list of xdoclets */
    private final transient Map<String,Map<String,String>> atXdoclets = new TreeMap<>();
    
    /** list of @throws tags */
    private final transient Map<JClass,JCommentPart> atThrows = new TreeMap<>(ClassNameComparator.theInstance);
    
    /**
     * The @return tag part.
     */
    private JCommentPart atReturn = null;
    
    /** The @deprecated tag */
    private JCommentPart atDeprecated = null;

    private final transient JCodeModel owner;


    public JDocComment(JCodeModel owner) {
        this.owner = owner;
    }

        @Override
    public JDocComment append(Object o) {
        add(o);
        return this;
    }

    /**
     * Append a text to a @param tag to the javadoc
     */
    public JCommentPart addParam( String param ) {
        JCommentPart p = atParams.get(param);
        if(p==null)
            atParams.put(param,p=new JCommentPart());
        return p;
    }

    /**
     * Append a text to an @param tag.
     */
    public JCommentPart addParam( JVar param ) {
        return addParam( param.name() );
    }


    /**
     * add an @throws tag to the javadoc
     */
    public JCommentPart addThrows( Class<? extends Throwable> exception ) {
        return addThrows( owner.ref(exception) );
    }
    
    /**
     * add an @throws tag to the javadoc
     */
    public JCommentPart addThrows( JClass exception ) {
        JCommentPart p = atThrows.get(exception);
        if(p==null)
            atThrows.put(exception,p=new JCommentPart());
        return p;
    }
    
    /**
     * Appends a text to @return tag.
     */
    public JCommentPart addReturn() {
        if(atReturn==null)
            atReturn = new JCommentPart();
        return atReturn;
    }

    /**
     * add an @deprecated tag to the javadoc, with the associated message.
     */
    public JCommentPart addDeprecated() {
        if(atDeprecated==null)
            atDeprecated = new JCommentPart();
        return atDeprecated;
    }

    /**
     * add an xdoclet.
     */
    public Map<String,String> addXdoclet(String name) {
        return atXdoclets.computeIfAbsent(name, k -> new HashMap<>());
    }

    /**
     * add an xdoclet.
     */
    public Map<String,String> addXdoclet(String name, Map<String,String> attributes) {
        Map<String, String> p = atXdoclets.computeIfAbsent(name, k -> new HashMap<>());
        p.putAll(attributes);
        return p;
    }

    /**
     * add an xdoclet.
     */
    public Map<String,String> addXdoclet(String name, String attribute, String value) {
        Map<String, String> p = atXdoclets.computeIfAbsent(name, k -> new HashMap<>());
        p.put(attribute, value);
        return p;
    }

        @Override
    public void generate(JFormatter f) {
        // I realized that we can't use StringTokenizer because
        // this will recognize multiple \n as one token.

        f.p("/**").nl();

        format(f," * ");

        f.p(" * ").nl();
        for (Map.Entry<String,JCommentPart> e : atParams.entrySet()) {
            f.p(" * @param ").p(e.getKey()).nl();
            e.getValue().format(f,INDENT);
        }
        if( atReturn != null ) {
            f.p(" * @return").nl();
            atReturn.format(f,INDENT);
        }
        for (Map.Entry<JClass,JCommentPart> e : atThrows.entrySet()) {
            f.p(" * @throws ").t(e.getKey()).nl();
            e.getValue().format(f,INDENT);
        }
        if( atDeprecated != null ) {
            f.p(" * @deprecated").nl();
            atDeprecated.format(f,INDENT);
        }
        for (Map.Entry<String,Map<String,String>> e : atXdoclets.entrySet()) {
            f.p(" * @").p(e.getKey());
            if (e.getValue() != null) {
                for (Map.Entry<String,String> a : e.getValue().entrySet()) {
                    f.p(" ").p(a.getKey()).p("= \"").p(a.getValue()).p("\"");
                }
            }
            f.nl();
        }
        f.p(" */").nl();
    }

    private static final String INDENT = " *     ";
}

