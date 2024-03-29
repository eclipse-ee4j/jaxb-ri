/*
 * Copyright (c) 2022 Eclipse Foundation
 * Copyright (C) 2004-2015
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

import com.sun.tools.rngom.ast.om.ParsedNameClass;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Name class is a set of {@link QName}s.
 */
public abstract class NameClass implements ParsedNameClass, Serializable {
    private static final long serialVersionUID = -8327594277689361653L;
    static final int SPECIFICITY_NONE = -1;
    static final int SPECIFICITY_ANY_NAME = 0;
    static final int SPECIFICITY_NS_NAME = 1;
    static final int SPECIFICITY_NAME = 2;
    
    /**
     * Returns true if the given {@link QName} is a valid name
     * for this QName.
     */
    public abstract boolean contains(QName name);
    
    public abstract int containsSpecificity(QName name);
    
    /**
     * Visitor pattern support.
     */
    public abstract <V> V accept(NameClassVisitor<V> visitor);
    
    /**
     * Returns true if the name class accepts infinite number of
     * {@link QName}s.
     * 
     * <p>
     * Intuitively, this method returns true if the name class is
     * some sort of wildcard.
     */
    public abstract boolean isOpen();

    /**
     * If the name class is closed (IOW !{@link #isOpen()}),
     * return the set of names in this name class. Otherwise the behavior
     * is undefined.
     */
    public Set<QName> listNames() {
        final Set<QName> names = new HashSet<>();
        accept(new NameClassWalker() {
            @Override
            public Void visitName(QName name) {
                names.add(name);
                return null;
            }
        });
        return names;
    }

    /**
     * Returns true if the intersection between this name class
     * and the specified name class is non-empty.
     */
    public final boolean hasOverlapWith( NameClass nc2 ) {
        return OverlapDetector.overlap(this,nc2);
    }

    
    /** Sigleton instance that represents "anyName". */
    public static final NameClass ANY = new AnyNameClass();
    
    /**
     * Sigleton instance that accepts no name.
     *
     * <p>
     * This instance is useful when doing boolean arithmetic over
     * name classes (such as computing an inverse of a given name class, etc),
     * even though it can never appear in a RELAX NG surface syntax.
     *
     * <p>
     * Internally, this instance is also used for:
     * <ol>
     *  <li>Used to recover from errors during parsing.
     *  <li>Mark element patterns with {@code <notAllowed/>} content model.
     * </ol>
     */
    public static final NameClass NULL = new NullNameClass();
}
