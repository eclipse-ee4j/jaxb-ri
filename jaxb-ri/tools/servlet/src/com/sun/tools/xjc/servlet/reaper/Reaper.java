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
import java.io.FileFilter;
import java.io.IOException;

/**
 * This thread is used by the DiskManagerServlet to periodically
 * clean up temporary files created by XJCServlet and JavaDocServlet.
 */
class Reaper extends Thread {

    // the root directory to reap
    private final File rootTmpDir;

    // disk space usage
    private long diskUsage;

    // amount of disk space reaped on the current sweep
    private long reapedSpace;

    // how often the reaper thread should wake up and run (in milliseconds)
    private static final long reapInterval = 1000 * 60 * 5; // 5 min.
    
    /*
     * initialize the reaper
     */
    Reaper() throws IOException {
        super("JAXB servlet disk reaper");
        rootTmpDir = DiskManagerServlet.getRootTmpDir();
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (true) {
            try {
                // reap stale sessions
                long currentReap = reapStaleSessions();
                if( currentReap != 0 ) reapedSpace = currentReap;
                
                // calculate the disk usage
                diskUsage = calculateTotalDiskUsage();

                // sleep
                Thread.sleep( reapInterval );
            } catch (InterruptedException ignored) {
            }
        }

    }

    /**
     * Traverse tmpDir looking for directories that are older than the
     * timout value and reap them.
     * 
     * @return the amount of disk space freeded by the operation
     */
    private long reapStaleSessions() {
        File[] sessions = getJAXBSessions();
        
        long reapedSpace = 0;
        for( int i = 0; i < sessions.length; i++ ) {
            if( isStale( sessions[i] ) ) {
                // add up the disk usage
                reapedSpace += calculateDirSize( sessions[i] );
                
                // and reap it
                deleteDir( sessions[i] );
            }
        }
        
        return reapedSpace;
    }

    /*
     * find all directories whose name begins with the session prefix
     */    
    private File[] getJAXBSessions() {
        File[] sessions = rootTmpDir.listFiles( new FileFilter() {
            public boolean accept(File pathname) {
                if( pathname.isDirectory() && pathname.getName().startsWith( DiskManagerServlet.SESSION_DIR_PREFIX )) {
                    return true;
                }
                return false;
            }
        });
        return sessions;
    }
    

    /**
     * determine if the specified directory has been modified within
     * a specified timeout
     * 
     * @param dir the directory
     * @return true if the file is older than the timeout
     */
    private boolean isStale(File dir) {
        return (System.currentTimeMillis() - dir.lastModified()) > DiskManagerServlet.TIMEOUT;
    }

    /**
     * recursively delete the directory
     * 
     * @param dir the directory to delete
     */
    protected void deleteDir(File dir) {
        File[] files = dir.listFiles();
        for( int i = 0; i < files.length; i++ ) {
            if( files[i].isDirectory() ) {
                deleteDir( files[i] );
            } else {
                files[i].delete();
            }
        }
        dir.delete();
    }

    /**
     * Find and delete all jaxb directories in the root tmp dir
     */
    protected void deleteAll() {
        File[] sessions = getJAXBSessions();
        for( int i = 0; i < sessions.length; i++ ) {
            deleteDir( sessions[i] );
        }
    }

    /**
     * Calculate the number of bytes of disk space being used under
     * the specified directory.
     * 
     * @return the disk usage in bytes
     */
    private long calculateDirSize(File directory) {
        File[] files = directory.listFiles();
        long usage = 0;
        
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                usage += calculateDirSize(files[i]);
            } else {
                usage += files[i].length();
            }
        }
        return usage;
    }

    private long calculateTotalDiskUsage() {
        File[] sessions = getJAXBSessions();
        long bytes = 0;

        for( int i = 0; i < sessions.length; i++ ) {
            bytes += calculateDirSize( sessions[i] );
        }
        
        return bytes;
    }
    /**
     * @return
     */
    public long getDiskUsage() {
        return diskUsage;
    }

    /**
     * @return
     */
    public long getReapedSpace() {
        return reapedSpace;
    }

    /**
     * @return
     */
    public File getTmpDir() {
        return rootTmpDir;
    }

    /*
     * testing purposes only
     */
    public static void main( String[] args ) throws Exception {
        Reaper r = new Reaper();
        System.out.println( r.calculateDirSize( new File( args[0] ) ) );
    }

    /**
     * re-calculate disk usage - this method is called everytime the 
     * DiskManagerServlet is run
     */
    protected void update() {
        // calculate the disk usage
        diskUsage = calculateTotalDiskUsage();
    }

}
