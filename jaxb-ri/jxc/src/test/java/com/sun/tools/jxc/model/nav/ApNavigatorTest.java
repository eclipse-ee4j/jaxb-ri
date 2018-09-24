/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.model.nav;

import java.util.Arrays;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author aefimov
 */
@RunWith(JMockit.class)
public final class ApNavigatorTest {

    @Test
    public void testgetEnumConstantsOrder(
            @Mocked final ProcessingEnvironment env,
            @Mocked final TypeElement clazz,
            @Mocked final VariableElement enumElement1,
            @Mocked final VariableElement enumElement2,
            @Mocked final VariableElement enumElement3,
            @Mocked final VariableElement enumElement4 ) throws Exception {

        new Expectations() {
            {
                //The primitiveType is irrelevant for getEnumConstants() operations
                env.getTypeUtils().getPrimitiveType(TypeKind.BYTE); result = (PrimitiveType) null;
                //enumElements needs to return ENUM_CONSTANT for getEnumConstants() to work properly
                enumElement1.getKind(); result = ElementKind.ENUM_CONSTANT;
                enumElement2.getKind(); result = ElementKind.ENUM_CONSTANT;
                enumElement3.getKind(); result = ElementKind.ENUM_CONSTANT;
                enumElement4.getKind(); result = ElementKind.ENUM_CONSTANT;
                //Redefine the hashCode() for test enum's - it will gives us an assurance that
                // all elements will have predifined order when they will be added to incorrect
                // HashSet container in getEnumConstants()
                enumElement1.hashCode(); result = 4;
                enumElement2.hashCode(); result = 3;
                enumElement3.hashCode(); result = 2;
                enumElement4.hashCode(); result = 1;
                //We want to return here a predifined clazz members
                env.getElementUtils().getAllMembers(clazz); result = Arrays.asList(enumElement1,
                                                                         enumElement2, enumElement3, enumElement4);
            }
        };
        //Create ApNavigator instance
        ApNavigator apn = new ApNavigator(env);
        //Filter the enum constants
        VariableElement[] resArr = apn.getEnumConstants(clazz);
        //Check that order is preserved after the getEnumConstants() operation
        assertTrue("Position of first element is changed", resArr[0] == enumElement1);
        assertTrue("Position of second element is changed", resArr[1] == enumElement2);
        assertTrue("Position of third element is changed", resArr[2] == enumElement3);
        assertTrue("Position of fourth element is changed", resArr[3] == enumElement4);

    }
}
