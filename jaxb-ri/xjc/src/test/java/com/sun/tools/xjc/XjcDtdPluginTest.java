/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.Driver.OptionsEx;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.ErrorReceiverFilter;
import org.junit.jupiter.api.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class XjcDtdPluginTest {
    @Test
    public void testCallPostProcessModelOnPlugin() {
        final String dtdSchema = "<!ELEMENT hello (#PCDATA)>";

        final OptionsEx opt = new OptionsEx();
        opt.setSchemaLanguage(Language.DTD);
        opt.compatibilityMode = Options.EXTENSION;

        opt.addGrammar(
            new InputSource(
                new ByteArrayInputStream(
                    dtdSchema.getBytes(StandardCharsets.UTF_8)
                )
            )
        );

        final Boolean[] wasPostProcessModelHookCalled = new Boolean[1];
        wasPostProcessModelHookCalled[0] = Boolean.FALSE;

        opt.activePlugins.add(new Plugin() {
            @Override
            public String getOptionName() {
                return "XdebugPostProcessModel";
            }

            @Override
            public String getUsage() {
                return null;
            }

            @Override
            public boolean run(
                Outline outline,
                Options opt,
                ErrorHandler errorHandler) throws SAXException {
                return true;
            }

            @Override
            public void postProcessModel(Model model, ErrorHandler errorHandler) {
                wasPostProcessModelHookCalled[0] = Boolean.TRUE;
                super.postProcessModel(model, errorHandler);
            }
        });

        ModelLoader.load(
            opt,
            new JCodeModel(),
            new ErrorReceiverFilter()
        );

        assertTrue(
                wasPostProcessModelHookCalled[0],
                "DTD model did not call postProcessModel hook of the plugin"
                );
    }
}
