/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * Server program that displays the messages sent from clients.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class TestServer implements Runnable {

    private final XMLInputFactory xif;

    public TestServer() {
        this.xif = XMLInputFactory.newInstance();
    }


    public void run() {
        try {
            ServerSocket ss = new ServerSocket(38247);
            JAXBContext context = JAXBContext.newInstance("message");

            // notify test driver that we are ready to accept
            synchronized( Test.lock ) {
                Test.ready = true;
                Test.lock.notifyAll();
            }
            
            while(true) {
                new Worker(ss.accept(),context).start();
            }
        } catch( Exception e ) {
            e.printStackTrace();
        }
        die();
    }
    
    class Worker extends Thread {
        private final XMLEventReader xer;
        private final Unmarshaller unmarshaller;
        
        Worker( Socket socket, JAXBContext context ) throws IOException, JAXBException, XMLStreamException {
            System.out.println("accepted a connection from a client");
            synchronized(TestServer.this) {
                xer = xif.createXMLEventReader(socket.getInputStream());
            }
            this.unmarshaller = context.createUnmarshaller();
        }
        
        public void run() {
            try {
                xer.nextEvent();    // read the start document
                xer.nextTag(); // get to the first <conversation> tag, and skip

                while( xer.peek().isStartElement() ) {
                    // unmarshal a new object
                    JAXBElement<String> msg = (JAXBElement<String>)unmarshaller.unmarshal(xer);
                    System.out.println("Message: "+ msg.getValue());
                }
                System.out.println("Bye!");
                xer.close();

                die();
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    private void die() {
        // notify the driver that we are done processing
        synchronized( Test.lock ) {
            Test.ready = true;
            Test.lock.notifyAll();
        }
    }
}
