/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.util;

import com.sun.codemodel.JBlock;

/**
 * Holds a reference to a {@link JBlock} object.
 * 
 * <p>
 * This interface is usually used when one wants to create
 * a new JBlock object lazily.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface BlockReference {
    /**
     * @param create
     *      If false, the method will return null if the block is
     *      not yet created.  
     */
    JBlock get(boolean create);
}
