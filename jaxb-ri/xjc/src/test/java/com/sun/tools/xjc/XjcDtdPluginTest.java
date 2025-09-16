/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.Driver.OptionsEx;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.util.ErrorReceiverFilter;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

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

        Assert.assertTrue(
            "DTD model did not call postProcessModel hook of the plugin",
            wasPostProcessModelHookCalled[0]
        );
    }

    @Test
    public void testIssue_cannotRenderJavadocForLists() throws Exception {
        final OptionsEx opt = new OptionsEx();
        opt.setSchemaLanguage(Language.DTD);
        opt.compatibilityMode = Options.EXTENSION;

        opt.addGrammar(
            new InputSource(
                new ByteArrayInputStream("<!ELEMENT AnElement1 (#PCDATA)>\n<!ELEMENT AnElement2 (#PCDATA)>\n<!ELEMENT AnElement (AnElement1 | AnElement2)>".getBytes())
            )
        );

        Model model = ModelLoader.load(
            opt,
            new JCodeModel(),
            null
        );
        model.generateCode(opt, null);
    }

    @Test
    public void testIssue_PCDataNoCamelCase() throws Exception {
        final OptionsEx opt = new OptionsEx();
        opt.setSchemaLanguage(Language.DTD);
        opt.compatibilityMode = Options.EXTENSION;

        opt.addGrammar(
                new InputSource(
                        XjcDtdPluginTest.class.getResourceAsStream("/schemas/issue1830/pcdata_no_camelcase.dtd")
                )
        );

        Model model = ModelLoader.load(
                opt,
                new JCodeModel(),
                null
        );
        model.generateCode(opt, null);
        JMethod getValueMethod = model.codeModel._getClass("generated.AnElement").methods().iterator().next();

        Assert.assertEquals("getValue", getValueMethod.name());
    }
}
