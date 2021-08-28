/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.parser;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;
import org.xml.sax.InputSource;

/**
 * LSInput implementation that wraps a SAX InputSource
 * 
 * @author Ryan.Shoemaker@Sun.COM
 */
public class LSInputSAXWrapper implements LSInput {
    private InputSource core;

    public LSInputSAXWrapper(InputSource inputSource) {
        assert inputSource!=null;
        core = inputSource;
    }

    @Override
    public Reader getCharacterStream() {
        return core.getCharacterStream();
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        core.setCharacterStream(characterStream);
    }

    @Override
    public InputStream getByteStream() {
        return core.getByteStream();
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        core.setByteStream(byteStream);
    }

    @Override
    public String getStringData() {
        return null;
    }

    @Override
    public void setStringData(String stringData) {
        // no-op
    }

    @Override
    public String getSystemId() {
        return core.getSystemId();
    }

    @Override
    public void setSystemId(String systemId) {
        core.setSystemId(systemId);
    }

    @Override
    public String getPublicId() {
        return core.getPublicId();
    }

    @Override
    public void setPublicId(String publicId) {
        core.setPublicId(publicId);
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public void setBaseURI(String baseURI) {
        // no-op
    }

    @Override
    public String getEncoding() {
        return core.getEncoding();
    }

    @Override
    public void setEncoding(String encoding) {
        core.setEncoding(encoding);
    }

    @Override
    public boolean getCertifiedText() {
        return true;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        // no-op
    }
}
