/*
 * Copyright (c) 1997, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.addon.episode;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.EnumOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.reader.Const;
import org.glassfish.jaxb.core.v2.schemagen.episode.Bindings;
import org.glassfish.jaxb.core.v2.schemagen.episode.SchemaBindings;
import com.sun.xml.txw2.TXW;
import com.sun.xml.txw2.output.StreamSerializer;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.visitor.XSFunction;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates the episode file,
 *
 * @author Kohsuke Kawaguchi
 * @author Ben Tomasini (ben.tomasini@gmail.com)
 */
public class PluginImpl extends Plugin {

    private File episodeFile;

    @Override
    public String getOptionName() {
        return "episode";
    }

    @Override
    public String getUsage() {
        return "  -episode <FILE>     :  generate the episode file for separate compilation";
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
        if(args[i].equals("-episode")) {
            episodeFile = new File(opt.requireArgument("-episode",args,++i));
            return 2;
        }
        return 0;
    }

    /**
     * Capture all the generated classes from global schema components
     * and generate them in an episode file.
     */
    @Override
    public boolean run(Outline model, Options opt, ErrorHandler errorHandler) throws SAXException {
        OutputStream episodeFileOutputStream = null;
        try {
            // reorganize qualifying components by their namespaces to
            // generate the list nicely
            Map<XSSchema, PerSchemaOutlineAdaptors> perSchema = new LinkedHashMap<>();
            boolean hasComponentInNoNamespace = false;

            // Combine classes and enums into a single list
            List<OutlineAdaptor> outlines = new ArrayList<>();

            for (ClassOutline co : model.getClasses()) {
                XSComponent sc = co.target.getSchemaComponent();
                String fullName = co.implClass.fullName();
                String packageName = co.implClass.getPackage().name();
                OutlineAdaptor adaptor = new OutlineAdaptor(sc,
                        OutlineAdaptor.OutlineType.CLASS, fullName, packageName);
                outlines.add(adaptor);
            }

            for (EnumOutline eo : model.getEnums()) {
                XSComponent sc = eo.target.getSchemaComponent();
                String fullName = eo.clazz.fullName();
                String packageName = eo.clazz.getPackage().name();
                OutlineAdaptor adaptor = new OutlineAdaptor(sc,
                        OutlineAdaptor.OutlineType.ENUM, fullName, packageName);
                outlines.add(adaptor);
            }

            for (OutlineAdaptor oa : outlines) {
                XSComponent sc = oa.schemaComponent;

                if (sc == null) continue;
                if (!(sc instanceof XSDeclaration))
                    continue;
                XSDeclaration decl = (XSDeclaration) sc;
                if (decl.isLocal())
                    continue;   // local components cannot be referenced from outside, so no need to list.

                PerSchemaOutlineAdaptors list = perSchema.get(decl.getOwnerSchema());
                if (list == null) {
                    list = new PerSchemaOutlineAdaptors();
                    perSchema.put(decl.getOwnerSchema(), list);
                }

                list.add(oa);

                if (decl.getTargetNamespace().equals(""))
                    hasComponentInNoNamespace = true;
            }

            episodeFileOutputStream = new FileOutputStream(episodeFile);
            Bindings bindings = TXW.create(Bindings.class, new StreamSerializer(episodeFileOutputStream, "UTF-8"));
            if(hasComponentInNoNamespace) // otherwise jaxb binding NS should be the default namespace
                bindings._namespace(Const.JAXB_NSURI,"jaxb");
            else
                bindings._namespace(Const.JAXB_NSURI,"");
            bindings.version("3.0");
            if (!opt.noFileHeader) {
                bindings._comment("\n\n"+opt.getPrologComment()+"\n  ");
            }

            // generate listing per schema
            for (Map.Entry<XSSchema,PerSchemaOutlineAdaptors> e : perSchema.entrySet()) {
            	PerSchemaOutlineAdaptors ps = e.getValue();
                Bindings group = bindings.bindings();
                String tns = e.getKey().getTargetNamespace();
                if(!tns.equals(""))
                    group._namespace(tns,"tns");

                group.scd("x-schema::"+(tns.equals("")?"":"tns"));
                group._attribute("if-exists", true);
                group._attribute("auto-acknowledge", true);
                SchemaBindings schemaBindings = group.schemaBindings();
				schemaBindings.map(false);
                if (ps.packageNames.size() == 1) {
                    final String packageName = ps.packageNames.iterator().next();
                    if (packageName != null && packageName.length() > 0) {
						schemaBindings._package().name(packageName);
					}
				}

				for (OutlineAdaptor oa : ps.outlineAdaptors) {
                    Bindings child = group.bindings();
                    oa.buildBindings(child);
                }
                group.commit(true);
            }

            bindings.commit();

            return true;
        } catch (IOException e) {
            errorHandler.error(new SAXParseException("Failed to write to "+episodeFile,null,e));
            return false;
        } finally {
            if (episodeFileOutputStream != null) {
                try {
                    episodeFileOutputStream.close();
                } catch (IOException e) {
                    errorHandler.error(new SAXParseException("Failed to close file handle for "+episodeFile,null,e));
                }
            }
        }
    }

