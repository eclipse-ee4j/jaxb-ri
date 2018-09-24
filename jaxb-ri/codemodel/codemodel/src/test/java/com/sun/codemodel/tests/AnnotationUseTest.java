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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.codemodel.writer.SingleStreamCodeWriter;

/**
 * A test program for the annotation use features Note: Not all the generated
 * code would make sense but just checking in all the different ways you can use
 * an annotation
 * 
 * @author Bhakti Mehta
 */
public class AnnotationUseTest {

	@Test
	public void main() throws Exception {
		JCodeModel cm = new JCodeModel();
		JDefinedClass cls = cm._class("Test");
		// JMethod m =
		cls.method(JMod.PUBLIC, cm.VOID, "foo");

		// Annotating a class
		// Using the existing Annotations from java.lang.annotation package
        JAnnotationUse use = cls.annotate(cm.ref(Retention.class));

        use = tstremoveAnnotation(cm, cls, use);

		// declaring an enum class and an enumconstant as a membervaluepair
		JDefinedClass enumcls = cls._enum("Iamenum");
		JEnumConstant ec = enumcls.enumConstant("GOOD");
		JEnumConstant ec1 = enumcls.enumConstant("BAD");
		JEnumConstant ec2 = enumcls.enumConstant("BAD");
		ec1.equals(ec2);

		use.param("value", ec);
		// adding another param as an enum
		use.param("value1", RetentionPolicy.RUNTIME);

		// Adding annotation for fields
		// will generate like
		// @String(name = "book") private double y;
		//
		JFieldVar field = cls.field(JMod.PRIVATE, cm.DOUBLE, "y");

		// Adding more annotations which are member value pairs
		JAnnotationUse ause = field.annotate(Retention.class);
		ause.param("name", "book");
		ause.param("targetNamespace", 5);

		// Adding arrays as member value pairs
		JAnnotationArrayMember arrayMember = ause.paramArray("names");
		arrayMember.param("Bob");
		arrayMember.param("Rob");
		arrayMember.param("Ted");

		JAnnotationArrayMember arrayMember1 = ause.paramArray("namesno");
		arrayMember1.param(4);
		arrayMember1.param(5);
		arrayMember1.param(6);

		JAnnotationArrayMember arrayMember2 = ause.paramArray("values");
		// adding an annotation as a member value pair
		arrayMember2.annotate(Target.class).param("type", Integer.class);
		arrayMember2.annotate(Target.class).param("type", Float.class);

		// test typed annotation writer
		XmlElementW w = cls.annotate2(XmlElementW.class);
		w.ns("##default").value("foobar");

		// adding an annotation as a member value pair
		JAnnotationUse myuse = ause.annotationParam("foo", Target.class);
		myuse.param("junk", 7);

		cm.build(new SingleStreamCodeWriter(System.out));
	}

    private JAnnotationUse tstremoveAnnotation(JCodeModel cm, JDefinedClass cls, JAnnotationUse use) {
        Collection<JAnnotationUse> anns = cls.annotations();
        Assert.assertTrue(anns.contains(use));
        Assert.assertTrue(cls.removeAnnotation(use));
        Collection<JAnnotationUse> annsUpdated = cls.annotations();
        Assert.assertFalse(annsUpdated.contains(use));
        Assert.assertFalse(cls.removeAnnotation(use));
        use = cls.annotate(cm.ref(Retention.class));
        return use;
    }

    @interface XmlElement {
		String value();

		String ns();
	}

	interface XmlElementW extends JAnnotationWriter<XmlElement> {
		XmlElementW value(String s);

		XmlElementW ns(String s);
	}
}

/*
 * *********************************************************************
 * Generates this
 * **********************************************************************
 * 
 * @java.lang.annotation.Retention(value1 =
 * java.lang.annotation.RetentionPolicy.RUNTIME, value = Test.Iamenum.GOOD)
 * public class Test {
 * 
 * @java.lang.annotation.Retention(foo = @java.lang.annotation.Target(junk = 7)
 * 
 * , targetNamespace = 5, namesno = { 4, 5, 6 }, values =
 * {@java.lang.annotation.Target(type = java.lang.Integer) ,
 * @java.lang.annotation.Target(type = java.lang.Float) }, names = {"Bob",
 * "Rob", "Ted"}, name = "book") private double y;
 * 
 * public void foo() { }
 * 
 * public enum Iamenum {
 * 
 * BAD, GOOD; }
 * 
 * }
 * 
 * }
 */
