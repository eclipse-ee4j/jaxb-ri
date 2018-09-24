/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com), Martin Grebac
 */
abstract class AbstractFieldWithVar extends AbstractField {
    
    /**
     * Field declaration of the actual list object that we use
     * to store data.
     */
    private JFieldVar field;
    
    /**
     * Invoke {@link #createField()} after calling the
     * constructor.
     */
    AbstractFieldWithVar( ClassOutlineImpl outline, CPropertyInfo prop ) {
        super(outline,prop);
    }
    
    protected final void createField() {
        field = outline.implClass.field( JMod.PROTECTED,
            getFieldType(), prop.getName(false) );

        annotate(field);
    }

    /**
     * Gets the name of the getter method.
     *
     * <p>
     * This encapsulation is necessary because sometimes we use
     * {@code isXXXX} as the method name.
     */
    protected String getGetterMethod() {
        if (getOptions().enableIntrospection) {
            return ((getFieldType().isPrimitive() &&
                     getFieldType().boxify().getPrimitiveType()==codeModel.BOOLEAN) ?
                         "is":"get") + prop.getName(true);
        } else {
            return (getFieldType().boxify().getPrimitiveType()==codeModel.BOOLEAN?"is":"get")+prop.getName(true);
        }
    }

    /**
     * Returns the type used to store the value of the field in memory.
     */
    protected abstract JType getFieldType();

    protected JFieldVar ref() { return field; }

    public final JType getRawType() {
        return exposedType;
    }
    
    protected abstract class Accessor extends AbstractField.Accessor {
    
        protected Accessor(JExpression $target) {
            super($target);
            this.$ref = $target.ref(AbstractFieldWithVar.this.ref());
        }
        
        /**
         * Reference to the field bound by the target object.
         */
        protected final JFieldRef $ref;

        public final void toRawValue(JBlock block, JVar $var) {
            if (getOptions().enableIntrospection) {
                block.assign($var,$target.invoke(getGetterMethod()));
            } else {
                block.assign($var,$target.invoke(getGetterMethod()));
            }
        }

        public final void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
            block.invoke($target,("set"+prop.getName(true))).arg($var);
        }
    }
}