    /**
     * Computes SCD.
     * This is fairly limited as JAXB can only map a certain kind of components to classes.
     */
    private static final XSFunction<String> SCD = new XSFunction<>() {
        private String name(XSDeclaration decl) {
            if (decl.getTargetNamespace().equals(""))
                return decl.getName();
            else
                return "tns:" + decl.getName();
        }

        @Override
        public String complexType(XSComplexType type) {
            return "~" + name(type);
        }

        @Override
        public String simpleType(XSSimpleType simpleType) {
            return "~" + name(simpleType);
        }

        @Override
        public String elementDecl(XSElementDecl decl) {
            return name(decl);
        }

        // the rest is doing nothing
        @Override
        public String annotation(XSAnnotation ann) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String attGroupDecl(XSAttGroupDecl decl) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String attributeDecl(XSAttributeDecl decl) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String attributeUse(XSAttributeUse use) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String schema(XSSchema schema) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String facet(XSFacet facet) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String notation(XSNotation notation) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String identityConstraint(XSIdentityConstraint decl) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String xpath(XSXPath xpath) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String particle(XSParticle particle) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String empty(XSContentType empty) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String wildcard(XSWildcard wc) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String modelGroupDecl(XSModelGroupDecl decl) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String modelGroup(XSModelGroup group) {
            throw new UnsupportedOperationException();
        }
    };

    private final static class OutlineAdaptor {

        private enum OutlineType {

            CLASS(new BindingsBuilder() {
                @Override
                public void build(OutlineAdaptor adaptor, Bindings bindings) {
                    bindings.klass().ref(adaptor.implName);
                }
            }),
            ENUM(new BindingsBuilder() {
                @Override
                public void build(OutlineAdaptor adaptor, Bindings bindings) {
                    bindings.typesafeEnumClass().ref(adaptor.implName);
                }
            });

            private final BindingsBuilder bindingsBuilder;

            OutlineType(BindingsBuilder bindingsBuilder) {
                this.bindingsBuilder = bindingsBuilder;
            }

            private interface BindingsBuilder {
                void build(OutlineAdaptor adaptor, Bindings bindings);
            }

        }

        private final XSComponent schemaComponent;
        private final OutlineType outlineType;
        private final String implName;
        private final String packageName;

        public OutlineAdaptor(XSComponent schemaComponent, OutlineType outlineType,
                              String implName, String packageName) {
            this.schemaComponent = schemaComponent;
            this.outlineType = outlineType;
            this.implName = implName;
            this.packageName = packageName;
        }

        private void buildBindings(Bindings bindings) {
            bindings.scd(schemaComponent.apply(SCD));
            bindings._attribute("if-exists", true);
            bindings._attribute("auto-acknowledge", true);
            outlineType.bindingsBuilder.build(this, bindings);
        }
    }
    
    private final static class PerSchemaOutlineAdaptors {
    	
    	private final List<OutlineAdaptor> outlineAdaptors = new ArrayList<>();
    	
    	private final Set<String> packageNames = new HashSet<>();

        private void add(OutlineAdaptor outlineAdaptor) {
            this.outlineAdaptors.add(outlineAdaptor);
            this.packageNames.add(outlineAdaptor.packageName);
    	}
    	
    }
}
