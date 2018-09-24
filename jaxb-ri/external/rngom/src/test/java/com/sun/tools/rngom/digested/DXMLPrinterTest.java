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
package com.sun.tools.rngom.digested;

import junit.framework.TestCase;
import org.custommonkey.xmlunit.XMLAssert;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class DXMLPrinterTest extends TestCase {
    @SuppressWarnings("CallToThreadDumpStack")
    protected void test(String resource) throws Exception {
        System.out.println(resource);
        String in = "src/test/java/" + getClass().getPackage().getName().replace('.', '/') + '/' + resource;
        String out = in + ".out";
        try {
            DXMLPrinter.main(new String[]{in, out});
            if (!in.endsWith(".rng"))
            	in += ".rng";
            Reader input = new FileReader(in);
            Reader output = new FileReader(out);
            XMLAssert.assertXMLEqual(input, output);
            input.close();
            output.close();
            new File(out).delete();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue("Unexpected exception", false);
        }
    }

    public void testXmlNS() throws Exception {
        test("xmlns.rng");
    }

}
