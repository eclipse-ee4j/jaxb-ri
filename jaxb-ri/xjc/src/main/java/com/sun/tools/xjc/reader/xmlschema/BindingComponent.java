/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import com.sun.tools.xjc.reader.Ring;

/**
 * Component accessible from {@link Ring}.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class BindingComponent {
    protected BindingComponent() {
        Ring.add(this);
    }

//
//
// Accessor to common components.
//
//

    protected final ErrorReporter getErrorReporter() {
        return Ring.get(ErrorReporter.class);
    }
    protected final ClassSelector getClassSelector() {
        return Ring.get(ClassSelector.class);
    }
}
