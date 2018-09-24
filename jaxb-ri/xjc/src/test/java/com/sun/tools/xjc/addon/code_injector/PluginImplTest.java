/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.addon.code_injector;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.CustomizableOutline;
import com.sun.tools.xjc.outline.Outline;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xml.sax.ErrorHandler;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yaroska
 */

@RunWith(JMockit.class)
public class PluginImplTest {

    @Test
    public void pluginRunTest(final @Mocked Outline model, @Mocked Options opt, @Mocked ErrorHandler errorHandler) {

        new Expectations() {{
            Collection<? extends CustomizableOutline> target = Collections.emptyList();
            model.getClasses(); result = target;
            Deencapsulation.invoke(PluginImpl.class, "checkAndInject", target);
            model.getEnums(); result = target;
            Deencapsulation.invoke(PluginImpl.class, "checkAndInject", target);
        }};

        new PluginImpl().run(model, opt, errorHandler);
    }
}
