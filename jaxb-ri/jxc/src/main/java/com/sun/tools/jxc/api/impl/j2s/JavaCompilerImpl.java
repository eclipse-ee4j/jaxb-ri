/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.api.impl.j2s;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlList;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Messager;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import com.sun.tools.jxc.ap.InlineAnnotationReaderImpl;
import com.sun.tools.jxc.api.J2SJAXBModel;
import com.sun.tools.jxc.api.JavaCompiler;
import com.sun.tools.jxc.api.Reference;
import com.sun.tools.jxc.model.nav.ApNavigator;
import org.glassfish.jaxb.core.v2.model.core.ErrorHandler;
import org.glassfish.jaxb.core.v2.model.core.Ref;
import org.glassfish.jaxb.core.v2.model.core.TypeInfoSet;
import org.glassfish.jaxb.runtime.v2.model.impl.ModelBuilder;
import org.glassfish.jaxb.core.v2.runtime.IllegalAnnotationException;

/**
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class JavaCompilerImpl implements JavaCompiler {
    @Override
    public J2SJAXBModel bind(
        Collection<Reference> rootClasses,
        Map<QName, Reference> additionalElementDecls,
        ProcessingEnvironment env,
        String defaultNamespaceRemap) {

        ModelBuilder<TypeMirror, TypeElement, VariableElement, ExecutableElement> builder =
                new ModelBuilder<>(
                        InlineAnnotationReaderImpl.theInstance,
                        new ApNavigator(env),
                        Collections.emptyMap(),
                        defaultNamespaceRemap);

        builder.setErrorHandler(new ErrorHandlerImpl(env.getMessager()));

        for ( Reference ref : rootClasses ) {
            TypeMirror t = ref.type;

            XmlJavaTypeAdapter xjta = ref.annotations.getAnnotation(XmlJavaTypeAdapter.class);
            XmlList xl = ref.annotations.getAnnotation(XmlList.class);

            builder.getTypeInfo(new Ref<>(builder, t, xjta, xl));
        }

        TypeInfoSet<TypeMirror, TypeElement, VariableElement, ExecutableElement> r = builder.link();
        if(r==null)     return null;

        if(additionalElementDecls==null)
            additionalElementDecls = Collections.emptyMap();
        else {
            // fool proof check
            for (Map.Entry<QName, Reference> e : additionalElementDecls.entrySet()) {
                if(e.getKey()==null)
                    throw new IllegalArgumentException("nulls in additionalElementDecls");
            }
        }
        return new JAXBModelImpl(r, builder.reader, rootClasses, new HashMap<>(additionalElementDecls));
    }

    private static final class ErrorHandlerImpl implements ErrorHandler {
        private final Messager messager;

        public ErrorHandlerImpl(Messager messager) {
            this.messager = messager;
        }

        @Override
        public void error(IllegalAnnotationException e) {
            String error = e.toString();
            messager.printMessage(Diagnostic.Kind.ERROR, error);
            System.err.println(error); //TODO: temporary fix problem with no ouput from messager
        }
    }
}
