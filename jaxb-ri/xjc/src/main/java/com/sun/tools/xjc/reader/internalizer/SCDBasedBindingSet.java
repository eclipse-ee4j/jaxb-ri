/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.internalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.xmlschema.AcknowledgePluginCustomizationBinder;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.validation.ValidatorHandler;

import com.sun.istack.NotNull;
import com.sun.istack.SAXParseException2;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.tools.xjc.util.ForkContentHandler;
import com.sun.tools.xjc.util.DOMUtils;

import com.sun.xml.xsom.SCD;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSSchemaSet;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Set of binding nodes that have target nodes specified via SCD.
 *
 * This is parsed during {@link Internalizer} works on the tree,
 * but applying this has to wait for {@link XSSchemaSet} to be parsed.
 *
 * @author Kohsuke Kawaguchi
 * @see SCD
 */
public final class SCDBasedBindingSet {

    /**
     * Represents the target schema component of the
     * customization identified by SCD.
     *
     * @author Kohsuke Kawaguchi
     */
    final class Target {
        /**
         * SCDs can be specified via multiple steps, like:
         *
         * <pre>{@code
         * <bindings scd="foo/bar">
         *   <bindings scd="zot/xyz">
         * }</pre>
         *
         * This field and {@link #nextSibling} form a single-linked list that
         * represent the children that shall be evaluated within this target.
         * Think of it as {@code List<Target>}.
         */
        private Target firstChild;
        private final Target nextSibling;

        /**
         * Compiled SCD.
         */
        private final @NotNull SCD scd;

        /**
         * The element on which SCD was found.
         */
        private final @NotNull Element src;

        /**
         * Bindings that apply to this SCD.
         */
        private final List<Element> bindings = new ArrayList<Element>();

        private Target(Target parent, Element src, SCD scd) {
            if(parent==null) {
                this.nextSibling = topLevel;
                topLevel = this;
            } else {
                this.nextSibling = parent.firstChild;
                parent.firstChild = this;
            }
            this.src = src;
            this.scd = scd;
        }

        /**
         * Adds a new binding declaration to be associated to the schema component
         * identified by {@link #scd}.
         */
        void addBinidng(Element binding) {
            bindings.add(binding);
        }

        /**
         * Applies bindings to the schema component for this and its siblings.
         */
        private void applyAll(Collection<? extends XSComponent> contextNode) {
            for( Target self=this; self!=null; self=self.nextSibling )
                self.apply(contextNode);
        }

        /**
         * Applies bindings to the schema component for just this node.
         */
        private void apply(Collection<? extends XSComponent> contextNode) {
            // apply the SCD...
            Collection<XSComponent> childNodes = scd.select(contextNode);
            if(childNodes.isEmpty()) {
                // no node matched
                if(src.getAttributeNode("if-exists")!=null) {
                    // if this attribute exists, it's not an error if SCD didn't match.
                    return;
                }

                reportError( src, Messages.format(Messages.ERR_SCD_EVALUATED_EMPTY,scd) );
                return;
            }

            if(firstChild!=null)
                    firstChild.applyAll(childNodes);

            if(!bindings.isEmpty()) {
                // error to match more than one components
                Iterator<XSComponent> itr = childNodes.iterator();
                XSComponent target = itr.next();
                if(itr.hasNext()) {
                    reportError( src, Messages.format(Messages.ERR_SCD_MATCHED_MULTIPLE_NODES,scd,childNodes.size()) );
                    errorReceiver.error( target.getLocator(), Messages.format(Messages.ERR_SCD_MATCHED_MULTIPLE_NODES_FIRST) );
                    errorReceiver.error( itr.next().getLocator(), Messages.format(Messages.ERR_SCD_MATCHED_MULTIPLE_NODES_SECOND) );
                }

                Ring old = Ring.begin();
                try {
                    // apply bindings to the target
                    for (Element binding : bindings) {
                        for (Element item : DOMUtils.getChildElements(binding)) {
                            String localName = item.getLocalName();

                            if ("bindings".equals(localName))
                                continue;   // this should be already in Target.bindings of some SpecVersion.

                            try {
                                new DOMForestScanner(forest).scan(item,loader);
                                BIDeclaration decl = (BIDeclaration)unmarshaller.getResult();

                                // add this binding to the target
                                XSAnnotation ann = target.getAnnotation(true);
                                BindInfo bi = (BindInfo)ann.getAnnotation();
                                if(bi==null) {
                                    bi = new BindInfo();
                                    ann.setAnnotation(bi);
                                }
                                bi.addDecl(decl);
                                if (src.getAttributeNode("auto-acknowledge") != null) {
                                    target.visit(Ring.get(AcknowledgePluginCustomizationBinder.class));
                                }
                            } catch (SAXException e) {
                                // the error should have already been reported.
                            } catch (JAXBException e) {
                                // if validation didn't fail, then unmarshalling can't go wrong
                                throw new AssertionError(e);
                            }
                        }
                    }
                } finally {
                    Ring.end(old);
                }

            }
        }
    }

    private Target topLevel;

    /**
     * The forest where binding elements came from. Needed to report line numbers for errors.
     */
    private final DOMForest forest;


    // variables used only during the apply method
    //
    private ErrorReceiver errorReceiver;
    private UnmarshallerHandler unmarshaller;
    private ForkContentHandler loader; // unmarshaller+validator

    SCDBasedBindingSet(DOMForest forest) {
        this.forest = forest;
    }

    Target createNewTarget(Target parent, Element src, SCD scd) {
        return new Target(parent,src,scd);
    }

    /**
     * Applies the additional binding customizations.
     */
    public void apply(XSSchemaSet schema, ErrorReceiver errorReceiver) {
        if(topLevel!=null) {
            this.errorReceiver = errorReceiver;
            Unmarshaller u =  BindInfo.getCustomizationUnmarshaller();
            this.unmarshaller = u.getUnmarshallerHandler();
            ValidatorHandler v = BindInfo.bindingFileSchema.newValidator();
            v.setErrorHandler(errorReceiver);
            loader = new ForkContentHandler(v,unmarshaller);

            topLevel.applyAll(schema.getSchemas());

            this.loader = null;
            this.unmarshaller = null;
            this.errorReceiver = null;
        }
    }

    private void reportError( Element errorSource, String formattedMsg ) {
        reportError( errorSource, formattedMsg, null );
    }

    private void reportError( Element errorSource,
                              String formattedMsg, Exception nestedException ) {

        SAXParseException e = new SAXParseException2( formattedMsg,
            forest.locatorTable.getStartLocation(errorSource),
            nestedException );
        errorReceiver.error(e);
    }
}
