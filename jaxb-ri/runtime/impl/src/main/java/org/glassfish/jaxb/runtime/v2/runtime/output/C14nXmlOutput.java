/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.output;

import com.sun.istack.FinalArrayList;
import org.glassfish.jaxb.core.marshaller.CharacterEscapeHandler;
import org.glassfish.jaxb.runtime.api.JAXBRIContext;
import org.glassfish.jaxb.runtime.v2.runtime.Name;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;

/**
 * {@link XmlOutput} that generates canonical XML.
 *
 * @author Kohsuke Kawaguchi
 */
public class C14nXmlOutput extends UTF8XmlOutput {
    public C14nXmlOutput(OutputStream out, Encoded[] localNames, boolean namedAttributesAreOrdered, CharacterEscapeHandler escapeHandler) {
        super(out, localNames, escapeHandler);
        this.namedAttributesAreOrdered = namedAttributesAreOrdered;

        for( int i=0; i<staticAttributes.length; i++ )
            staticAttributes[i] = new StaticAttribute();
    }

    /**
     * Hosts statically known attributes.
     *
     * {@link StaticAttribute} instances are reused.
     */
    private StaticAttribute[] staticAttributes = new StaticAttribute[8];
    private int len = 0;

    /**
     * Used to sort namespace declarations. Reused.
     */
    private int[] nsBuf = new int[8];

    /**
     * Hosts other attributes whose name are not statically known
     * (AKA attribute wildcard.)
     *
     * As long as this map is empty, there's no need for sorting.
     */
    private final FinalArrayList<DynamicAttribute> otherAttributes = new FinalArrayList<>();

    /**
     * True if {@link JAXBRIContext} is created with c14n support on,
     * in which case all named attributes are sorted by the marshaller
     * and we won't have to do it here.
     */
    private final boolean namedAttributesAreOrdered;

    final class StaticAttribute implements Comparable<StaticAttribute> {
        Name name;
        String value;

        public void set(Name name, String value) {
            this.name = name;
            this.value = value;
        }

        void write() throws IOException {
            C14nXmlOutput.super.attribute(name,value);
        }

        DynamicAttribute toDynamicAttribute() {
            int nsUriIndex = name.nsUriIndex;
            int prefix;
            if(nsUriIndex==-1)
                prefix = -1;
            else
                prefix = nsUriIndex2prefixIndex[nsUriIndex];
            return new DynamicAttribute(
                prefix, name.localName, value );
        }

        @Override
        public int compareTo(StaticAttribute that) {
            return this.name.compareTo(that.name);
        }

    }

    final class DynamicAttribute implements Comparable<DynamicAttribute> {
        final int prefix;
        final String localName;
        final String value;

        public DynamicAttribute(int prefix, String localName, String value) {
            this.prefix = prefix;
            this.localName = localName;
            this.value = value;
        }

        private String getURI() {
            if(prefix==-1)  return "";
            else            return nsContext.getNamespaceURI(prefix);
        }

        @Override
        public int compareTo(DynamicAttribute that) {
            int r = this.getURI().compareTo(that.getURI());
            if(r!=0)    return r;
            return this.localName.compareTo(that.localName);
        }
    }

    @Override
    public void attribute(Name name, String value) throws IOException {
        if(staticAttributes.length==len) {
            // reallocate
            int newLen = len*2;
            StaticAttribute[] newbuf = new StaticAttribute[newLen];
            System.arraycopy(staticAttributes,0,newbuf,0,len);
            for(int i=len;i<newLen;i++) {
                newbuf[i] = new StaticAttribute();
            }
            staticAttributes = newbuf;
        }

        staticAttributes[len++].set(name,value);
    }

    @Override
    public void attribute(int prefix, String localName, String value) throws IOException {
        otherAttributes.add(new DynamicAttribute(prefix,localName,value));
    }

    @Override
    public void endStartTag() throws IOException {
        if(otherAttributes.isEmpty()) {
            if(len!=0) {
                // sort is expensive even for size 0 array,
                // so it's worth checking len==0
                if(!namedAttributesAreOrdered)
                    Arrays.sort(staticAttributes,0,len);
                // this is the common case
                for( int i=0; i<len; i++ )
                    staticAttributes[i].write();
                len = 0;
            }
        } else {
            // this is the exceptional case

            // sort all the attributes, not just the other attributes
            for( int i=0; i<len; i++ )
                otherAttributes.add(staticAttributes[i].toDynamicAttribute());
            len = 0;
            Collections.sort(otherAttributes);

            // write them all
            int size = otherAttributes.size();
            for( int i=0; i<size; i++ ) {
                DynamicAttribute a = otherAttributes.get(i);
                super.attribute(a.prefix,a.localName,a.value);
            }
            otherAttributes.clear();
        }
        super.endStartTag();
    }

    /**
     * Write namespace declarations after sorting them.
     */
    @Override
    protected void writeNsDecls(int base) throws IOException {
        int count = nsContext.getCurrent().count();

        if(count==0)
            return; // quickly reject the most common case

        if(count>nsBuf.length)
            nsBuf = new int[count];

        for( int i=count-1; i>=0; i-- )
            nsBuf[i] = base+i;

        // do a bubble sort. Hopefully # of ns decls are small enough to justify bubble sort.
        // faster algorithm is more compliated to implement
        for( int i=0; i<count; i++ ) {
            for( int j=i+1; j<count; j++ ) {
                String p = nsContext.getPrefix(nsBuf[i]);
                String q = nsContext.getPrefix(nsBuf[j]);
                if( p.compareTo(q) > 0 ) {
                    // swap
                    int t = nsBuf[j];
                    nsBuf[j] = nsBuf[i];
                    nsBuf[i] = t;
                }
            }
        }

        // write them out
        for( int i=0; i<count; i++ )
            writeNsDecl(nsBuf[i]);
    }
}
