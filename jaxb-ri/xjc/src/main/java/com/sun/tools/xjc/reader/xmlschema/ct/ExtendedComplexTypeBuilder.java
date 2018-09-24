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
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSType;


/**
 * Binds a complex type derived from another complex type by extension.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class ExtendedComplexTypeBuilder extends AbstractExtendedComplexTypeBuilder {

    public boolean isApplicable(XSComplexType ct) {
        XSType baseType = ct.getBaseType();
        return baseType!=schemas.getAnyType()
            &&  baseType.isComplexType()
            &&  ct.getDerivationMethod()==XSType.EXTENSION;
    }

    public void build(XSComplexType ct) {
        XSComplexType baseType = ct.getBaseType().asComplexType();

        // build the base class
        CClass baseClass = selector.bindToType(baseType, ct, true);
        assert baseClass != null;   // global complex type must map to a class

        selector.getCurrentBean().setBaseClass(baseClass);

        // derivation by extension.
        ComplexTypeBindingMode baseTypeFlag = builder.getBindingMode(baseType);

        XSContentType explicitContent = ct.getExplicitContent();

        if (!checkIfExtensionSafe(baseType, ct)) {
            // error. We can't handle any further extension
            errorReceiver.error(ct.getLocator(),
                    Messages.ERR_NO_FURTHER_EXTENSION.format(
                    baseType.getName(), ct.getName() )
            );
            return;
        }

        // explicit content is always either empty or a particle.
        if (explicitContent != null && explicitContent.asParticle() != null) {
            if (baseTypeFlag == ComplexTypeBindingMode.NORMAL) {
                // if we have additional explicit content, process them.
                builder.recordBindingMode(ct,
                        bgmBuilder.getParticleBinder().checkFallback(explicitContent.asParticle())
                        ? ComplexTypeBindingMode.FALLBACK_REST
                        : ComplexTypeBindingMode.NORMAL);

                bgmBuilder.getParticleBinder().build(explicitContent.asParticle());

            } else {
                // the base class has already done the fallback.
                // don't add anything new
                builder.recordBindingMode(ct, baseTypeFlag );
            }
        } else {
            // if it's empty, no additional processing is necessary
            builder.recordBindingMode(ct, baseTypeFlag);
        }

        // adds attributes and we are through.
        green.attContainer(ct);
    }

}
