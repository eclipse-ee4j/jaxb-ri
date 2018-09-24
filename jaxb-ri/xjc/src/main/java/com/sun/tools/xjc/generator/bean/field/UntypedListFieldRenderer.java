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

import com.sun.codemodel.JClass;
import com.sun.tools.xjc.generator.bean.ClassOutlineImpl;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.outline.FieldOutline;

/**
 *
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class UntypedListFieldRenderer implements FieldRenderer {

    private JClass coreList;
    private boolean dummy;
    private boolean content;

    protected UntypedListFieldRenderer( JClass coreList) {
        this(coreList, false, false);
    }

    protected UntypedListFieldRenderer( JClass coreList, boolean dummy, boolean content) {
        this.coreList = coreList;
        this.dummy = dummy;
        this.content = content;
    }

    public FieldOutline generate(ClassOutlineImpl context, CPropertyInfo prop) {
        if (dummy) {
            return new DummyListField(context, prop, coreList);
        }
        if (content) {
            return new ContentListField(context, prop, coreList);
        }
        return new UntypedListField(context, prop, coreList);
    }
}
