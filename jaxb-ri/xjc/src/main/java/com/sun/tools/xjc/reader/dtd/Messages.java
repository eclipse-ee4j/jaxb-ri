/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.dtd;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
class Messages
{
    /** Loads a string resource and formats it with specified arguments. */
    static String format( String property, Object... args ) {
        String text = ResourceBundle.getBundle(Messages.class.getPackage().getName() + ".MessageBundle").getString(property);
        return MessageFormat.format(text,args);
    }
    

    public static final String ERR_NO_ROOT_ELEMENT = // arg:0
        "TDTDReader.NoRootElement";

    public static final String ERR_UNDEFINED_ELEMENT_IN_BINDINFO = // arg:1
        "TDTDReader.UndefinedElementInBindInfo";

    public static final String ERR_CONVERSION_FOR_NON_VALUE_ELEMENT = // arg:1
        "TDTDReader.ConversionForNonValueElement";

    public static final String ERR_CONTENT_PROPERTY_PARTICLE_MISMATCH = // arg:1
        "TDTDReader.ContentProperty.ParticleMismatch";

    public static final String ERR_CONTENT_PROPERTY_DECLARATION_TOO_SHORT = // arg:1
        "TDTDReader.ContentProperty.DeclarationTooShort";
    
    public static final String ERR_BINDINFO_NON_EXISTENT_ELEMENT_DECLARATION = // arg:1
        "TDTDReader.BindInfo.NonExistentElementDeclaration";

    public static final String ERR_BINDINFO_NON_EXISTENT_INTERFACE_MEMBER = // arg:1
        "TDTDReader.BindInfo.NonExistentInterfaceMember";
        
}
