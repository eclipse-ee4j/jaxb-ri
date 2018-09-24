/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.File;

import javax.xml.bind.JAXBContext;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class RELAXNGExample {

    public static void main(String[] args) throws Exception {
        // in this example, I skip the error check entirely
        // for the sake of simplicity. In reality, you should
        // do a better job of handling errors.
        for( int i=0; i<args.length; i++ ) {
            test(args[i]);
        }
    }
    
    private static void test( String fileName ) throws Exception {
        
        // there's really nothing special about the code generated
        // from RELAX NG. So I'll just do the basic operation
        // to show that it actually feels exactly the same no matter
        // what schema language you use.
        
        JAXBContext context = JAXBContext.newInstance("formula");
        
        // unmarshal a file. Just like you've always been doing.
        Object o = context.createUnmarshaller().unmarshal(new File(fileName)); 
        
        // valdiate it. Again, the same procedure regardless of the schema language
        context.createValidator().validate(o);
        
        // marshal it. Nothing new.
        context.createMarshaller().marshal(o,System.out);
    }
}
