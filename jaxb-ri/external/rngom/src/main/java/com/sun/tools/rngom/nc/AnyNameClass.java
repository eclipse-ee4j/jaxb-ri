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
package com.sun.tools.rngom.nc;

import javax.xml.namespace.QName;

final class AnyNameClass extends NameClass {

    private static final long serialVersionUID = -5701225757278913969L;

    protected AnyNameClass() {} // no instanciation
    
    public boolean contains(QName name) {
        return true;
    }

    public int containsSpecificity(QName name) {
        return SPECIFICITY_ANY_NAME;
    }

    @Override
    public boolean equals(Object obj) {
        return obj==this;
    }

    @Override
    public int hashCode() {
        return AnyNameClass.class.hashCode();
    }

    public <V> V accept(NameClassVisitor<V> visitor) {
        return visitor.visitAnyName();
    }

    public boolean isOpen() {
        return true;
    }
    
}
