/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.addon.accessors;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import java.io.IOException;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import org.xml.sax.ErrorHandler;

/**
 * Generates synchronized methods.
 * 
 * @author
 *     Martin Grebac (martin.grebac@sun.com)
 */
public class PluginImpl extends Plugin {

    public String getOptionName() {
        return "Xpropertyaccessors";
    }

    public String getUsage() {
        return "  -Xpropertyaccessors :  Use XmlAccessType PROPERTY instead of FIELD for generated classes";
    }

    @Override
    public int parseArgument(Options opt, String[] args, int i) throws BadCommandLineException, IOException {
        return 0;   // no option recognized
    }

    public boolean run( Outline model, Options opt, ErrorHandler errorHandler ) {

        for( ClassOutline co : model.getClasses() ) {
            Iterator<JAnnotationUse> ann = co.ref.annotations().iterator();
            while (ann.hasNext()) {
               try {
                    JAnnotationUse a = ann.next();
                    Field clazzField = a.getClass().getDeclaredField("clazz");
                    clazzField.setAccessible(true);
                    JClass cl = (JClass) clazzField.get(a);
                    if (cl.equals(model.getCodeModel()._ref(XmlAccessorType.class))) {
                        a.param("value", XmlAccessType.PROPERTY);
                        break;
                    }
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(PluginImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(PluginImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchFieldException ex) {
                    Logger.getLogger(PluginImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(PluginImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
        return true;
    }
    
}
