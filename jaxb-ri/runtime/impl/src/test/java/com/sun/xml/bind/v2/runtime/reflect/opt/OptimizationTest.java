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

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Test flag for disabling optimization
 * com.sun.xml.bind.v2.runtime.reflect.opt.OptimizedAccessorFactory.noOptimization=true/false
 *
 * @author Daniel Kec
 */
public class OptimizationTest {

    private static final String EXPECTED_ACCESSOR_CLASS_REF = Book.class.getName() + "$JaxbAccessorM_getAuthor_setAuthor_java_lang_String";
    private static final String OPTIMIZATION_ON = "AUTHOR_EXPECTING_OPTIMIZATION_ON";
    private static final String OPTIMIZATION_OFF = "AUTHOR_EXPECTING_OPTIMIZATION_OFF";

    @Test
    public void checkNoOptimizationFlagFalse() throws JAXBException {
        Book book = unmarshal(OPTIMIZATION_ON);
        Assert.assertTrue(book.errorMessage, book.errorMessage == null);
        Assert.assertEquals(OPTIMIZATION_ON, book.getAuthor());
    }

    @Test
    public void checkNoOptimizationFlagTrue() throws JAXBException, NoSuchFieldException, IllegalAccessException {
        try {
            setNoOptimizationFlag(true);
            Book book = unmarshal(OPTIMIZATION_OFF);
            Assert.assertTrue(book.errorMessage, book.errorMessage == null);
            Assert.assertEquals(OPTIMIZATION_OFF, book.getAuthor());
        } finally {
            setNoOptimizationFlag(false);
        }
    }

    private Book unmarshal(String autor) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Book.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Book) unmarshaller.unmarshal(new StringReader(
                String.format("<book><author>%s</author></book>", autor)));
    }

    private void setNoOptimizationFlag(boolean flag) throws NoSuchFieldException, IllegalAccessException {
        Field noOptimizationField = OptimizedAccessorFactory.class.getField("noOptimization");
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        noOptimizationField.setAccessible(true);
        modifiersField.setInt(noOptimizationField, noOptimizationField.getModifiers() & ~Modifier.FINAL);
        noOptimizationField.setBoolean(OptimizedAccessorFactory.class, true);
    }

    @XmlRootElement
    static class Book {
        private String author;

        @XmlTransient
        String errorMessage = null;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            String artificialInnerClass = null;
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement el : stackTrace) {
                if (el.getClassName().equals(EXPECTED_ACCESSOR_CLASS_REF)) {
                    artificialInnerClass = el.getClassName();
                    break;
                }
            }

            if (author.equals(OPTIMIZATION_OFF) && artificialInnerClass != null) {
                this.errorMessage = String.format("Artificial accessor class detected %s", artificialInnerClass);
            } else if (author.equals(OPTIMIZATION_ON) && artificialInnerClass == null) {
                this.errorMessage = String.format("Expected artificial accessor class not found: %s", EXPECTED_ACCESSOR_CLASS_REF);
            }
            this.author = author;
        }
    }
}
