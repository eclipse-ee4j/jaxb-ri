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
 * Lazy block reference.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class LazyBlockReference implements BlockReference {
    
    private JBlock block = null;
    
    /**
     * Called when a block needs to be created.
     * Only called once in the whole life time of this object.
     */
    protected abstract JBlock create();
    
    public JBlock get(boolean create) {
        if(!create)     return block;
        if(block==null)
            block = create();
        return block;
    }

}
