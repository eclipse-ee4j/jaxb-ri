/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.addon.code_injector;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.sun.istack.NotNull;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.outline.CustomizableOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.DOMUtils;

import org.xml.sax.ErrorHandler;

/**
 * Entry point of a plugin.
 *
 * See the javadoc of {@link Plugin} for what those methods mean.
 *
 * @author Kohsuke Kawaguchi
 */
public class PluginImpl extends Plugin {
    public String getOptionName() {
        return "Xinject-code";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList(Const.NS);
    }

    public boolean isCustomizationTagName(String nsUri, String localName) {
        return Const.NS.equals(nsUri) && "code".equals(localName);
    }

    public String getUsage() {
        return "  -Xinject-code       :  inject specified Java code fragments into the generated code";
    }

    // meat of the processing
    public boolean run(@NotNull Outline model, Options opt, ErrorHandler errorHandler) {
        checkAndInject(model.getClasses());
        checkAndInject(model.getEnums());
        return true;
    }

    private static void checkAndInject(Collection<? extends CustomizableOutline> outlines) {
        for (CustomizableOutline co : outlines) {
            CPluginCustomization c = co.getTarget().getCustomizations().find(Const.NS, "code");
            if(c==null)
                continue;   // no customization --- nothing to inject here

            c.markAsAcknowledged();
            // TODO: ideally you should validate this DOM element to make sure
            // that there's no typo/etc. JAXP 1.3 can do this very easily.
            String codeFragment = DOMUtils.getElementText(c.element);

            // inject the specified code fragment into the implementation class.
            co.getImplClass().direct(codeFragment);
        }
    }
}
