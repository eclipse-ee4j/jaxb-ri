/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.generator.bean.field;

import com.sun.codemodel.JAssignmentTarget;
import java.util.List;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JForLoop;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.generator.bean.MethodWriter;
import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * Realizes a property as an "indexed property"
 * as specified in the JAXB spec.
 * 
 * <p>
 * We will generate the following set of methods:
 * <pre>
 * T[] getX();
 * T getX( int idx );
 * void setX(T[] values);
 * void setX( int idx, T value );
 * </pre>
 * 
 * We still use List as our back storage.
 * This renderer also handles boxing/unboxing if
 * T is a boxed type.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class ArrayField extends AbstractListField {
    
    class Accessor extends AbstractListField.Accessor {
        protected Accessor( JExpression $target ) {
            super($target);
        }
        
        @Override
        public void toRawValue(JBlock block, JVar $var) {
            block.assign($var,$target.invoke($getAll));
        }

        @Override
        public void fromRawValue(JBlock block, String uniqueName, JExpression $var) {
            block.invoke($target,$setAll).arg($var);
        }
        
        @Override
        public JExpression hasSetValue() {
            return field.ne(JExpr._null()).cand(field.ref("length").gt(JExpr.lit(0)));
        }
        
    }
    
    private JMethod $setAll;
    
    private JMethod $getAll;
    
    ArrayField(ClassOutlineImpl context, CPropertyInfo prop) {
        super(context,prop,false);
        generateArray();
    }
    
    protected void generateArray() {
        field = outline.implClass.field( JMod.PROTECTED, getCoreListType(), prop.getName(false) );
        annotate(field);

        // generate the rest of accessors
        generateAccessors();
    }
    
    @Override
    public void generateAccessors() {
        
        MethodWriter writer = outline.createMethodWriter();
        Accessor acc = create(JExpr._this());
        JVar $idx,$value; JBlock body;

        // [RESULT] T[] getX() {
        //     if( <var>==null )    return new T[0];
        //     T[] retVal = new T[this._return.length] ;
        //     System.arraycopy(this._return, 0, "retVal", 0, this._return.length);
        //     return (retVal);
        // }
        $getAll = writer.declareMethod( exposedType.array(),"get"+prop.getName(true));
        if (prop.javadoc != null && prop.javadoc.length() > 0) {
            writer.javadoc().append(prop.javadoc).append("\n\n");
        }
        body = $getAll.body();

        body._if( acc.ref(true).eq(JExpr._null()) )._then()
            ._return(JExpr.newArray(exposedType,0));
        JVar var = body.decl(exposedType.array(), "retVal", JExpr.newArray(implType,acc.ref(true).ref("length")));
        body.add(codeModel.ref(System.class).staticInvoke("arraycopy")
                        .arg(acc.ref(true)).arg(JExpr.lit(0))
                        .arg(var)
                        .arg(JExpr.lit(0)).arg(acc.ref(true).ref("length")));
        body._return(JExpr.direct("retVal"));

        List<Object> returnTypes = listPossibleTypes(prop);
        writer.javadoc().addReturn().append("array of\n").append(returnTypes);

        // [RESULT]
        // ET getX(int idx) {
        //     if( <var>==null )    throw new IndexOutOfBoundsException();
        //     return unbox(<var>.get(idx));
        // }
        JMethod $get = writer.declareMethod(exposedType,"get"+prop.getName(true));
        $idx = writer.addParameter(codeModel.INT,"idx");

        $get.body()._if(acc.ref(true).eq(JExpr._null()))._then()
            ._throw(JExpr._new(codeModel.ref(IndexOutOfBoundsException.class)));

        writer.javadoc().append(prop.javadoc);
        $get.body()._return(acc.ref(true).component($idx));

        writer.javadoc().addReturn().append("one of\n").append(returnTypes);

        // [RESULT] int getXLength() {
        //     if( <var>==null )    throw new IndexOutOfBoundsException();
        //     return <ref>.length;
        // }
        JMethod $getLength = writer.declareMethod(codeModel.INT,"get"+prop.getName(true)+"Length");
        $getLength.body()._if(acc.ref(true).eq(JExpr._null()))._then()
                ._return(JExpr.lit(0));
        $getLength.body()._return(acc.ref(true).ref("length"));

        // [RESULT] void setX(ET[] values) {
        //     if (values == null) {
        //         <ref> = null;
        //         return;
        //     }
        //     int len = values.length;
        //     for( int i=0; i<len; i++ )
        //         <ref>[i] = values[i];
        // }
        $setAll = writer.declareMethod(
            codeModel.VOID,
            "set"+prop.getName(true));

        writer.javadoc().append(prop.javadoc);
        $value = writer.addParameter(exposedType.array(),"values");

        $setAll.body()._if( $value.eq(JExpr._null()) )._then()
            .assign((JAssignmentTarget) acc.ref(true), JExpr._null())
             ._return();

        JVar $len = $setAll.body().decl(codeModel.INT,"len", $value.ref("length"));

        $setAll.body().assign(
                (JAssignmentTarget) acc.ref(true),
                castToImplTypeArray(JExpr.newArray(
                    implType,
                    $len)));

        JForLoop _for = $setAll.body()._for();
        JVar $i = _for.init( codeModel.INT, "i", JExpr.lit(0) );
        _for.test( JOp.lt($i,$len) );
        _for.update( $i.incr() );
        _for.body().assign(acc.ref(true).component($i), castToImplType(acc.box($value.component($i))));

        writer.javadoc().addParam($value)
            .append("allowed objects are\n")
            .append(returnTypes);

        // [RESULT] ET setX(int idx, ET value)
        // <ref>[idx] = value
        JMethod $set = writer.declareMethod(
            exposedType,
            "set"+prop.getName(true));
        $idx = writer.addParameter( codeModel.INT, "idx" );
        $value = writer.addParameter( exposedType, "value" );

        writer.javadoc().append(prop.javadoc);

        body = $set.body();
        body._return( JExpr.assign(acc.ref(true).component($idx),
                                   castToImplType(acc.box($value))));

        writer.javadoc().addParam($value)
            .append("allowed object is\n")
            .append(returnTypes);
            
    }
    
    @Override
    public JType getRawType() {
        return exposedType.array();
    }

    @Override
    protected JClass getCoreListType() {
        return exposedType.array();
    }
    
    @Override
    public Accessor create(JExpression targetObject) {
        return new Accessor(targetObject);
    }

    /**
     * Case from {@link #exposedType} to array of {@link #implType} .
     */
    protected JExpression castToImplTypeArray(JExpression exp ) {
        return JExpr.cast(implType.array(), exp);
    }

}
