/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.ct;

import com.sun.tools.xjc.model.CBuiltinLeafInfo;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.tools.xjc.reader.xmlschema.RawTypeSetBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import static com.sun.tools.xjc.reader.xmlschema.ct.ComplexTypeBindingMode.FALLBACK_CONTENT;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSType;

/**
 * @author Kohsuke Kawaguchi
 */
final class MultiWildcardComplexTypeBuilder extends CTBuilder {

    public boolean isApplicable(XSComplexType ct) {
        if (!bgmBuilder.model.options.contentForWildcard) {
            return false;
        }
        XSType bt = ct.getBaseType();
        if (bt ==schemas.getAnyType() && ct.getContentType() != null) {
            XSParticle part = ct.getContentType().asParticle();
            if ((part != null) && (part.getTerm().isModelGroup())) {
                XSParticle[] parts = part.getTerm().asModelGroup().getChildren();
                int wildcardCount = 0;
                int i = 0;
                while ((i < parts.length) && (wildcardCount <= 1)) {
                    if (parts[i].getTerm().isWildcard()) {
                        wildcardCount += 1;
                    }
                    i++;
                }
                return (wildcardCount > 1);
            }
        }
        return false;
    }

    public void build(XSComplexType ct) {
        XSContentType contentType = ct.getContentType();

        builder.recordBindingMode(ct, FALLBACK_CONTENT);
        BIProperty prop = BIProperty.getCustomization(ct);

        CPropertyInfo p;

        if(contentType.asEmpty()!=null) {
            p = prop.createValueProperty("Content",false,ct,CBuiltinLeafInfo.STRING,null);
        } else {
            RawTypeSet ts = RawTypeSetBuilder.build(contentType.asParticle(),false);
            p = prop.createReferenceProperty("Content", false, ct, ts, true, false, true, false);
        }

        selector.getCurrentBean().addProperty(p);

        // adds attributes and we are through.
        green.attContainer(ct);
    }

}
