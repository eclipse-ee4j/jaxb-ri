/*
 * Copyright (c) 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.sun.codemodel;

/**
 * Captures the Class or Enum value of the annotation.
 *
 */
public final class JAnnotationClassValue extends JAnnotationValue {

    private final JClass type;
    private String param;

    JAnnotationClassValue(JClass type) {
        this.type = type;
    }

    JAnnotationClassValue(JEnumConstant en) {
        this.type = en.type();
        this.param = en.getName().substring(en.getName().lastIndexOf('.') + 1);
    }


    @Override
    public void generate(JFormatter f) {
        if (param != null) {
            f.t(type.erasure()).p('.').p(param);
        } else {
            f.t(type.erasure()).p(".class");
        }
    }

    public JClass type() {
        return type;
    }

    public String value() {
        return param;
    }

}
