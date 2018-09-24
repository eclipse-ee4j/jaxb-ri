/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.ap;

import com.sun.tools.xjc.api.Reference;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static mockit.Deencapsulation.invoke;
import static org.junit.Assert.assertTrue;

/**
 * @author yaroska
 */

@RunWith(JMockit.class)
public final class SchemaGeneratorTest {

    @Test
    public void filterClassTest(
            @Mocked Reference ref, // needed for filing of the result's list
            @Mocked final TypeElement interfaceElement, @Mocked final TypeElement enumElement,
            @Mocked final TypeElement classElement, @Mocked final TypeElement nestedClassElement,
            @Mocked final TypeElement nestedEnumElement) throws Exception {

        new Expectations() {{
            interfaceElement.getKind(); result = ElementKind.INTERFACE; // this one will be ignored
            enumElement.getKind(); result = ElementKind.ENUM;
            nestedEnumElement.getKind(); result = ElementKind.ENUM;
            classElement.getKind(); result = ElementKind.CLASS;
            nestedClassElement.getKind(); result = ElementKind.CLASS;

            // these two let's have enclosed elements
            enumElement.getEnclosedElements(); result = Arrays.asList(interfaceElement, nestedClassElement);
            classElement.getEnclosedElements(); result = Arrays.asList(nestedEnumElement, interfaceElement);
        }};

        List<Reference> result = new ArrayList<Reference>();
        SchemaGenerator sg = new SchemaGenerator();

        Collection<TypeElement> elements = Collections.singletonList(interfaceElement);
        invoke(sg, "filterClass", result, elements);
        assertTrue("Expected no root types to be found. But found: " + result.size(), result.isEmpty());

        elements = Arrays.asList(interfaceElement, enumElement, classElement);
        invoke(sg, "filterClass", result, elements);
        assertTrue("Expected 4 root types to be found. But found: " + result.size(), result.size() == 4);

        new Verifications() {{
            interfaceElement.getEnclosedElements(); maxTimes = 0;
            nestedClassElement.getEnclosedElements(); maxTimes = 1;
            nestedEnumElement.getEnclosedElements(); maxTimes = 1;
        }};
    }
}
