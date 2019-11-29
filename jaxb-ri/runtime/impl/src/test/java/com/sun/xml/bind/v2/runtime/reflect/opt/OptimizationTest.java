/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
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
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
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
    private static final VarHandle MODIFIERS;

    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }

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
        noOptimizationField.setAccessible(true);
        if (Modifier.isFinal(noOptimizationField.getModifiers())) {
            MODIFIERS.set(noOptimizationField, noOptimizationField.getModifiers() & ~Modifier.FINAL);
        }
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
