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
 * Marker interface for code components that can be placed to
 * the left of '=' in an assignment.
 * 
 * A left hand value can always be a right hand value, so
 * this interface derives from {@link JExpression}. 
 */
public interface JAssignmentTarget extends JGenerable, JExpression {
    JExpression assign(JExpression rhs);
    JExpression assignPlus(JExpression rhs);
}
