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

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.reader.RawTypeSet;
import com.sun.tools.xjc.reader.xmlschema.RawTypeSetBuilder;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSType;

/**
 * @author Kohsuke Kawaguchi
 */
final class MixedExtendedComplexTypeBuilder extends AbstractExtendedComplexTypeBuilder {

    public boolean isApplicable(XSComplexType ct) {

        if (!bgmBuilder.isGenerateMixedExtensions()) return false;

        XSType bt = ct.getBaseType();
        if (bt.isComplexType() &&
            bt.asComplexType().isMixed() &&
            ct.isMixed() &&
            ct.getDerivationMethod()==XSType.EXTENSION &&
            ct.getContentType().asParticle() != null &&
            ct.getExplicitContent().asEmpty() == null
            )  {
                return true;
        }

        return false;
    }

    public void build(XSComplexType ct) {
        XSComplexType baseType = ct.getBaseType().asComplexType();

        // build the base class
        CClass baseClass = selector.bindToType(baseType, ct, true);
        assert baseClass != null;   // global complex type must map to a class

        if (!checkIfExtensionSafe(baseType, ct)) {
            // error. We can't handle any further extension
            errorReceiver.error(ct.getLocator(),
                    Messages.ERR_NO_FURTHER_EXTENSION.format(
                    baseType.getName(), ct.getName() )
            );
            return;
        }

        selector.getCurrentBean().setBaseClass(baseClass);
        builder.recordBindingMode(ct, ComplexTypeBindingMode.FALLBACK_EXTENSION);

        BIProperty prop = BIProperty.getCustomization(ct);
        CPropertyInfo p;

        RawTypeSet ts = RawTypeSetBuilder.build(ct.getContentType().asParticle(), false);
        p = prop.createDummyExtendedMixedReferenceProperty("contentOverrideFor" + ct.getName(), ct, ts);

        selector.getCurrentBean().addProperty(p);

        // adds attributes and we are through.
        green.attContainer(ct);        
    }

}
