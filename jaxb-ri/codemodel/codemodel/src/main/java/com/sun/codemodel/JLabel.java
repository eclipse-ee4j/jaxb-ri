/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

/**
 * Label that can be used for continue and break.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JLabel implements JStatement {
    
    final String label;
    
    /**
     * JBreak constructor
     * 
     * @param   _label
     *      break label or null.
     */
    JLabel( String _label ) {
        this.label = _label;
    }

    public void state(JFormatter f) {
        f.p(label+':').nl();
    }

}
