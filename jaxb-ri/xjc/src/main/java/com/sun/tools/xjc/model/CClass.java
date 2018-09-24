/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.model;

/**
 * Either {@link CClassInfo} or {@link CClassRef}.
 *
 * @author Kohsuke Kawaguchi
 */
public interface CClass extends CNonElement, CElement {
    // how can anything be CNonElement and CElement at the same time, you may ask.
    
}
