/*
 * Copyright (c) 2022 Eclipse Foundation
 * Copyright (C) 2004-2011
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sun.tools.rngom.binary;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

final class RestrictionViolationException extends Exception {
    private static final long serialVersionUID = -5264123170571318715L;
    private String messageId;
    private Locator loc;
    private QName name;

    RestrictionViolationException(String messageId) {
        this.messageId = messageId;
    }

    RestrictionViolationException(String messageId, QName name) {
        this.messageId = messageId;
        this.name = name;
    }

    String getMessageId() {
        return messageId;
    }

    Locator getLocator() {
        return loc;
    }

    void maybeSetLocator(Locator loc) {
        if (this.loc == null)
            this.loc = loc;
    }

    QName getName() {
        return name;
    }
}
