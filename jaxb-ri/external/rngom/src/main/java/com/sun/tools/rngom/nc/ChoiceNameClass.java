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

public class ChoiceNameClass extends NameClass {

    private static final long serialVersionUID = 6131402436437748085L;

    private final NameClass nameClass1;
    private final NameClass nameClass2;

    public ChoiceNameClass(NameClass nameClass1, NameClass nameClass2) {
        this.nameClass1 = nameClass1;
        this.nameClass2 = nameClass2;
    }

    public boolean contains(QName name) {
        return (nameClass1.contains(name) || nameClass2.contains(name));
    }

    public int containsSpecificity(QName name) {
        return Math.max(
            nameClass1.containsSpecificity(name),
            nameClass2.containsSpecificity(name));
    }

    @Override
    public int hashCode() {
        return nameClass1.hashCode() ^ nameClass2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ChoiceNameClass))
            return false;
        ChoiceNameClass other = (ChoiceNameClass) obj;
        return (
            nameClass1.equals(other.nameClass1)
                && nameClass2.equals(other.nameClass2));
    }

    public <V> V accept(NameClassVisitor<V> visitor) {
        return visitor.visitChoice(nameClass1, nameClass2);
    }

    public boolean isOpen() {
        return nameClass1.isOpen() || nameClass2.isOpen();
    }
}
