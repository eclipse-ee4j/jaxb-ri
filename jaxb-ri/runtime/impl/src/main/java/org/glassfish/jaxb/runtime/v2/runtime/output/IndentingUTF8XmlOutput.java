/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.runtime.output;

import org.glassfish.jaxb.core.marshaller.CharacterEscapeHandler;
import org.glassfish.jaxb.runtime.v2.runtime.Name;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * {@link UTF8XmlOutput} with indentation.
 *
 *
 * Doesn't have to be final, but it helps the JVM.
 *
 * @author Kohsuke Kawaguchi
 */
public final class IndentingUTF8XmlOutput extends UTF8XmlOutput {

    private static final int MAX_DEPTH = 12;
    /**
     * Null if the writer should perform no indentation.
     *
     * Otherwise this will keep the MAX_DEPTH copies of the string for indentation.
     * (so that we can write MAX_DEPTH indentation at once.)
     */
    private final Encoded indentDepth;

    /**
     * Length of one indentation.
     */
    private final int unitLen;

    private int depth = 0;

    private boolean seenText = false;

    /**
     *
     * @param indentStr
     *      set to null for no indentation and optimal performance.
     *      otherwise the string is used for indentation.
     */
    public IndentingUTF8XmlOutput(OutputStream out, String indentStr, Encoded[] localNames, CharacterEscapeHandler escapeHandler) {
        super(out, localNames, escapeHandler);

        if(indentStr!=null) {
            Encoded e = new Encoded(indentStr);
            indentDepth = new Encoded();
            indentDepth.ensureSize(e.len*MAX_DEPTH);
            unitLen = e.len;
            for( int i=0; i<MAX_DEPTH; i++ )
                System.arraycopy(e.buf, 0, indentDepth.buf, unitLen*i, unitLen);
        } else {
            this.indentDepth = null;
            this.unitLen = 0;
        }
    }

    @Override
    public void beginStartTag(int prefix, String localName) throws IOException {
        indentStartTag();
        super.beginStartTag(prefix, localName);
    }

    @Override
    public void beginStartTag(Name name) throws IOException {
        indentStartTag();
        super.beginStartTag(name);
    }

    private void indentStartTag() throws IOException {
        closeStartTag();
        if(!seenText)
            printIndent();
        depth++;
        seenText = false;
    }

    @Override
    public void endTag(Name name) throws IOException {
        indentEndTag();
        super.endTag(name);
    }

    @Override
    public void endTag(int prefix, String localName) throws IOException {
        indentEndTag();
        super.endTag(prefix, localName);
    }

    private void indentEndTag() throws IOException {
        depth--;
        if(!closeStartTagPending && !seenText)
            printIndent();
        seenText = false;
    }

    private void printIndent() throws IOException {
        write('\n');
        int i = depth%MAX_DEPTH;

        write( indentDepth.buf, 0, i*unitLen );

        //i>>=3;  // really i /= 8;
        i /= MAX_DEPTH;

        for( ; i>0; i-- )
            indentDepth.write(this);
    }

    @Override
    public void text(String value, boolean needSP) throws IOException {
        seenText = true;
        super.text(value, needSP);
    }

    @Override
    public void text(Pcdata value, boolean needSP) throws IOException {
        seenText = true;
        super.text(value, needSP);
    }

    @Override
    public void endDocument(boolean fragment) throws IOException, SAXException, XMLStreamException {
        write('\n');
        super.endDocument(fragment);
    }
}
