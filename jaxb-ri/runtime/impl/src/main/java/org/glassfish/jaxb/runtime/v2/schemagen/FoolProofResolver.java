/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.schemagen;

import org.glassfish.jaxb.core.Utils;
import jakarta.xml.bind.SchemaOutputResolver;

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * {@link SchemaOutputResolver} that wraps the user-specified resolver
 * and makes sure that it's following the contract.
 *
 * <p>
 * This protects the rest of the {@link XmlSchemaGenerator} from client programming
 * error.
 */
final class FoolProofResolver extends SchemaOutputResolver {
    private static final Logger logger = Utils.getClassLogger();
    private final SchemaOutputResolver resolver;

    public FoolProofResolver(SchemaOutputResolver resolver) {
        assert resolver!=null;
        this.resolver = resolver;
    }

    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
        logger.entering(getClass().getName(),"createOutput",new Object[]{namespaceUri,suggestedFileName});
        Result r = resolver.createOutput(namespaceUri,suggestedFileName);
        if(r!=null) {
            String sysId = r.getSystemId();
            logger.finer("system ID = "+sysId);
            if(sysId!=null) {
                // TODO: make sure that the system Id is absolute

                // don't use java.net.URI, because it doesn't allow some characters (like SP)
                // which can legally used as file names.

                // but don't use java.net.URL either, because it doesn't allow a made-up URI
                // like kohsuke://foo/bar/zot
            } else
                throw new AssertionError("system ID cannot be null");
        }
        logger.exiting(getClass().getName(),"createOutput",r);
        return r;
    }
}
