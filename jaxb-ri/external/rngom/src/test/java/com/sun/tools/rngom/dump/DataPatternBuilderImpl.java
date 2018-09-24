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
package com.sun.tools.rngom.dump;

import com.sun.tools.rngom.ast.builder.Annotations;
import com.sun.tools.rngom.ast.om.Location;
import com.sun.tools.rngom.ast.om.ParsedElementAnnotation;
import com.sun.tools.rngom.ast.om.ParsedPattern;
import com.sun.tools.rngom.parse.Context;
import com.sun.tools.rngom.ast.builder.BuildException;
import com.sun.tools.rngom.ast.builder.DataPatternBuilder;

/**
 * 
 * @author
 *      Kohsuke Kawaguchi (kk@kohsuke.org)
 */
public class DataPatternBuilderImpl extends Base implements DataPatternBuilder {
    public DataPatternBuilderImpl(Factory f, Printer p, int id) {
        super(f, p, id);
    }

    protected String prefix() {
        return "dtb";
    }

    public void addParam(String name, String value, Context context, String ns, Location loc, Annotations anno) throws BuildException {
        out("addParam").param(name).param(value).param(ns).param(loc).param(anno).result();
    }

    public void annotation(ParsedElementAnnotation ea) {
        out("annotation").param(ea).result();
    }

    public ParsedPattern makePattern(Location loc, Annotations anno) throws BuildException {
        out("makePattern").param(loc).param(anno);
        return printer.result(factory.createPattern());
    }

    public ParsedPattern makePattern(ParsedPattern except, Location loc, Annotations anno) throws BuildException {
        out("makePattern").param(except).param(loc).param(anno);
        return printer.result(factory.createPattern());
    }
}
