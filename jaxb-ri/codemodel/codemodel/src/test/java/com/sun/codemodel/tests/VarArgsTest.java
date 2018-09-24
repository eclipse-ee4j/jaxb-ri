/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.tests;

import java.io.IOException;

import org.junit.Test;

import com.sun.codemodel.*;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * 
 * Simple program to test the generation of
 * the varargs feature in jdk 1.5
 * @author Bhakti Mehta Bhakti.Mehta@sun.com
 *
 */
/*======================================================
 * This is how the output from this program looks like
 * Still need to learn how to work on instantiation and args
 * =========================================================
 * public class Test {


     public void foo(java.lang.String param1, 
         java.lang.Integer param2, java.lang.String param5,
         java.lang.Object... param3) {
      for (int count = 0; (count<(param3.length)); count ++) {
          java.lang.System.out.println((param3[count]));
      }
  }

    public static void main(java.lang.String[] args) {
    }

}
*==========================================================
**/

public class VarArgsTest {

	@Test
	public void main() throws Exception {

        try {
            JCodeModel cm = new JCodeModel();
            JDefinedClass cls = cm._class("Test");
            JMethod m = cls.method(JMod.PUBLIC, cm.VOID, "foo");
            m.param(String.class, "param1");
            m.param(Integer.class, "param2");
            JVar var = m.varParam(Object.class, "param3");
            System.out.println("First varParam " + var);
            
            // checking for param after varParam it behaves ok
            //JVar[] var1 = m.varParam(Float.class, "param4");
            JClass string = cm.ref(String.class);
            JClass stringArray = string.array();
//            JVar param5 =
            m.param(String.class, "param5");
            
            JForLoop forloop = m.body()._for();
            
            JVar $count = forloop.init(cm.INT, "count", JExpr.lit(0));
            
            forloop.test($count.lt(JExpr.direct("param3.length")));
            forloop.update($count.incr());
            
            JFieldRef out = cm.ref(System.class).staticRef("out");
            
//            JVar typearray = 
            m.listVarParam();
            
//            JInvocation invocation =
            forloop.body().invoke(out, "println").arg(
                    JExpr.direct("param3[count]"));
            
            JMethod main = cls.method(JMod.PUBLIC | JMod.STATIC, cm.VOID, "main");
            main.param(stringArray, "args");
            main.body().directStatement("new Test().foo(new String(\"Param1\"),new Integer(5),null,new String(\"Param3\"),new String(\"Param4\"));" );//new String("Param1"))"");//                "new Integer(5),+//                "null," +//                "new String("first")," +//                " new String("Second"))");
            
            cm.build(new SingleStreamCodeWriter(System.out));
        } catch (JClassAlreadyExistsException e) {
            
            e.printStackTrace();
        } catch (IOException e) {
            
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
