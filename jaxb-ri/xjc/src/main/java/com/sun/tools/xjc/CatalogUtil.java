/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import com.sun.org.apache.xml.internal.resolver.CatalogManager;
import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import org.xml.sax.EntityResolver;

/**
 *
 * @author lukas
 */
final class CatalogUtil {

    static EntityResolver getCatalog(EntityResolver entityResolver, File catalogFile, ArrayList<URI> catalogUrls) throws IOException {
        EntityResolver er = entityResolver;
        if (er == null) {
            final CatalogManager staticManager = CatalogManager.getStaticManager();
            // hack to force initialization so catalog manager system properties take effect
            staticManager.getVerbosity();
            staticManager.setIgnoreMissingProperties(true);
            er = new CatalogResolver(true);
        }
        ((CatalogResolver) er).getCatalog().parseCatalog(catalogFile.getPath());
        return er;
    }
}
