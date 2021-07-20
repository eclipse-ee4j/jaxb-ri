/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.File;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 *
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class DTDSample {

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
        // from DTD. So I'll just do the basic operation
        // to show that it actually feels exactly the same no matter
        // what schema language you use.

        JAXBContext context = JAXBContext.newInstance("foo.jaxb");

        // unmarshal a file. Just like you've always been doing.
        Object o = context.createUnmarshaller().unmarshal(new File(fileName));

        // marshal it. Nothing new.
        Marshaller m = context.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        m.marshal(o,System.out);
    }
}
