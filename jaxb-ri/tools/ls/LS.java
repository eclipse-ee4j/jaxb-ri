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

/**
 * Creates a list of files in a directory.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class LS {

    public static void main(String[] args) {
        File f = new File(args[0]);
        File[] children = f.listFiles();
        for( int i=0; i<children.length; i++ ) {
            File c = children[i];
            if(c.isFile())
                System.out.println(c.getName());
        }
    }
}
