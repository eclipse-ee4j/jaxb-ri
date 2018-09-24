/*
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

import com.sun.tools.rngom.nc.ChoiceNameClass;
import com.sun.tools.rngom.nc.NameClass;

class Alphabet {
  private NameClass nameClass;

  boolean isEmpty() {
    return nameClass == null;
  }

  void addElement(NameClass nc) {
    if (nameClass == null)
      nameClass = nc;
    else if (nc != null)
      nameClass = new ChoiceNameClass(nameClass, nc);
  }

  void addAlphabet(Alphabet a) {
    addElement(a.nameClass);
  }

  void checkOverlap(Alphabet a) throws RestrictionViolationException {
    if (nameClass != null
	&& a.nameClass != null
	&& nameClass.hasOverlapWith(a.nameClass))
      throw new RestrictionViolationException("interleave_element_overlap");
  }
}
