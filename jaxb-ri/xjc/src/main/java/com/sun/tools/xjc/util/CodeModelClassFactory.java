/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.util;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JJavaName;
import com.sun.codemodel.JMod;
import com.sun.tools.xjc.ErrorReceiver;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * Create new {@link JDefinedClass} and report class collision errors,
 * if necessary.
 * 
 * This is just a helper class that simplifies the class name collision
 * detection. This object maintains no state, so it is OK to use
 * multiple instances of this.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class CodeModelClassFactory {
    
    /** errors are reported to this object. */
    private ErrorReceiver errorReceiver;
    
    /** unique id generator. */
    private int ticketMaster = 0;
    
    
    public CodeModelClassFactory( ErrorReceiver _errorReceiver ) {
        this.errorReceiver = _errorReceiver;
    }
    
    public JDefinedClass createClass( JClassContainer parent, String name, Locator source ) {
        return createClass( parent, JMod.PUBLIC, name, source );
    }
    public JDefinedClass createClass( JClassContainer parent, int mod, String name, Locator source ) {
        return createClass(parent,mod,name,source,ClassType.CLASS);
    }
        
    public JDefinedClass createInterface( JClassContainer parent, String name, Locator source ) {
        return createInterface( parent, JMod.PUBLIC, name, source );
    }
    public JDefinedClass createInterface( JClassContainer parent, int mod, String name, Locator source ) {
        return createClass(parent,mod,name,source,ClassType.INTERFACE);
    }
    public JDefinedClass createClass(
        JClassContainer parent, String name, Locator source, ClassType kind ) {
        return createClass(parent,JMod.PUBLIC,name,source,kind);
    }
    public JDefinedClass createClass(
        JClassContainer parent, int mod, String name, Locator source, ClassType kind ) {

        if(!JJavaName.isJavaIdentifier(name)) {
            // report the error
            errorReceiver.error( new SAXParseException(
                Messages.format( Messages.ERR_INVALID_CLASSNAME, name ), source ));
            return createDummyClass(parent);
        }


        try {
            if(parent.isClass() && kind==ClassType.CLASS)
                mod |= JMod.STATIC;

            JDefinedClass r = parent._class(mod,name,kind);
            // use the metadata field to store the source location,
            // so that we can report class name collision errors.
            r.metadata = source;
            
            return r;
        } catch( JClassAlreadyExistsException e ) {
            // class collision.
            JDefinedClass cls = e.getExistingClass();
            
            // report the error
            errorReceiver.error( new SAXParseException(
                Messages.format( Messages.ERR_CLASSNAME_COLLISION, cls.fullName() ),
                (Locator)cls.metadata ));
            errorReceiver.error( new SAXParseException(
                Messages.format( Messages.ERR_CLASSNAME_COLLISION_SOURCE, name ),
                source ));
            
            if( !name.equals(cls.name()) ) {
                // on Windows, FooBar and Foobar causes name collision
                errorReceiver.error( new SAXParseException(
                    Messages.format( Messages.ERR_CASE_SENSITIVITY_COLLISION,
                        name, cls.name() ), null ) );
            }

            if(Util.equals((Locator)cls.metadata,source)) {
                errorReceiver.error( new SAXParseException(
                    Messages.format( Messages.ERR_CHAMELEON_SCHEMA_GONE_WILD ),
                    source ));
            }

            return createDummyClass(parent);
        }
    }

    /**
     * Create a dummy class to recover from an error.
     *
     * We won't generate the code, so the client will never see this class
     * getting generated.
     */
    private JDefinedClass createDummyClass(JClassContainer parent) {
        try {
            return parent._class("$$$garbage$$$"+(ticketMaster++));
        } catch( JClassAlreadyExistsException ee ) {
            return ee.getExistingClass();
        }
    }
}
