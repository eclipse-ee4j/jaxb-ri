/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Test {

    public static Object lock = new Object();
    public static boolean ready = false;
    
    public static void main(String[] args) throws Exception {
        // launch the server
        new Thread(new TestServer()).start();

        // wait for the server to become ready
        while( !ready ) {
            synchronized( lock ) {
                lock.wait(1000);
            }
        }
        
        // reset the flag
        ready = false;
        
        // run the client
        new TestClient().run();

        // wait for the server to finish processing data  
        // from the client 
        while( !ready ) {
            synchronized( lock ) {
                lock.wait(1000);
            }
        }

        System.exit(0);
    }
}
