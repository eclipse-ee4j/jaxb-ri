/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.outline;

import com.sun.codemodel.JDefinedClass;
import com.sun.istack.NotNull;
import com.sun.tools.xjc.model.CCustomizable;

/**
 * This interface describes that outline class could be customized.
 * It provides the bound info from {@link CCustomizable} target. And
 * customization output - implementation class.
 *
 * @author yaroska
 * @since 2.2.12
 */
public interface CustomizableOutline {

    /**
     * Provides bound information about customizable target.
     * @return customizable target
     */
    @NotNull CCustomizable getTarget();

    /**
     * Provides customization output.
     * @return Implementation class
     */
    @NotNull JDefinedClass getImplClass();
}
