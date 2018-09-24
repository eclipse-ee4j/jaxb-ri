/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 * Use is subject to the license terms.
 */
package com.sun.tools.xjc;


/**
 * Signals the abortion of the compilation.
 * <p>
 * This exception should be only thrown from {@link ErrorReceiver}
 * for the consistent error handling.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class AbortException extends RuntimeException {
    public AbortException() {
    }
}
    
