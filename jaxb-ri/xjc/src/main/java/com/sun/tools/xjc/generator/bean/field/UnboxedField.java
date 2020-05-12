/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JPrimitiveType;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.FieldAccessor;
import org.glassfish.jaxb.core.api.impl.NameConverter;

/**
 * A required primitive property.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class UnboxedField extends AbstractFieldWithVar {

    /**
     * The primitive version of {@link #implType} and {@link #exposedType}.
     */
    private final JPrimitiveType ptype;


    protected UnboxedField( ClassOutlineImpl outline, CPropertyInfo prop ) {
        super(outline,prop);
        // primitive types don't have this distintion
        assert implType==exposedType;

        ptype = (JPrimitiveType) implType;
        assert ptype!=null;
        
        createField();

        // apparently a required attribute can be still defaulted.
        // so this assertion is incorrect.
        // assert prop.defaultValue==null;

        MethodWriter writer = outline.createMethodWriter();
        NameConverter nc = outline.parent().getModel().getNameConverter();

        JBlock body;
        
        // [RESULT]
        // Type getXXX() {
        //     return value;
        // }
        JMethod $get = writer.declareMethod( ptype, getGetterMethod() );
        String javadoc = prop.javadoc;
        if(javadoc.length()==0)
            javadoc = Messages.DEFAULT_GETTER_JAVADOC.format(nc.toVariableName(prop.getName(true)));
        writer.javadoc().append(javadoc);

        $get.body()._return(ref());


        // [RESULT]
        // void setXXX( Type value ) {
        //     this.value = value;
        // }
        JMethod $set = writer.declareMethod( codeModel.VOID, "set"+prop.getName(true) );
        JVar $value = writer.addParameter( ptype, "value" );
        body = $set.body();
        body.assign(JExpr._this().ref(ref()),$value);
        // setter always get the default javadoc. See issue #381
        writer.javadoc().append(Messages.DEFAULT_SETTER_JAVADOC.format(nc.toVariableName(prop.getName(true))));

    }

    protected JType getType(Aspect aspect) {
        return super.getType(aspect).boxify().getPrimitiveType();
    }

    protected JType getFieldType() {
        return ptype;
    }

    public FieldAccessor create(JExpression targetObject) {
        return new Accessor(targetObject) {
            
            public void unsetValues( JBlock body ) {
                // you can't unset a value
            }
            
            public JExpression hasSetValue() {
                return JExpr.TRUE;
            }
        };
    }
}
