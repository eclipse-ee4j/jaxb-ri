/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
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
 * TODO: not sure if it's a good idea to move the indenting functionality to another class.
 *
 * Doesn't have to be final, but it helps the JVM.
 *
 * @author Kohsuke Kawaguchi
 */
public final class IndentingUTF8XmlOutput extends UTF8XmlOutput {

    /**
     * Null if the writer should perform no indentation.
     *
     * Otherwise this will keep the 8 copies of the string for indentation.
     * (so that we can write 8 indentation at once.)
     */
    private final Encoded indent8;

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
            indent8 = new Encoded();
            indent8.ensureSize(e.len*8);
            unitLen = e.len;
            for( int i=0; i<8; i++ )
                System.arraycopy(e.buf, 0, indent8.buf, unitLen*i, unitLen);
        } else {
            this.indent8 = null;
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
        int i = depth%8;

        write( indent8.buf, 0, i*unitLen );

        i>>=3;  // really i /= 8;

        for( ; i>0; i-- )
            indent8.write(this);
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
