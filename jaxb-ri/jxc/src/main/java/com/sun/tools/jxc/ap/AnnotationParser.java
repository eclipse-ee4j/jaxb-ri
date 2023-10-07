/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.ap;

import com.sun.tools.jxc.ConfigReader;
import com.sun.tools.jxc.api.JXC;
import com.sun.tools.xjc.ErrorReceiver;
import com.sun.tools.jxc.api.J2SJAXBModel;
import com.sun.tools.jxc.api.Reference;
import org.xml.sax.SAXException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import jakarta.xml.bind.SchemaOutputResolver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class behaves as a JAXB Annotation Processor,
 * It reads the user specified typeDeclarations
 * and the config files
 * It also reads config files
 *
 * Used in unit tests
 *
 * @author Bhakti Mehta (bhakti.mehta@sun.com)
 */
@SupportedAnnotationTypes("jakarta.xml.bind.annotation.*")
@SupportedOptions("jaxb.config")
public final class AnnotationParser extends AbstractProcessor {

    private ErrorReceiver errorListener;

    /**
     * Default constructor.
     */
    public AnnotationParser() {}

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        errorListener = new ErrorReceiverImpl(
                processingEnv.getMessager(),
                processingEnv.getOptions().containsKey(Const.DEBUG_OPTION.getValue())
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processingEnv.getOptions().containsKey(Const.CONFIG_FILE_OPTION.getValue())) {
            String value = processingEnv.getOptions().get(Const.CONFIG_FILE_OPTION.getValue());

            // For multiple config files we are following the format
            // -Aconfig=foo.config:bar.config where : is the pathSeparatorChar
            StringTokenizer st = new StringTokenizer(value, File.pathSeparator);
            if (!st.hasMoreTokens()) {
                errorListener.error(null, Messages.OPERAND_MISSING.format(Const.CONFIG_FILE_OPTION.getValue()));
                return true;
            }

            while (st.hasMoreTokens()) {
                File configFile = new File(st.nextToken());
                if (!configFile.exists()) {
                    errorListener.error(null, Messages.NON_EXISTENT_FILE.format());
                    continue;
                }

                try {
                    Collection<TypeElement> rootElements = new ArrayList<>();
                    filterClass(rootElements, roundEnv.getRootElements());
                    ConfigReader configReader = new ConfigReader(
                            processingEnv,
                            rootElements,
                            configFile,
                            errorListener
                    );

                    Collection<Reference> classesToBeIncluded = configReader.getClassesToBeIncluded();
                    J2SJAXBModel model = JXC.createJavaCompiler().bind(
                            classesToBeIncluded, Collections.emptyMap(), processingEnv, null);

                    SchemaOutputResolver schemaOutputResolver = configReader.getSchemaOutputResolver();

                    model.generateSchema(schemaOutputResolver, errorListener);
                } catch (IOException e) {
                    errorListener.error(e.getMessage(), e);
                } catch (SAXException e) {
                    // the error should have already been reported
                }
            }
        }
        return true;
    }

    private void filterClass(Collection<TypeElement> rootElements, Collection<? extends Element> elements) {
        for (Element element : elements) {
            if (element.getKind().equals(ElementKind.CLASS) || element.getKind().equals(ElementKind.INTERFACE) ||
                    element.getKind().equals(ElementKind.ENUM)) {
                rootElements.add((TypeElement) element);
                filterClass(rootElements, ElementFilter.typesIn(element.getEnclosedElements()));
            }
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
