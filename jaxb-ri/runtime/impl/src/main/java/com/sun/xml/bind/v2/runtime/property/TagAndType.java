/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.xml.bind.v2.runtime.property;

import javax.xml.namespace.QName;

import com.sun.xml.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.bind.v2.runtime.Name;

/**
 * Pair of {@link QName} and {@link JaxBeanInfo}.
 * 
 * @author Kohsuke Kawaguchi
 */
class TagAndType {
    final Name tagName;
    final JaxBeanInfo beanInfo;
    TagAndType(Name tagName, JaxBeanInfo beanInfo) {
        this.tagName = tagName;
        this.beanInfo = beanInfo;
    }
}
