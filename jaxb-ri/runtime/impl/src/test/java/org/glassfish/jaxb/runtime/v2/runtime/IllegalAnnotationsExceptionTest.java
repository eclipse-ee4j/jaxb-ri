/*
 * Copyright (c) 2025, 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package org.glassfish.jaxb.runtime.v2.runtime;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class IllegalAnnotationsExceptionTest {
    @Test
    public void test() {
        try {
            JAXBContext.newInstance(IllegalAnnotationsExceptionDTO.class);
            fail("no exception thrown");
        } catch (IllegalAnnotationsException e) {
            // actual message (x counts of IllegalAnnotationExceptions)
            assertTrue(e.getMessage().contains("1 counts of IllegalAnnotationExceptions"));
            // new detailed message containing location of exception if present (here it is)
            assertTrue(e.getMessage().contains("this problem is related to the following location:"));
        } catch (JAXBException e) {
            fail("unexpected JAXBException");
        }
    }
}
