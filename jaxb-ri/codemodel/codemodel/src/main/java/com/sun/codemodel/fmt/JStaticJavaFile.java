/*
 * Copyright (c) 1997, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel.fmt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JResourceFile;
import com.sun.codemodel.JTypeVar;

/**
 * Statically generated Java soruce file.
 *
 * <p>
 * This {@link JResourceFile} implementation will generate a Java source
 * file by copying the source code from a resource.
 * <p>
 * While copying a resource, we look for a package declaration and
 * replace it with the target package name. This allows the static Java
 * source code to have an arbitrary package declaration.
 * <p>
 * You can also use the getJClass method to obtain a {@link JClass}
 * object that represents the static file. This allows the client code
 * to refer to the class from other CodeModel generated code.
 * <p>
 * Note that because we don't parse the static Java source code,
 * the returned {@link JClass} object doesn't respond to methods like
 * "isInterface" or "_extends",
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class JStaticJavaFile extends JResourceFile {

    private final JPackage pkg;
    private final String className;
    private final ResourceLoader source;
    private final JStaticClass clazz;
    private final LineFilter filter;

    public JStaticJavaFile(JPackage _pkg, String _className, Class<?> loadingClass, LineFilter _filter) {
        super(_className + ".java");
        if (loadingClass == null) throw new NullPointerException();
        this.pkg = _pkg;
        this.clazz = new JStaticClass();
        this.className = _className;
        this.source = new ResourceLoader(_className, loadingClass);
        this.filter = _filter;
    }

    /**
     * Returns a class object that represents a statically generated code.
     */
    public final JClass getJClass() {
        return clazz;
    }

    protected boolean isResource() {
        return false;
    }

    protected  void build(OutputStream os) throws IOException {
        int lineNumber=1;
        try (
                InputStream is = source.getResourceAsStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                PrintWriter w = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
        ) {
            LineFilter filter = createLineFilter();
            String line;
            while((line=r.readLine())!=null) {
                line = filter.process(line);
                if(line!=null)
                    w.println(line);
                lineNumber++;
            }
        } catch( ParseException e ) {
            throw new IOException("unable to process "+source+" line:"+lineNumber+"\n"+e.getMessage());
        }
    }

    /**
     * Creates a {@link LineFilter}.
     * <p>
     * A derived class can override this method to process
     * the contents of the source file.
     */
    private LineFilter createLineFilter() {
        // this filter replaces the package declaration.
        LineFilter f = new LineFilter() {
            public String process(String line) {
                if(!line.startsWith("package ")) return line;

                // replace package decl
                if( pkg.isUnnamed() )
                    return null;
                else
                    return "package "+pkg.name()+";";
            }
        };
        if( filter!=null )
            return new ChainFilter(filter,f);
        else
            return f;
    }

    /**
     * Filter that alters the Java source code.
     * <p>
     * By implementing this interface, derived classes
     * can modify the Java source file before it's written out.
     */
    public interface LineFilter {
        /**
         * @param line
         *      a non-null valid String that corresponds to one line.
         *      No '\n' included.
         * @return
         *      null to strip the line off. Otherwise the returned
         *      String will be written out. Do not add '\n' at the end
         *      of this string.
         *
         * @exception ParseException
         *      when for some reason there's an error in the line.
         */
        String process(String line) throws ParseException;
    }

    /**
     * A {@link LineFilter} that combines two {@link LineFilter}s.
     */
    public static final class ChainFilter implements LineFilter {
        private final LineFilter first,second;
        public ChainFilter( LineFilter first, LineFilter second ) {
            this.first=first;
            this.second=second;
        }
        public String process(String line) throws ParseException {
            line = first.process(line);
            if(line==null)  return null;
            return second.process(line);
        }
    }


    private class JStaticClass extends JClass {

        private final JTypeVar[] typeParams;

        JStaticClass() {
            super(pkg.owner());
            // TODO: allow those to be specified
            typeParams = new JTypeVar[0];
        }

        public String name() {
            return className;
        }

        public String fullName() {
            if(pkg.isUnnamed())
                return className;
            else
                return pkg.name()+'.'+className;
        }

        public JPackage _package() {
            return pkg;
        }

        public JClass _extends() {
            throw new UnsupportedOperationException();
        }

        public Iterator<JClass> _implements() {
            throw new UnsupportedOperationException();
        }

        public boolean isInterface() {
            throw new UnsupportedOperationException();
        }

        public boolean isAbstract() {
            throw new UnsupportedOperationException();
        }

        public JTypeVar[] typeParams() {
            return typeParams;
        }

        protected JClass substituteParams(JTypeVar[] variables, List<JClass> bindings) {
            return this;
        }
    }

    static class ResourceLoader {
        Class<?> loadingClass;
        String shortName;

        ResourceLoader(String shortName, Class<?> loadingClass) {
            this.loadingClass = loadingClass;
            this.shortName = shortName;
        }

        InputStream getResourceAsStream() {
            // some people didn't like our jars to contain files with .java extension,
            // so when we build jars, we'' use ".java_". But when we run from the workspace,
            // we want the original source code to be used, so we check both here.
            // see bug 6211503.
            InputStream stream = loadingClass.getResourceAsStream(shortName + ".java");
            if (stream == null) {
                stream = loadingClass.getResourceAsStream(shortName + ".java_");
            }
            if (stream == null) {
                throw new InternalError("Unable to load source code of " + loadingClass.getName() + " as a resource");
            }
            return stream;
        }

    }

}
