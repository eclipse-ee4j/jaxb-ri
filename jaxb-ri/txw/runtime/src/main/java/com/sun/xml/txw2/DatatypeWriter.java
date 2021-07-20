/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.txw2;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;

/**
 * Pluggable datatype writer.
 *
 * @author Kohsuke Kawaguchi
 */
public interface DatatypeWriter<DT> {

    /**
     * Gets the Java class that this writer can write.
     *
     * @return
     *      must not be null. Must be the same value always.
     */
    Class<DT> getType();

    /**
     * Prints the given datatype object and appends that result
     * into the given buffer.
     *
     * @param dt
     *      the datatype object to be printed.
     * @param resolver
     *      allows the converter to declare additional namespace prefixes.
     */
    void print(DT dt, NamespaceResolver resolver, StringBuilder buf);

    static final List<DatatypeWriter<?>> BUILTIN = Collections.unmodifiableList(new AbstractList() {
        
        private DatatypeWriter<?>[] BUILTIN_ARRAY = new DatatypeWriter<?>[] {
            new DatatypeWriter<String>() {
                public Class<String> getType() {
                    return String.class;
                }
                public void print(String s, NamespaceResolver resolver, StringBuilder buf) {
                    buf.append(s);
                }
            },
            new DatatypeWriter<Integer>() {
                public Class<Integer> getType() {
                    return Integer.class;
                }
                public void print(Integer i, NamespaceResolver resolver, StringBuilder buf) {
                    buf.append(i);
                }
            },
            new DatatypeWriter<Float>() {
                public Class<Float> getType() {
                    return Float.class;
                }
                public void print(Float f, NamespaceResolver resolver, StringBuilder buf) {
                    buf.append(f);
                }
            },
            new DatatypeWriter<Double>() {
                public Class<Double> getType() {
                    return Double.class;
                }
                public void print(Double d, NamespaceResolver resolver, StringBuilder buf) {
                    buf.append(d);
                }
            },
            new DatatypeWriter<QName>() {
                public Class<QName> getType() {
                    return QName.class;
                }
                public void print(QName qn, NamespaceResolver resolver, StringBuilder buf) {
                    String p = resolver.getPrefix(qn.getNamespaceURI());
                    if(p.length()!=0)
                        buf.append(p).append(':');
                    buf.append(qn.getLocalPart());
                }
            }
        };
                
        public DatatypeWriter<?> get(int n) { 
          return BUILTIN_ARRAY[n];
        }

        public int size() {
          return BUILTIN_ARRAY.length;
        }
      });
}
