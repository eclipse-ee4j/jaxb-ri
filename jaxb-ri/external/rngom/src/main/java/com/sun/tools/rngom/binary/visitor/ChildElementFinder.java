/*
 * Copyright (c) 2022, 2026 Eclipse Foundation
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
package com.sun.tools.rngom.binary.visitor;

import com.sun.tools.rngom.binary.Pattern;
import com.sun.tools.rngom.nc.NameClass;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Visits a pattern and creates a list of possible child elements.
 *
 * <p>
 * One can use a similar technique to introspect a pattern.
 *
 * @author Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class ChildElementFinder extends PatternWalker {

    private final Set children = new HashSet();

    /**
     * Represents a child element.
     */
    public static class Element {
        public final NameClass nc;
        public final Pattern content;

        public Element(NameClass nc, Pattern content) {
            this.nc = nc;
            this.content = content;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Element element)) return false;

            if (!Objects.equals(content, element.content)) return false;
            if (!Objects.equals(nc, element.nc)) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (nc != null ? nc.hashCode() : 0);
            result = 29 * result + (content != null ? content.hashCode() : 0);
            return result;
        }
    }

    /**
     * Returns a set of {@link Element}.
     */
    public Set getChildren() {
        return children;
    }

    public void visitElement(NameClass nc, Pattern content) {
        children.add(new Element(nc,content));
    }

    public void visitAttribute(NameClass ns, Pattern value) {
        // there will be no element inside attribute,
        // so don't go in there.
    }

    public void visitList(Pattern p) {
        // there will be no element inside a list,
        // so don't go in there.
    }
}
