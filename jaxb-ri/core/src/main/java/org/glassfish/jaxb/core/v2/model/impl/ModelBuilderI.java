/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.core.v2.model.impl;

import org.glassfish.jaxb.core.v2.model.annotation.AnnotationReader;
import org.glassfish.jaxb.core.v2.model.nav.Navigator;

/**
 * User: Iaroslav Savytskyi
 * Date: 24/05/12
 */
public interface ModelBuilderI<T,C,F,M> {

    Navigator<T,C,F,M> getNavigator();

    AnnotationReader<T,C,F,M> getReader();
}
