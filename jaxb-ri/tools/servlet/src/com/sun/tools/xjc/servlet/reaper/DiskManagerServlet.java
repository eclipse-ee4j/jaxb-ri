/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.servlet.reaper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Launches {@link Compiler} from the form input.
 * 
 * @author
 *  Ryan Shoemaker
 */
public class DiskManagerServlet extends HttpServlet {

    // each compiler session will have a tmp directory whose name 
    // begins with this string
    static final String SESSION_DIR_PREFIX = "jaxbOnTheWeb";

    // session directories will be considered "stale" when their
    // last modification time is more than this many milliseconds 
    // ago.
    public static final long TIMEOUT = 1000 * 60 * 60;

    // the background thread that periodically wakes up and looks
    // for disk space to reclaim.
    private static Reaper reaperThread;

    // this is the root output directory for all client sessions
    private static File rootTmpDir;

    // text used for the reset button and parameter name
    private static final String RESET_BUTTON = "Reset";

    /**
     * Allocates a new temporary directory for anyone who needs
     * time-limited quota-controlled disk storage area.
     */
    public synchronized static File createOutDir() throws IOException {
        File outDir = File.createTempFile(SESSION_DIR_PREFIX, "");
        outDir.delete();
        // to avoid another thread from stealing the unique name when one thread
        // is here, this method is made synchronized.
        outDir.mkdir();
        return outDir;
    }

    /* (non-Javadoc)
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            // get the root tmp dir
            File tmpFile = File.createTempFile("foo", "");
            rootTmpDir = tmpFile.getParentFile();
            tmpFile.delete();
 
            // create
            reaperThread = new Reaper();
            reaperThread.setPriority(Thread.MIN_PRIORITY);
            reaperThread.start();
        } catch (IOException ioe) {
            throw new ServletException(ioe);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy() {
        super.destroy();

        // cleanup the disk
        deleteAllTmpStorage();
        
        // kill the reaper thread
        reaperThread.destroy();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

        doIt(request, response);
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

        doIt(request, response);
    }

    private static int hits = 0;
    private void doIt(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String value = request.getParameter(RESET_BUTTON);
        if (value != null) {
            // we don't care about the value
            deleteAllTmpStorage(); 
        }

        // cause the reaper thread to calculate the latest stats
        reaperThread.update();
        
        // TODO: format report

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(
            "<html><body><h3>DiskManager,v1.22," + ++hits + "</h3>\n"
                + "<p>Tmp dir: "
                + reaperThread.getTmpDir()
                + "\n"
                + "<p>Last reap reclaimed: "
                + reaperThread.getReapedSpace()
                + "b\n"
                + "<p>Current disk usage: "
                + reaperThread.getDiskUsage()
                + "b\n"
                + generateResetButton()
                + "\n<hr>"
                + dumpParams( request )
                + "</body></html>");

    }

    /**
     * @param request
     * @return
     */
    private String dumpParams(HttpServletRequest request) {
        StringBuffer params = new StringBuffer();
        params.append( "<h3>params</h3>");
        for( Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String param = (String)e.nextElement();
            params.append( "name: " + param + " value: " + request.getParameter( param ) + "<br>\n" );
        }
        return params.toString();
    }

    /**
     * generate the html code for the "reset" button.
     * 
     * @return the button code
     */
    private String generateResetButton() {
        StringBuffer resetCode = new StringBuffer();
        resetCode.append("<p>");
        resetCode.append("\tClick the 'reset' button to completely erase all tmp JAXB files\n");
//        resetCode.append("\t<form method=\"post\" action=\"diskmanager?" + RESET_BUTTON + "=true\">\n");
        resetCode.append("\t<form method=\"post\" action=\"diskmanager\">\n");
        resetCode.append("\t\t<input type=\"SUBMIT\" name=\"" + RESET_BUTTON + "\" value=\"" + RESET_BUTTON + "\">\n");
        resetCode.append("\t</form>\n");
        resetCode.append("</p>\n");
        return resetCode.toString();
    }

    /**
     * Accessor for the root directory containing all of the tmp files.
     * 
     * @return the root tmp directory 
     */
    public static File getRootTmpDir() {
        return rootTmpDir;
    }

    /**
     * causes all temp storage to be deleted.  This method is called when 
     * someone clicks the "reset" button in the DiskManager page and when
     * the destroy method is invoked. 
     */
    private void deleteAllTmpStorage() {
        reaperThread.deleteAll();
    }

}
