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
 * JContinue statement
 */
class JContinue implements JStatement {
    
    private final JLabel label;
    
    /**
     * JContinue constructor.
     * 
     * @param _label
     *      a valid label or null.
     */
    JContinue(JLabel _label) {
        this.label = _label;
    }

    public void state(JFormatter f) {
        if( label==null )
            f.p("continue;").nl();
        else
            f.p("continue").p(label.label).p(';').nl();
    }

}
