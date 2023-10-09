/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.internalizer;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Formats error messages.
 */
class Messages
{
    /** Loads a string resource and formats it with specified arguments. */
    static String format( String property, Object... args ) {
        String text = ResourceBundle.getBundle(Messages.class.getPackage().getName() +".MessageBundle").getString(property);
        return MessageFormat.format(text,args);
    }
    
    static final String ERR_INCORRECT_SCHEMA_REFERENCE = // args:2
        "Internalizer.IncorrectSchemaReference";
    static final String ERR_XPATH_EVAL = // arg:1
        "Internalizer.XPathEvaluationError";
    static final String NO_XPATH_EVAL_TO_NO_TARGET = // arg:1
        "Internalizer.XPathEvaluatesToNoTarget";
    static final String NO_XPATH_EVAL_TOO_MANY_TARGETS = // arg:2
        "Internalizer.XPathEvaulatesToTooManyTargets";
    static final String NO_XPATH_EVAL_TO_NON_ELEMENT = // arg:1
        "Internalizer.XPathEvaluatesToNonElement";
    static final String XPATH_EVAL_TO_NON_SCHEMA_ELEMENT = // arg:2
        "Internalizer.XPathEvaluatesToNonSchemaElement";
    static final String SCD_NOT_ENABLED = // arg:0
        "SCD_NOT_ENABLED";
    static final String ERR_SCD_EVAL = // arg: 1
        "ERR_SCD_EVAL";
    static final String ERR_SCD_EVALUATED_EMPTY = // arg:1
        "ERR_SCD_EVALUATED_EMPTY";
    static final String ERR_SCD_MATCHED_MULTIPLE_NODES = // arg:2
        "ERR_SCD_MATCHED_MULTIPLE_NODES";
    static final String ERR_SCD_MATCHED_MULTIPLE_NODES_FIRST = // arg:1
        "ERR_SCD_MATCHED_MULTIPLE_NODES_FIRST";
    static final String ERR_SCD_MATCHED_MULTIPLE_NODES_SECOND = // arg:1
        "ERR_SCD_MATCHED_MULTIPLE_NODES_SECOND";
    static final String CONTEXT_NODE_IS_NOT_ELEMENT = // arg:0
        "Internalizer.ContextNodeIsNotElement";
    static final String ERR_INCORRECT_VERSION = // arg:0
        "Internalizer.IncorrectVersion";
    static final String ERR_VERSION_NOT_FOUND = // arg:0
        "Internalizer.VersionNotPresent";
    static final String TWO_VERSION_ATTRIBUTES = // arg:0
        "Internalizer.TwoVersionAttributes";
    static final String OLD_CUSTOMIZATION = // arg:0
        "Internalizer.OldCustomizationNS";
    static final String OLD_CUSTOMIZATION_VERSION = // arg:0
        "Internalizer.OldCustomizationVersion";
    static final String ORPHANED_CUSTOMIZATION = // arg:1
        "Internalizer.OrphanedCustomization";
    static final String ERR_UNABLE_TO_PARSE = // arg:2
        "AbstractReferenceFinderImpl.UnableToParse";
    static final String ERR_FILENAME_IS_NOT_URI = // arg:0
        "ERR_FILENAME_IS_NOT_URI";
    static final String ERR_GENERAL_SCHEMA_CORRECTNESS_ERROR = // arg:1
        "ERR_GENERAL_SCHEMA_CORRECTNESS_ERROR";
    static final String DOMFOREST_INPUTSOURCE_IOEXCEPTION = // arg:2
        "DOMFOREST_INPUTSOURCE_IOEXCEPTION";

}
