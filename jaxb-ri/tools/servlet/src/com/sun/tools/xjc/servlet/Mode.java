/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet;

import java.util.ResourceBundle;

/**
 * Defines the various constants the dictates the behavior of the servlet.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Mode {
    
    /**
     * True if this is a part of the JWSDP example.
     */
    public static boolean inJWSDP; // TODO: look up this value from somewhere
    
    static {
        String mode = ResourceBundle.getBundle(Mode.class.getName()).getString("mode");
        inJWSDP = mode.equals("jwsdp");
    }

    /**
     * True to enable featuers that use disk space.
     */
    public static boolean canUseDisk = true;
    
    /**
     * Usage notification e-mail will be sent to this address.
     */
    public static String homeAddress = "jaxb-dev@sun.com";
    
    /**
     * SMTP server that can receive e-mails to the above address.
     */
    public static String mailServer = "mail.sun.net";
}
