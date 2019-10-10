/*
 * Copyright (c) 1997, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A part is a part of a javadoc comment, and it is a list of values.
 *
 * <p>
 * A part can contain a free-form text. This text is modeled as a collection of 'values'
 * in this class. A value can be a {@link JType} (which will be prinited with a @link tag),
 * anything that can be turned into a {@link String} via the {@link Object#toString()} method,
 * or a {@link Collection}/array of those objects.
 *
 * <p>
 * Values can be added through the various append methods one by one or in a bulk.
 *
 * @author Kohsuke Kawaguchi
 */
public class JCommentPart extends ArrayList<Object> {

	private static final long serialVersionUID = 1L;

	/**
     * Appends a new value.
     *
     * If the value is {@link JType} it will be printed as a @link tag.
     * Otherwise it will be converted to String via {@link Object#toString()}.
     */
    public JCommentPart append(Object o) {
        add(o);
        return this;
    }

    public boolean add(Object o) {
        flattenAppend(o);
        return true;
    }

    private void flattenAppend(Object value) {
        if(value==null) return;
        if(value instanceof Object[]) {
            for( Object o : (Object[])value)
                flattenAppend(o);
        } else
        if(value instanceof Collection<?>) {
            for( Object o : (Collection<?>)value)
                flattenAppend(o);
        } else
            super.add(value);
    }

    /**
     * Writes this part into the formatter by using the specified indentation.
     */
    protected void format( JFormatter f, String indent ) {
        if(!f.isPrinting()) {
            // quickly pass the types to JFormatter, as that's all we care.
            // we don't need to worry about the exact formatting of text.
            for( Object o : this )
                if(o instanceof JClass)
                    f.g((JClass)o);
            return;
        }

        if(!isEmpty())
            f.p(indent);

        Iterator<Object> itr = iterator();
        while(itr.hasNext()) {
            Object o = itr.next();

            if(o instanceof String) {
                int idx;
                String s = (String)o;
                while( (idx=s.indexOf('\n'))!=-1 ) {
                    String line = s.substring(0,idx);
                    if(line.length()>0)
                        f.p(escape(line));
                    s = s.substring(idx+1);
                    f.nl().p(indent);
                }
                if(s.length()!=0)
                    f.p(escape(s));
            } else
            if(o instanceof JClass) {
                // TODO: this doesn't print the parameterized type properly
                ((JClass)o).printLink(f);
            } else
            if(o instanceof JType) {
                f.g((JType)o);
            } else
                throw new IllegalStateException();
        }

        if(!isEmpty())
            f.nl();
    }

    /**
     * Escapes the appearance of the comment terminator.
     */
    private String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '@':
                    sb.append("&#064;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '*':
                    sb.append(c);
                    if (s.charAt(i+1) == '/') {
                        sb.append("<!---->");
                        sb.append('/');
                        i++;
                    }
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
