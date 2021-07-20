/*
 * Copyright (c) 2017, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author Yan GAO (gaoyan.gao@oracle.com)
 */
public class AntExecutor {
    private static boolean DEBUG = Boolean.getBoolean("anttasks.debug");
    private static String DEBUG_PORT = "5432";
    private static boolean PROFILE = Boolean.getBoolean("anttasks.profile");

    public static int exec(File script, String... targets) throws IOException {
        File heapDump = null;
        List<String> cmd = new ArrayList<String>();
        cmd.add("java");
        if (DEBUG) {
            cmd.add("-Xdebug");
            cmd.add("-Xnoagent");
            cmd.add("-Xrunjdwp:transport=dt_socket,address=" + DEBUG_PORT + ",server=y,suspend=y");
        } else if (PROFILE) {
            heapDump = File.createTempFile(script.getName(), ".hprof", new File(System.getProperty("user.home")));
            cmd.add("-agentlib:hprof=heap=dump,file=" + heapDump.getAbsolutePath() + ",format=b");
        }
        cmd.add("-Dbin.folder=" + System.getProperty("bin.folder"));
//        cmd.add("-Djaxp.debug=true");
        cmd.add("-cp");
        cmd.add(getAntCP(new File(System.getProperty("bin.folder"), "lib/ant")));
        cmd.add("org.apache.tools.ant.Main");
//        cmd.add("-d");
        cmd.add("-f");
        cmd.add(script.getName());
        cmd.addAll(Arrays.asList(targets));
        ProcessBuilder pb = new ProcessBuilder(cmd).directory(script.getParentFile());
        Process p = pb.start();
        new InOut(p.getInputStream(), System.out).start();
        new InOut(p.getErrorStream(), System.out).start();
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            // ignore
        }
        if (PROFILE) {
            System.out.println("Heap dump (in binary format): " + heapDump.getAbsolutePath());
        }
        return p.exitValue();
    }

    private static String getAntCP(File dir) {
        StringBuilder path = new StringBuilder();
        for (File jar : dir.listFiles()) {
            path.append(jar.getAbsolutePath());
            path.append(File.pathSeparator);
        }
        return path.substring(0, path.length() - 1);
    }

    private static class InOut extends Thread {

        private InputStream is;
        private PrintStream out;

        public InOut(InputStream is, PrintStream out) {
            this.is = is;
            this.out = out;
        }

        @Override
        public void run() {
            Scanner sc = new Scanner(is);
            while (sc.hasNextLine()) {
                out.println(sc.nextLine());
            }
        }
    }
}
