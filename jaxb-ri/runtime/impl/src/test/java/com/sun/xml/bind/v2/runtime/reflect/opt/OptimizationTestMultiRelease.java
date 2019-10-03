/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.sun.xml.bind.v2.runtime.reflect.opt;

import com.sun.xml.bind.test.AbstractTestMultiRelease;
import com.sun.xml.bind.test.SinceJava9;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

/**
 * Test multi release for optimization feature,
 * Feature needs to be disabled since java 9.
 *
 * @author Daniel Kec
 */
public class OptimizationTestMultiRelease extends AbstractTestMultiRelease {

    @SuppressWarnings("WeakerAccess")
    public static String TEST_FIELD = "TEST_FIELD";

    @Test
    @SinceJava9
    public void testStubbedOptimizedAccessorFactory() throws NoSuchFieldException {
        Field field = this.getClass().getDeclaredField(TEST_FIELD);
        Assert.assertNull("OptimizedAccessorFactory should always return null since Java 9", OptimizedAccessorFactory.get(field));
    }

    @Test
    @SinceJava9
    @SuppressWarnings("ConstantConditions")
    public void testStubbedOptimizedTransducedAccessorFactory() throws NoSuchFieldException {
        final String errMessage = "OptimizedTransducedAccessorFactory should always return null since Java 9";
        try {
            //Stubbed version doesn't mind null param
            TransducedAccessor accessor = OptimizedTransducedAccessorFactory.get((RuntimePropertyInfo) null);
            Assert.assertNull(errMessage, accessor);
        } catch (NullPointerException npe) {
            Assert.fail(errMessage);
        }
    }

}
