/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Reports the usage to us.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class Dialer extends Thread {
    
    private final String remoteHost;
    private final Compiler compiler;
    
    public Dialer( Compiler compiler, String remoteHost ) {
        this.compiler = compiler;
        this.remoteHost = remoteHost;
    }
    
    /**
     * Phones home.
     */
    public void run() {
        try {
            System.out.println("JAXB on the web phones home...");
            Properties props = new Properties();
            props.put("mail.smtp.host", Mode.mailServer); 
            Session session = Session.getDefaultInstance(props);
        
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("kohsuke.kawaguchi@sun.com","JAXB on the web"));
        
            msg.setRecipient(Message.RecipientType.TO,
                new InternetAddress(Mode.homeAddress));
            
            Multipart mp = new MimeMultipart();
            
            {// main message
                MimeBodyPart body = new MimeBodyPart();
                body.setText(
                    "Remote IP: "+remoteHost+"\n" );
                mp.addBodyPart(body);
            }
            
            {// status message part
                MimeBodyPart status = new MimeBodyPart();
                status.setText( compiler.getStatusMessages() );
                mp.addBodyPart(status);
            }
            
            // TODO: add other parts
            
            msg.setContent(mp);
            msg.setSentDate(new Date());
            
            msg.setSubject("JAXBotW: phone home");
            
            Transport.send(msg);    // send the message 
        } catch (MessagingException e) {
            e.printStackTrace();
            // TODO: can we do something? maybe log an error?
        } catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            // can't happen
        }
    }
}
