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
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class ExistingBlockReference implements BlockReference {
    private final JBlock block;
    
    public ExistingBlockReference( JBlock _block ) {
        this.block = _block;
    }
    
    public JBlock get(boolean create) {
        return block;
    }

}
