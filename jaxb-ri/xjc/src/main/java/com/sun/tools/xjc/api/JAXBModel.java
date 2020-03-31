/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.api;

import java.util.List;

import jakarta.xml.bind.JAXBContext;

/**
 * The in-memory representation of the JAXB binding.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface JAXBModel {

    /**
     * Returns a list of fully-qualified class names, which should
     * be used at the runtime to create a new {@link JAXBContext}.
     *
     * <p>
     * Until the JAXB team fixes the bootstrapping issue, we have
     * two bootstrapping methods. This one is to use a list of class names
     * to call {@link JAXBContext#newInstance(Class[])} method. If
     * this method returns non-null, the caller is expected to use
     * that method. <b>This is meant to be a temporary workaround.</b>
     *
     * @return
     *      non-null read-only list.
     *
     * @deprecated
     *      this method is provided for now to allow gradual migration for JAX-RPC.
     */
    List<String> getClassList();

}
