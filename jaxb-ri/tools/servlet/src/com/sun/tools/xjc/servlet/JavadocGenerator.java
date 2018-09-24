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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates Javadoc from all the source code in a zip file.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class JavadocGenerator {
    private final File outDir;
    private final List args = new ArrayList();
    
    /**
     * Executes javadoc for source code in the "inDir" directory
     * and produces HTML t the "outDir" directory.
     * Any message from javadoc will be sent to the "out" stream. 
     */
    public static int process( File inDir, File outDir, OutputStream out ) throws Exception {
        return new JavadocGenerator(outDir)._process(inDir,out);
    }
    
    public JavadocGenerator( File _outDir ) throws IOException {
        this.outDir = _outDir;
        
        args.add("javadoc");
        args.add("-d");
        args.add( outDir.getPath() );
        args.add("-linkoffline");
        args.add("http://java.sun.com/webservices/docs/1.1/api/");
        args.add(getPackageListLocation("javadoc/jwsdp/package-list"));
        args.add("-linkoffline");
        args.add("http://java.sun.com/j2se/1.4/docs/api"); 
        args.add(getPackageListLocation("javadoc/j2se/package-list"));
    }

    private String getPackageListLocation(String path) throws IOException {
        String name = this.getClass().getResource(path).toExternalForm();
        return name.substring(0, name.length()-12/*lengthOf("package-list")*/);
    }
    
    private int _process( File inDir, OutputStream out ) throws Exception {
        listJavaFiles(inDir);

        Process proc = Runtime.getRuntime().exec( (String[]) args.toArray(new String[args.size()]) );
        return execProcess(proc,out);
    }

    private void listJavaFiles( File dir ) throws IOException {
        File[] files = dir.listFiles();
        
        for( int i=0; i<files.length; i++ ) {
            File f = files[i];
            if( f.isDirectory() )
                listJavaFiles(f);
            else
            if( f.getName().endsWith(".java") )
                args.add( f.getPath() );
        }
    }
    
    
    /**
     * Waits for the given process to complete, and return its exit code.
     */
    private static int execProcess( Process proc, OutputStream out ) throws IOException, InterruptedException {
        // is this a correct handling?
        proc.getOutputStream().close();
        new Thread(new ProcessReader(proc.getInputStream(),out)).start();
        new Thread(new ProcessReader(proc.getErrorStream(),out)).start();
            
        return proc.waitFor();
    }

    /**
     * Reads an input stream and copies them into the given output stream.
     */
    private static class ProcessReader implements Runnable {

        ProcessReader( InputStream is, OutputStream out ) {
            this.in = new BufferedInputStream(is);
            this.out = out;
        }
        
        private final BufferedInputStream in;
        private final OutputStream out;
        
        public void run() {
            try {
                byte[] buf = new byte[256];
                while(true) {
                    int len = in.read(buf);
                    if(len==-1) {
                        in.close();
                        // don't close "out".
                        return;
                    }
                    out.write(buf,0,len);
                }
            } catch( Exception e ) {
                e.printStackTrace();
                throw new Error();
            }
        }
    }
    
    /**
     * For quick testing.
     */
    public static void main( String[] args ) throws Exception {
        JavadocGenerator.process( new File(args[0]), new File(args[1]), System.out );
    }
    
}
