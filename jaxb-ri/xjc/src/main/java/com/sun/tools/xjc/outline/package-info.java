/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * Provides the outline of the generated Java source code so that
 * additional processing (such as adding more annotations) can be
 * done on the generated code.
 *
 * <p>
 * Code generation phase builds an outline little by little, while each step is using the outline built by the prior
 * steps.
 */
package com.sun.tools.xjc.outline;
