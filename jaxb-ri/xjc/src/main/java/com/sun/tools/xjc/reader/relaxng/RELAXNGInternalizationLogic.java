/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.relaxng;

import com.sun.tools.xjc.reader.Const;
import com.sun.tools.xjc.reader.internalizer.AbstractReferenceFinderImpl;
import com.sun.tools.xjc.reader.internalizer.DOMForest;
import com.sun.tools.xjc.reader.internalizer.InternalizationLogic;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * RELAX NG specific internalization logic.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class RELAXNGInternalizationLogic implements InternalizationLogic {

    /**
     * This filter looks for {@code <xs:import>} and {@code <xs:include>}
     * and parses those documents referenced by them.
     */
    private static final class ReferenceFinder extends AbstractReferenceFinderImpl {
        ReferenceFinder( DOMForest parent ) {
            super(parent);
        }

        protected String findExternalResource( String nsURI, String localName, Attributes atts) {
            if( Const.RELAXNG_URI.equals(nsURI)
            && ("include".equals(localName) || "externalRef".equals(localName) ) )
                return atts.getValue("href");
            else
                return null;
        }
    };

    public XMLFilterImpl createExternalReferenceFinder(DOMForest parent) {
        return new ReferenceFinder(parent);
    }

    public boolean checkIfValidTargetNode(DOMForest parent, Element bindings, Element target) {
        return Const.RELAXNG_URI.equals(target.getNamespaceURI());
    }

    public Element refineTarget(Element target) {
        // no refinement necessary
        return target;
    }

}
