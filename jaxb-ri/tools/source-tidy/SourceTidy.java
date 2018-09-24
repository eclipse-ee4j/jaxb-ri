/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.regex.Pattern;

/**
 * Clean up the specified Java source files.
 * 
 * Specifically, we currently:
 * <ul>
 *  <li>strips TODO comments
 * </ul>
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SourceTidy {
    
    private static final Pattern todoComment = Pattern.compile(".*//.*TODO.*");

    public static void main(String[] args) throws IOException {
        for( int i=0; i<args.length; i++ )
            process(new File(args[i]));
    }
    
    private static void process( File file ) throws IOException {
        if( file.isDirectory() ) {
            File[] files = file.listFiles();
            for( int i=0; i<files.length; i++ )
            	process(files[i]);
        } else
            processFile(file);
    }
    
    private static void processFile( File file ) throws IOException {
        if( !file.getName().endsWith(".java") )
            return; // skip
        
        // System.out.println("processing "+file);
        
        BufferedReader in = new BufferedReader(new FileReader(file));
        File tmp = new File(file.getPath()+".tmp");
        
        try {
            PrintWriter out = new PrintWriter(new FileWriter(tmp));
            
            String line;
            do {
                line = in.readLine();
            } while( !line.startsWith("package") );
            
            out.println(line);
            
            while((line=in.readLine())!=null) {
                if( todoComment.matcher(line).matches() )
                    ;   // ignore this line
                else
                    out.println(line);
            }
            
            in.close();
            out.close();
        } catch( IOException e ) {
            tmp.delete();
            throw e;
        }
        
        // replace in with out
        file.delete();
        tmp.renameTo(file);
    }
}
