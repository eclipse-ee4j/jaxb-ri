/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.reflect;

import org.glassfish.jaxb.core.WhiteSpaceProcessor;
import org.glassfish.jaxb.runtime.api.AccessorException;
import org.glassfish.jaxb.runtime.v2.runtime.Transducer;
import org.glassfish.jaxb.runtime.v2.runtime.XMLSerializer;
import jakarta.xml.bind.JAXBException;
import org.xml.sax.SAXException;

/**
 * {@link TransducedAccessor} for a list simple type.
 *
 * @author Kohsuke Kawaguchi
 */
public final class ListTransducedAccessorImpl<BeanT,ListT,ItemT,PackT> extends DefaultTransducedAccessor<BeanT> {
    /**
     * {@link Transducer} for each item type.
     */
    private final Transducer<ItemT> xducer;
    /**
     * {@link Lister} for handling list of tokens.
     */
    private final Lister<BeanT,ListT,ItemT,PackT> lister;
    /**
     * {@link Accessor} to get/set the list. 
     */
    private final Accessor<BeanT,ListT> acc;

    public ListTransducedAccessorImpl(Transducer<ItemT> xducer, Accessor<BeanT,ListT> acc, Lister<BeanT,ListT,ItemT,PackT> lister) {
        this.xducer = xducer;
        this.lister = lister;
        this.acc = acc;
    }

    @Override
    public boolean useNamespace() {
        return xducer.useNamespace();
    }

    @Override
    public void declareNamespace(BeanT bean, XMLSerializer w) throws AccessorException, SAXException {
        ListT list = acc.get(bean);

        if(list!=null) {
           ListIterator<ItemT> itr = lister.iterator(list, w);

            while(itr.hasNext()) {
                try {
                    ItemT item = itr.next();
                    if (item != null) {
                        xducer.declareNamespace(item,w);
                    }
                } catch (JAXBException e) {
                    w.reportError(null,e);
                }
            }
        }
    }

    // TODO: this is inefficient, consider a redesign
    // perhaps we should directly write to XMLSerializer,
    // or maybe add more methods like writeLeafElement.
    @Override
    public String print(BeanT o) throws AccessorException, SAXException {
        ListT list = acc.get(o);

        if(list==null)
            return null;

        StringBuilder buf = new StringBuilder();
        XMLSerializer w = XMLSerializer.getInstance();
        ListIterator<ItemT> itr = lister.iterator(list, w);

        while(itr.hasNext()) {
            try {
                ItemT item = itr.next();
                if (item != null) {
                    if(buf.length()>0)  buf.append(' ');
                    buf.append(xducer.print(item));
                }
            } catch (JAXBException e) {
                w.reportError(null,e);
            }
        }
        return buf.toString();
    }

    private void processValue(BeanT bean, CharSequence s) throws AccessorException, SAXException {
        PackT pack = lister.startPacking(bean,acc);

        int idx = 0;
        int len = s.length();

        while(true) {
            int p = idx;
            while( p<len && !WhiteSpaceProcessor.isWhiteSpace(s.charAt(p)) )
                p++;

            CharSequence token = s.subSequence(idx,p);
            if (!token.equals(""))
                lister.addToPack(pack,xducer.parse(token));

            if(p==len)      break;  // done

            while( p<len && WhiteSpaceProcessor.isWhiteSpace(s.charAt(p)) )
                p++;
            if(p==len)      break;  // done

            idx = p;
        }

        lister.endPacking(pack,bean,acc);
    }

    @Override
    public void parse(BeanT bean, CharSequence lexical) throws AccessorException, SAXException {
        processValue(bean,lexical);
    }

    @Override
    public boolean hasValue(BeanT bean) throws AccessorException {
        return acc.get(bean)!=null;
    }
}
