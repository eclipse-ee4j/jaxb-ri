/*
 * Copyright (c) 1997, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.util;

/**
 * Created to record the caller stack trace in logging.
 *
 * @author Kohsuke Kawaguchi
 */
public class StackRecorder extends Throwable {
    private static final long serialVersionUID = 1296878485146023581L;

    /**
     * Default constructor.
     */
    public StackRecorder() {}

}
