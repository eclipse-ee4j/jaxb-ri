/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2019 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.xml.bind.v2.runtime.reflect.opt;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.RuntimeUtil;

import static com.sun.xml.bind.v2.bytecode.ClassTailor.toVMClassName;
import static com.sun.xml.bind.v2.bytecode.ClassTailor.toVMTypeName;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class OptimizedAccessorFactory {
    private OptimizedAccessorFactory() {} // no instanciation please

    public static final boolean noOptimization = Boolean.getBoolean(OptimizedAccessorFactory.class.getName()+".noOptimization");

    private static final Logger logger = Logger.getLogger(OptimizedAccessorFactory.class.getName());

    private static final String fieldTemplateName;
    private static final String methodTemplateName;

    static {
        String s = FieldAccessor_Byte.class.getName();
        fieldTemplateName = s.substring(0,s.length()-"Byte".length()).replace('.','/');

        s = MethodAccessor_Byte.class.getName();
        methodTemplateName = s.substring(0,s.length()-"Byte".length()).replace('.','/');
    }

    /**
     * Gets the optimized {@link Accessor} that accesses the given getter/setter.
     *
     * @return null
     *      if for some reason it fails to create an optimized version.
     */
    public static final <B,V> Accessor<B,V> get(Method getter, Method setter) {
        if(noOptimization){
            return null;
        }

        // make sure the method signatures are what we expect
        if(getter.getParameterTypes().length!=0)
            return null;
        Class<?>[] sparams = setter.getParameterTypes();
        if(sparams.length!=1)
            return null;
        if(sparams[0]!=getter.getReturnType())
            return null;
        if(setter.getReturnType()!=Void.TYPE)
            return null;
        if(getter.getDeclaringClass()!=setter.getDeclaringClass())
            return null;
        if(Modifier.isPrivate(getter.getModifiers()) || Modifier.isPrivate(setter.getModifiers()))
            // we can't access private fields
            return null;

        Class t = sparams[0];
        String typeName = t.getName().replace('.','_');
        if (t.isArray()) {
            typeName = "AOf_";
            String compName = t.getComponentType().getName().replace('.','_');
            while (compName.startsWith("[L")) {
                compName = compName.substring(2);
                typeName += "AOf_";
            }
            typeName = typeName + compName;
        }

        String newClassName = toVMClassName(getter.getDeclaringClass())+"$JaxbAccessorM_"+getter.getName()+'_'+setter.getName()+'_'+typeName;
        Class opt;

        if(t.isPrimitive())
            opt = AccessorInjector.prepare( getter.getDeclaringClass(),
                    methodTemplateName+RuntimeUtil.primitiveToBox.get(t).getSimpleName(),
                    newClassName,
                    toVMClassName(Bean.class),
                    toVMClassName(getter.getDeclaringClass()),
                    "get_"+t.getName(),
                    getter.getName(),
                    "set_"+t.getName(),
                    setter.getName());
        else
            opt = AccessorInjector.prepare( getter.getDeclaringClass(),
                    methodTemplateName+"Ref",
                    newClassName,
                    toVMClassName(Bean.class),
                    toVMClassName(getter.getDeclaringClass()),
                    toVMClassName(Ref.class),
                    toVMClassName(t),
                    "()"+toVMTypeName(Ref.class),
                    "()"+toVMTypeName(t),
                    '('+toVMTypeName(Ref.class)+")V",
                    '('+toVMTypeName(t)+")V",
                    "get_ref",
                    getter.getName(),
                    "set_ref",
                    setter.getName());

        if(opt==null)
            return null;

        Accessor<B,V> acc = instanciate(opt);
        if (acc!=null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Using optimized Accessor for {0} and {1}", new Object[]{getter, setter});
            }
        }
        return acc;
    }


    /**
     * Gets the optimized {@link Accessor} that accesses the given field.
     *
     * @return null
     *      if for some reason it fails to create an optimized version.
     */
    public static final <B,V> Accessor<B,V> get(Field field) {
        if(noOptimization){
            return null;
        }

        int mods = field.getModifiers();
        if(Modifier.isPrivate(mods) || Modifier.isFinal(mods))
            // we can't access private fields
            return null;

        String newClassName = toVMClassName(field.getDeclaringClass())+"$JaxbAccessorF_"+field.getName();

        Class opt;

        if(field.getType().isPrimitive())
            opt = AccessorInjector.prepare( field.getDeclaringClass(),
                    fieldTemplateName+RuntimeUtil.primitiveToBox.get(field.getType()).getSimpleName(),
                    newClassName,
                    toVMClassName(Bean.class),
                    toVMClassName(field.getDeclaringClass()),
                    "f_"+field.getType().getName(),
                    field.getName() );
        else
            opt = AccessorInjector.prepare( field.getDeclaringClass(),
                    fieldTemplateName+"Ref",
                    newClassName,
                    toVMClassName(Bean.class),
                    toVMClassName(field.getDeclaringClass()),
                    toVMClassName(Ref.class),
                    toVMClassName(field.getType()),
                    toVMTypeName(Ref.class),
                    toVMTypeName(field.getType()),
                    "f_ref",
                    field.getName() );

        if(opt==null)
            return null;

        Accessor<B,V> acc = instanciate(opt);
        if (acc!=null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Using optimized Accessor for {0}", field);
            }
        }
        return acc;
    }

    private static <B,V> Accessor<B,V> instanciate(Class opt) {
        try {
            return (Accessor<B,V>)opt.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.INFO,"failed to load an optimized Accessor",e);
        } catch (IllegalAccessException e) {
            logger.log(Level.INFO,"failed to load an optimized Accessor",e);
        } catch (SecurityException e) {
            logger.log(Level.INFO,"failed to load an optimized Accessor",e);
        }
        return null;
    }
}
