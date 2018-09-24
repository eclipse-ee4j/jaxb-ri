/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * Ant task that adds licence header to java source files.
 *
 * @author Martin Grebac (martin.grebac@oracle.com)
 */
public class LicenceTask extends Task {

    private File licence = null;

    private Vector filesets = new Vector();
    
    public LicenceTask() {
    }

    public void setLicence(File licence) {
        this.licence = licence;
    }
    
    @Override
    public void setProject(Project project) {
        super.setProject(project);
    }

    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }
    
    @Override
    public void execute() throws BuildException {
        
        Iterator iter = filesets.iterator();
        while (iter.hasNext()) {
            FileSet fset = (FileSet) iter.next();
            DirectoryScanner ds = fset.getDirectoryScanner(project);
            File dir = ds.getBasedir();
            String[] filesInSet = ds.getIncludedFiles();
            for (String filename : filesInSet) {
                System.out.println("Applying licence header on: " + filename);
                File file = new File(dir,filename);

                String licenceHeader = readFile(licence);
                String javafile = readFile(file);

                if (javafile.contains(licenceHeader)) {
                    System.out.println("Licence already present, no changed made.");
                    continue;
                }

                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(file);
                pw.write(licenceHeader);
                pw.write(javafile);

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(LicenceTask.class.getName()).log(Level.SEVERE, null, ex);
                } finally { 
                    if (pw != null) {
                        pw.close();
                    }
                }
            }
        }
    }

    private String readFile(File file) {
        String licenceHeader = null;
        LineNumberReader lnr = null;
        FileReader fr = null;
        try {
            fr = new FileReader(file);
            lnr = new LineNumberReader(fr);
            licenceHeader = lnr.readLine();
            do {
                licenceHeader += "\n";
                String line = lnr.readLine();
                if (line == null) {
                    break;
                } else {
                    licenceHeader += line;
                }
            } while (true);
        } catch (IOException ex) {
            if (lnr != null) {
                try {
                    lnr.close();
                } catch (IOException ex1) {
                    Logger.getLogger(LicenceTask.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException ex1) {
                    Logger.getLogger(LicenceTask.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            Logger.getLogger(LicenceTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return licenceHeader;
    }
    
}
