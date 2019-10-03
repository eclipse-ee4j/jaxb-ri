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
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.xml.bind.v2.model.core.TypeInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;

import static com.sun.xml.bind.v2.bytecode.ClassTailor.toVMClassName;

/**
 * Prepares optimized {@link TransducedAccessor} from templates.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class OptimizedTransducedAccessorFactory {
    private OptimizedTransducedAccessorFactory() {} // no instantiation please

    // http://java.sun.com/docs/books/vmspec/2nd-edition/html/ConstantPool.doc.html#75929
    // "same runtime package"

    private static final Logger logger = Logger.getLogger(OptimizedTransducedAccessorFactory.class.getName());

    private static final String fieldTemplateName;
    private static final String methodTemplateName;

    static {
        String s = TransducedAccessor_field_Byte.class.getName();
        fieldTemplateName = s.substring(0,s.length()-"Byte".length()).replace('.','/');

        s = TransducedAccessor_method_Byte.class.getName();
        methodTemplateName = s.substring(0,s.length()-"Byte".length()).replace('.','/');
    }

    /**
     * Gets the optimized {@link TransducedAccessor} if possible.
     *
     * @return null
     *      if for some reason it fails to create an optimized version.
     */
    public static final TransducedAccessor get(RuntimePropertyInfo prop) {
        if(OptimizedAccessorFactory.noOptimization){
            return null;
        }

        Accessor acc = prop.getAccessor();

        // consider using an optimized TransducedAccessor implementations.
        Class opt=null;

        TypeInfo<Type,Class> parent = prop.parent();
        if(!(parent instanceof RuntimeClassInfo))
            return null;
        
        Class dc = ((RuntimeClassInfo)parent).getClazz();
        String newClassName = toVMClassName(dc)+"_JaxbXducedAccessor_"+prop.getName();


        if(acc instanceof Accessor.FieldReflection) {
            // TODO: we also need to make sure that the default xducer is used.
            Accessor.FieldReflection racc = (Accessor.FieldReflection) acc;
            Field field = racc.f;

            int mods = field.getModifiers();
            if(Modifier.isPrivate(mods) || Modifier.isFinal(mods))
                // we can't access private fields.
                // TODO: think about how to improve this case
                return null;

            Class<?> t = field.getType();
            if(t.isPrimitive())
                opt = AccessorInjector.prepare( dc,
                    fieldTemplateName+suffixMap.get(t),
                    newClassName,
                    toVMClassName(Bean.class),
                    toVMClassName(dc),
                    "f_"+t.getName(),
                    field.getName() );
        }

        if(acc.getClass()==Accessor.GetterSetterReflection.class) {
            Accessor.GetterSetterReflection gacc = (Accessor.GetterSetterReflection) acc;

            if(gacc.getter==null || gacc.setter==null)
                return null;    // incomplete

            Class<?> t = gacc.getter.getReturnType();

            if(Modifier.isPrivate(gacc.getter.getModifiers())
            || Modifier.isPrivate(gacc.setter.getModifiers()))
                // we can't access private methods.
                return null;


            if(t.isPrimitive())
                opt = AccessorInjector.prepare( dc,
                    methodTemplateName+suffixMap.get(t),
                    newClassName,
                    toVMClassName(Bean.class),
                    toVMClassName(dc),
                    "get_"+t.getName(),
                    gacc.getter.getName(),
                    "set_"+t.getName(),
                    gacc.setter.getName());
        }

        if(opt==null)
            return null;

        logger.log(Level.FINE,"Using optimized TransducedAccessor for "+prop.displayName());


        try {
            return (TransducedAccessor)opt.newInstance();
        } catch (InstantiationException e) {
            logger.log(Level.INFO,"failed to load an optimized TransducedAccessor",e);
        } catch (IllegalAccessException e) {
            logger.log(Level.INFO,"failed to load an optimized TransducedAccessor",e);
        } catch (SecurityException e) {
            logger.log(Level.INFO,"failed to load an optimized TransducedAccessor",e);
        }
        return null;
    }

    private static final Map<Class,String> suffixMap = new HashMap<Class, String>();

    static {
        suffixMap.put(Byte.TYPE,"Byte");
        suffixMap.put(Short.TYPE,"Short");
        suffixMap.put(Integer.TYPE,"Integer");
        suffixMap.put(Long.TYPE,"Long");
        suffixMap.put(Boolean.TYPE,"Boolean");
        suffixMap.put(Float.TYPE,"Float");
        suffixMap.put(Double.TYPE,"Double");
    }

}
