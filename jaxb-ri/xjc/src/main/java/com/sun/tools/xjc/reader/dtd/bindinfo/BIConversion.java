/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd.bindinfo;

import com.sun.tools.xjc.model.TypeUse;

/**
 * conversion declaration ({@code <conversion> and <enumeration>}).
 */
public interface BIConversion
{
    /** Gets the conversion name. */
    String name();
    
    /** Gets a transducer for this conversion. */
    TypeUse getTransducer();
}
