/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.jxc.api;

import com.sun.tools.xjc.api.JavaCompiler;
import com.sun.tools.jxc.api.impl.j2s.JavaCompilerImpl;

/**
 * User: Iaroslav Savytskyi
 * Date: 25/05/12
 */
public class JXC {
    /**
     * Gets a fresh {@link JavaCompiler}.
     *
     * @return
     *      always return non-null object.
     */
    public static JavaCompiler createJavaCompiler() {
        return new JavaCompilerImpl();
    }
}
