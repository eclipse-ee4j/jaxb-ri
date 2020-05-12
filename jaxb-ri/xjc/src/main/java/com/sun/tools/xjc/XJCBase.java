/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc;

import org.apache.tools.ant.taskdefs.MatchingTask;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FilterCodeWriter;
import com.sun.istack.tools.DefaultAuthenticator;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.reader.Util;
import com.sun.tools.xjc.util.ForkEntityResolver;
import com.sun.tools.xjc.api.SpecVersion;
import org.glassfish.jaxb.core.v2.util.EditDistance;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * @author Yan GAO (gaoyan.gao@oracle.com)
 */
public class XJCBase extends MatchingTask {
    public XJCBase() {
        super();
        classpath = new Path(null);
        options.setSchemaLanguage(Language.XMLSCHEMA);  // disable auto-guessing
    }

    private Path modulepath = null;

    public void setModulepath(Path mp) {
        this.modulepath = mp;
    }

    public Path getModulepath() {
        return this.modulepath;
    }

    private Path upgrademodulepath = null;

    public void setUpgrademodulepath(Path ump) {
        this.upgrademodulepath = ump;
    }

    public Path getUpgrademodulepath() {
        return this.upgrademodulepath;
    }

    private String addmodules = null;

    public void setAddmodules(String ams) {
        this.addmodules = ams;
    }

    public String getAddmodules() {
        return this.addmodules;
    }

    private String limitmodules = null;

    public void setLimitmodules(String lms) {
        this.limitmodules = lms;
    }

    public String getLimitmodules() {
        return this.limitmodules;
    }

    private String addreads = null;

    public void setAddreads(String ars) {
        this.addreads = ars;
    }

    public String getAddreads() {
        return this.addreads;
    }

    private String addexports = null;

    public void setAddexports(String aes) {
        this.addexports = aes;
    }

    public String getAddexports() {
        return this.addexports;
    }

    private String patchmodule = null;

    public void setPatchmodule(String pms) {
        this.patchmodule = pms;
    }

    public String getPatchmodule() {
        return this.patchmodule;
    }

    private String addopens = null;

    public void setAddopens(String aos) {
        this.addopens = aos;
    }

    public String getAddopens() {
        return this.addopens;
    }

    public final Options options = new Options();

    /**
     * User-specified stack size.
     */
    private long stackSize = -1;

    /**
     * False to continue the build even if the compilation fails.
     */
    private boolean failonerror = true;

    private final ArrayList<File> bindingFiles = new ArrayList<>();
    private final ArrayList<File> schemaFiles = new ArrayList<>();

    /**
     * True if we will remove all the old output files before
     * invoking XJC.
     */
    private boolean removeOldOutput = false;

    /**
     * Files used to determine whether XJC should run or not.
     */
    private final ArrayList<File> dependsSet = new ArrayList<>();
    private final ArrayList<File> producesSet = new ArrayList<>();

    /**
     * Set to true once the {@code <produces>} element is used.
     * This flag is used to issue a suggestion to users.
     */
    private boolean producesSpecified = false;

    /**
     * Used to load additional user-specified classes.
     */
    private final Path classpath;

    /**
     * Additional command line arguments.
     */
    private final Commandline cmdLine = new Commandline();

    /**
     * for resolving entities such as dtds
     */
    private XMLCatalog xmlCatalog = null;

    /* *********************** -fork option ************************ */
    private boolean fork = false;

    private final CommandlineJava cmd = new CommandlineJava();

    CommandlineJava getCommandline() {
        return cmd;
    }

    /**
     * Gets the "fork" flag.
     *
     * @return true if execution should be done in forked JVM, false otherwise.
     */
    public boolean getFork() {
        return fork;
    }

    /**
     * Sets the "fork" flag.
     *
     * @param fork true to run execution in a forked JVM.
     */
    public void setFork(boolean fork) {
        this.fork = fork;
    }

    /**
     * Parses the schema attribute. This attribute will be used when
     * there is only one schema.
     *
     * @param schema A file name (can be relative to base dir),
     *               or an URL (must be absolute).
     */
    public void setSchema(String schema) {
        File f;
        try {
            f = new File(schema);
            options.addGrammar(getInputSource(new URL(schema)));
        } catch (MalformedURLException e) {
            f = getProject().resolveFile(schema);
            options.addGrammar(f);
            dependsSet.add(f);
        }
        schemaFiles.add(f);
    }

    /**
     * Nested {@code <schema>} element.
     */
    public void addConfiguredSchema(FileSet fs) {
        for (InputSource value : toInputSources(fs))
            options.addGrammar(value);

        addIndividualFilesTo(fs, dependsSet);
        addIndividualFilesTo(fs, schemaFiles);
    }

    /**
     * Nested {@code <classpath>} element.
     */
    public void setClasspath(Path cp) {
        classpath.createPath().append(cp);
    }

    /**
     * Nested {@code <classpath>} element.
     */
    public Path createClasspath() {
        return classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        classpath.createPath().setRefid(r);
    }

    /**
     * Sets the schema language.
     */
    public void setLanguage(String language) {
        Language l = Language.valueOf(language.toUpperCase());
        if (l == null) {
            Language[] languages = Language.values();
            String[] candidates = new String[languages.length];
            for (int i = 0; i < candidates.length; i++)
                candidates[i] = languages[i].name();

            throw new BuildException("Unrecognized language: " + language + ". Did you mean " +
                EditDistance.findNearest(language.toUpperCase(), candidates) + " ?");
        }
        options.setSchemaLanguage(l);
    }

    /**
     * External binding file.
     */
    public void setBinding(String binding) {
        try {
            File f = new File(binding);
            bindingFiles.add(f);
            options.addBindFile(getInputSource(new URL(binding)));
        } catch (MalformedURLException e) {
            File f = getProject().resolveFile(binding);
            options.addBindFile(f);
            dependsSet.add(f);
        }
    }

    /**
     * Nested {@code <binding>} element.
     */
    public void addConfiguredBinding(FileSet fs) {
        for (InputSource is : toInputSources(fs))
            options.addBindFile(is);

        addIndividualFilesTo(fs, dependsSet);
        addIndividualFilesTo(fs, bindingFiles);
    }

    /**
     * Sets the package name of the generated code.
     */
    public void setPackage(String pkg) {
        this.options.defaultPackage = pkg;
    }

    public String getPackage() {
        return this.options.defaultPackage;
    }

    private File catalog;

    /**
     * Adds a new catalog file.
     */
    public void setCatalog(File catalog) {
        try {
            this.options.addCatalog(catalog);
            this.catalog = catalog;
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    public File getCatalog() {
        return this.catalog;
    }

    /**
     * Mostly for our SQE teams and not to be advertized.
     */
    public void setFailonerror(boolean value) {
        failonerror = value;
    }

    /**
     * Sets the stack size of the XJC invocation.
     *
     * @deprecated not much need for JAXB2, as we now use much less stack.
     */
    public void setStackSize(String ss) {
        try {
            stackSize = Long.parseLong(ss);
            return;
        } catch (NumberFormatException e) {
            // ignore
        }

        if (ss.length() > 2) {
            String head = ss.substring(0, ss.length() - 2);
            String tail = ss.substring(ss.length() - 2);

            if (tail.equalsIgnoreCase("kb")) {
                try {
                    stackSize = Long.parseLong(head) * 1024;
                    return;
                } catch (NumberFormatException ee) {
                    // ignore
                }
            }
            if (tail.equalsIgnoreCase("mb")) {
                try {
                    stackSize = Long.parseLong(head) * 1024 * 1024;
                    return;
                } catch (NumberFormatException ee) {
                    // ignore
                }
            }
        }

        throw new BuildException("Unrecognizable stack size: " + ss);
    }

    /**
     * Add the catalog to our internal catalog
     *
     * @param xmlCatalog the XMLCatalog instance to use to look up DTDs
     */
    public void addConfiguredXMLCatalog(XMLCatalog xmlCatalog) {
        if (this.xmlCatalog == null) {
            this.xmlCatalog = new XMLCatalog();
            this.xmlCatalog.setProject(getProject());
        }
        this.xmlCatalog.addConfiguredXMLCatalog(xmlCatalog);
    }

    /**
     * Controls whether files should be generated in read-only mode or not
     */
    public void setReadonly(boolean flg) {
        this.options.readOnly = flg;
    }

    public boolean getReadOnly() {
        return this.options.readOnly;
    }

    /**
     * Controls whether the file header comment is generated or not.
     */
    public void setHeader(boolean flg) {
        this.options.noFileHeader = !flg;
    }

    public boolean getHeader() {
        return this.options.noFileHeader;
    }

    /**
     * @see Options#runtime14
     */
    public void setXexplicitAnnotation(boolean flg) {
        this.options.runtime14 = flg;
    }

    private boolean extension = false;

    /**
     * Controls whether the compiler will run in the strict
     * conformance mode (flg=false) or the extension mode (flg=true)
     */
    public void setExtension(boolean flg) {
        extension = flg;
        if (flg)
            this.options.compatibilityMode = Options.EXTENSION;
        else
            this.options.compatibilityMode = Options.STRICT;
    }

    public boolean getExtension() {
        return extension;
    }

    private String specTarget;

    /**
     * Sets the target version of the compilation
     */
    public void setTarget(String version) {
        options.target = SpecVersion.parse(version);
        if (options.target == null)
            throw new BuildException(version + " is not a valid version number. Perhaps you meant @destdir?");
        specTarget = options.target.getVersion();
    }

    public String getSpecTarget() {
        return this.specTarget;
    }

    public boolean getVerbose() {
        return this.options.verbose;
    }

    /**
     * Sets the directory to produce generated source files.
     */
    public void setDestdir(File dir) {
        this.options.targetDir = dir;
    }

    public File getDestdir() {
        return this.options.targetDir;
    }

    public void setEncoding(String encoding) {
        this.options.encoding = encoding;
    }

    public String getEncoding() {
        return this.options.encoding;
    }

    /**
     * Nested {@code <depends>} element.
     */
    public void addConfiguredDepends(FileSet fs) {
        addIndividualFilesTo(fs, dependsSet);
    }

    /**
     * Nested {@code <produces>} element.
     */
    public void addConfiguredProduces(FileSet fs) {
        producesSpecified = true;
        if (!fs.getDir(getProject()).exists()) {
            log(
                fs.getDir(getProject()).getAbsolutePath() + " is not found and thus excluded from the dependency check",
                Project.MSG_INFO);
        } else
            addIndividualFilesTo(fs, producesSet);
    }

    /**
     * "removeOldOutput" attribute.
     */
    public void setRemoveOldOutput(boolean roo) {
        this.removeOldOutput = roo;
    }

    public boolean getRemoveOldOutput() {
        return this.removeOldOutput;
    }

    public Commandline.Argument createArg() {
        return cmdLine.createArgument();
    }

    public Commandline.Argument createJvmarg() {
        return cmd.createVmArgument();
    }

    /**
     * Set up command line to invoke.
     *
     * @return ready to run command line
     */
    protected CommandlineJava setupCommand() {
        // d option
        if (null != getDestdir() && !getDestdir().getName().equals("")) {
            getCommandline().createArgument().setValue("-d");
            getCommandline().createArgument().setFile(getDestdir());
        }
        //p option
        if (null != getPackage() && !getPackage().equals("")) {
            getCommandline().createArgument().setValue("-p");
            getCommandline().createArgument().setValue(getPackage());
        }
        // extension flag
        if (getExtension()) {
            getCommandline().createArgument().setValue("-extension");
        }
        // encoding option
        if (getEncoding() != null) {
            getCommandline().createArgument().setValue("-encoding");
            getCommandline().createArgument().setValue(getEncoding());
        }
        // readOnly option
        if (getReadOnly()) {
            getCommandline().createArgument().setValue("-readOnly");
        }
        // no-header option
        if (getHeader()) {
            getCommandline().createArgument().setValue("-no-header");
        }
        if (getRemoveOldOutput()) {
            getCommandline().createArgument().setValue("-removeOldOutput");
        }
        if (getSpecTarget() != null) {
            getCommandline().createArgument().setValue("-target");
            getCommandline().createArgument().setValue(getSpecTarget());
        }
        // verbose option
        if (getVerbose()) {
            getCommandline().createArgument().setValue("-verbose");
        }
        //catalog
        if ((getCatalog() != null) && (getCatalog().getName().length() > 0)) {
            getCommandline().createArgument().setValue("-catalog");
            getCommandline().createArgument().setFile(getCatalog());
        }
        for (String a : cmdLine.getArguments()) {
            getCommandline().createArgument().setValue(a);
        }

        addFilesToCommandLine(schemaFiles, null);

        addFilesToCommandLine(bindingFiles, "-b");

        return getCommandline();
    }

    void addFilesToCommandLine(ArrayList<File> files, String option) {
        if (!files.isEmpty()) {
            for (File file : files) {
                if (option != null && option.length() > 0) {
                    getCommandline().createArgument().setValue(option);
                }

                boolean isLink = false;
                try {
                    isLink = !file.getCanonicalPath().equals(file.getAbsolutePath())
                        && !(file.getAbsolutePath().contains("~1") &&
                        file.getCanonicalPath().indexOf(' ') >= 0);
                } catch (IOException e) {
                    // do nothing
                }

                if (isLink) {
                    getCommandline().createArgument().setValue(file.toURI().toString());
                } else
                    getCommandline().createArgument().setFile(file);
            }
        }
    }

    void setupForkCommand(String className) {
        ClassLoader loader = this.getClass().getClassLoader();
        while (loader != null && !(loader instanceof AntClassLoader)) {
            loader = loader.getParent();
        }

        Path cp = getCommandline().createClasspath(getProject());
        if (loader != null) {
            // fork from within ant
            cp.append(new Path(getProject(), ((AntClassLoader) loader).getClasspath()));
        }

        File jreHome = new File(System.getProperty("java.home"));
        File toolsJar = new File(jreHome.getParent(), "lib/tools.jar");
        if (toolsJar.exists()) {
            // on java se 8
            Path.PathElement tools = cp.createPathElement();
            tools.setPath(toolsJar.getAbsolutePath());
        }

        Path mvn = getProject().getReference("maven.plugin.classpath");
        if (mvn != null) {
            // fork in ant called from maven,
            // likely through maven-antrun-plugin:run
            cp.append(mvn);
        }

        if (getModulepath() != null && getModulepath().size() > 0) {
            getCommandline().createModulepath(getProject()).add(getModulepath());
        }
        if (getUpgrademodulepath() != null && getUpgrademodulepath().size() > 0) {
            getCommandline().createUpgrademodulepath(getProject()).add(getUpgrademodulepath());
        }
        if (getAddmodules() != null && getAddmodules().length() > 0) {
            getCommandline().createVmArgument().setLine("--add-modules " + getAddmodules());
        }
        if (getAddreads() != null && getAddreads().length() > 0) {
            getCommandline().createVmArgument().setLine("--add-reads " + getAddreads());
        }
        if (getAddexports() != null && getAddexports().length() > 0) {
            getCommandline().createVmArgument().setLine("--add-exports " + getAddexports());
        }
        if (getAddopens() != null && getAddopens().length() > 0) {
            getCommandline().createVmArgument().setLine("--add-opens " + getAddopens());
        }
        if (getPatchmodule() != null && getPatchmodule().length() > 0) {
            getCommandline().createVmArgument().setLine("--patch-module " + getPatchmodule());
        }
        if (getLimitmodules() != null && getLimitmodules().length() > 0) {
            getCommandline().createVmArgument().setLine("--limit-modules " + getLimitmodules());
        }

        getCommandline().setClassname(className);
    }

    /**
     * Runs XJC.
     */
    @Override
    public void execute() throws BuildException {

        log("build id of XJC is " + Driver.getBuildID(), Project.MSG_VERBOSE);

        classpath.setProject(getProject());

        // up to date check
        long srcTime = computeTimestampFor(dependsSet, true);
        long dstTime = computeTimestampFor(producesSet, false);
        log("the last modified time of the inputs is  " + srcTime, Project.MSG_VERBOSE);
        log("the last modified time of the outputs is " + dstTime, Project.MSG_VERBOSE);

        if (srcTime < dstTime) {
            log("files are up to date");
            return;
        }

        try {
            if (getFork()) {
                setupCommand();
                setupForkCommand("com.sun.tools.xjc.XJCFacade");
                int status = run(getCommandline().getCommandline());
                if (status != 0) {
                    log("Command invoked: xjc " + getCommandline().toString());
                    throw new BuildException("xjc failed", getLocation());
                }
            } else {
                if (getCommandline().getVmCommand().size() > 1) {
                    log("JVM args ignored when same JVM is used.", Project.MSG_WARN);
                }
                if (stackSize == -1)
                    doXJC();   // just invoke XJC
                else {
                    try {
                        // launch XJC with a new thread so that we can set the stack size.
                        final Throwable[] e = new Throwable[1];

                        Thread t;
                        Runnable job = new Runnable() {
                            public void run() {
                                try {
                                    doXJC();
                                } catch (Throwable be) {
                                    e[0] = be;
                                }
                            }
                        };

                        try {
                            // this method is available only on JDK1.4
                            Constructor c = Thread.class.getConstructor(
                                ThreadGroup.class,
                                Runnable.class,
                                String.class,
                                long.class);
                            t = (Thread) c.newInstance(
                                Thread.currentThread().getThreadGroup(),
                                job,
                                Thread.currentThread().getName() + ":XJC",
                                stackSize);
                        } catch (Throwable err) {
                            // if fail, fall back.
                            log("Unable to set the stack size. Use JDK1.4 or above", Project.MSG_WARN);
                            doXJC();
                            return;
                        }

                        t.start();
                        t.join();
                        if (e[0] instanceof Error) throw (Error) e[0];
                        if (e[0] instanceof RuntimeException) throw (RuntimeException) e[0];
                        if (e[0] instanceof BuildException) throw (BuildException) e[0];
                        if (e[0] != null) throw new BuildException(e[0]);
                    } catch (InterruptedException e) {
                        throw new BuildException(e);
                    }
                }
            }
        } catch (BuildException e) {
            log("failure in the XJC task. Use the Ant -verbose switch for more details");
            if (failonerror)
                throw e;
            else {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                getProject().log(sw.toString(), Project.MSG_WARN);
                // continue
            }
        }
    }

    /**
     * Executes the given class name with the given arguments in a separate VM.
     *
     * @param command arguments.
     * @return return value from the executed process.
     */
    private int run(String[] command) throws BuildException {
        Execute exe;
        LogStreamHandler logstr = new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN);
        exe = new Execute(logstr);
        exe.setAntRun(getProject());
        exe.setCommandline(command);
        try {
            int rc = exe.execute();
            if (exe.killedProcess()) {
                log("Timeout: killed the sub-process", Project.MSG_WARN);
            }
            return rc;
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }

    private void doXJC() throws BuildException {
        ClassLoader old = SecureLoader.getContextClassLoader();
        AntClassLoader acl = null;
        try {
            if (classpath != null) {
                for (String pathElement : classpath.list()) {
                    try {
                        options.classpaths.add(new File(pathElement).toURI().toURL());
                    } catch (MalformedURLException ex) {
                        log("Classpath for XJC task not setup properly: " + pathElement);
                    }
                }
            }
            // set the user-specified class loader so that XJC will use it.
            // We have to specify parent classLoader because in other case AntClassLoader class classLoader will be set as parent
            // and we will lose current classLoader.
            SecureLoader.setContextClassLoader(
                acl = new AntClassLoader(this.getClass().getClassLoader(), getProject(), classpath, false));
            _doXJC();
        } finally {
            // restore the context class loader
            SecureLoader.setContextClassLoader(old);
            // close AntClassLoader
            if (acl != null) {
                acl.cleanup();
            }
            if (options.proxyAuth != null) {
                DefaultAuthenticator.reset();
            }
        }
    }

    private void _doXJC() throws BuildException {
        try {
            // parse additional command line params
            options.parseArguments(cmdLine.getArguments());
//            options.parseArguments(jvmarg.getArguments());
        } catch (BadCommandLineException e) {
            throw new BuildException(e.getMessage(), e);
        }

        if (xmlCatalog != null) {
            if (options.entityResolver == null) {
                options.entityResolver = xmlCatalog;
            } else {
                options.entityResolver = new ForkEntityResolver(options.entityResolver, xmlCatalog);
            }
        }

        if (!producesSpecified) {
            log("Consider using <depends>/<produces> so that XJC won't do unnecessary compilation", Project.MSG_INFO);
        }

        InputSource[] grammars = options.getGrammars();

        String msg = "Compiling " + grammars[0].getSystemId();
        if (grammars.length > 1) msg += " and others";
        log(msg, Project.MSG_INFO);

        if (removeOldOutput) {
            log("removing old output files", Project.MSG_INFO);
            for (File f : producesSet)
                f.delete();
        }

        // TODO: I don't know if I should send output to stdout
        ErrorReceiver errorReceiver = new XJCBase.ErrorReceiverImpl();

        Model model = ModelLoader.load(options, new JCodeModel(), errorReceiver);

        if (model == null)
            throw new BuildException("unable to parse the schema. Error messages should have been provided");

        try {

            if (model.generateCode(options, errorReceiver) == null)
                throw new BuildException("failed to compile a schema");

            log("Writing output to " + options.targetDir, Project.MSG_INFO);

            model.codeModel.build(new XJCBase.AntProgressCodeWriter(options.createCodeWriter()));
        } catch (IOException e) {
            throw new BuildException("unable to write files: " + e.getMessage(), e);
        }
    }

    /**
     * Determines the timestamp of the newest/oldest file in the given set.
     */
    private long computeTimestampFor(List<File> files, boolean findNewest) {

        long lastModified = findNewest ? Long.MIN_VALUE : Long.MAX_VALUE;

        for (File file : files) {
            log("Checking timestamp of " + file.toString(), Project.MSG_VERBOSE);

            if (findNewest)
                lastModified = Math.max(lastModified, file.lastModified());
            else
                lastModified = Math.min(lastModified, file.lastModified());
        }

        if (lastModified == Long.MIN_VALUE) // no file was found
            return Long.MAX_VALUE;  // force re-run

        if (lastModified == Long.MAX_VALUE) // no file was found
            return Long.MIN_VALUE;  // force re-run

        return lastModified;
    }

    /**
     * Extracts {@link File} objects that the given {@link FileSet}
     * represents and adds them all to the given {@link List}.
     */
    private void addIndividualFilesTo(FileSet fs, List<File> lst) {
        DirectoryScanner ds = fs.getDirectoryScanner(getProject());
        String[] includedFiles = ds.getIncludedFiles();
        File baseDir = ds.getBasedir();

        for (String value : includedFiles) {
            lst.add(new File(baseDir, value));
        }
    }

    /**
     * Extracts files in the given {@link FileSet}.
     */
    private InputSource[] toInputSources(FileSet fs) {
        DirectoryScanner ds = fs.getDirectoryScanner(getProject());
        String[] includedFiles = ds.getIncludedFiles();
        File baseDir = ds.getBasedir();

        ArrayList<InputSource> lst = new ArrayList<InputSource>();

        for (String value : includedFiles) {
            lst.add(getInputSource(new File(baseDir, value)));
        }

        return lst.toArray(new InputSource[lst.size()]);
    }

    /**
     * Converts a File object to an InputSource.
     */
    private InputSource getInputSource(File f) {
        try {
            return new InputSource(f.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            return new InputSource(f.getPath());
        }
    }

    /**
     * Converts an URL to an InputSource.
     */
    private InputSource getInputSource(URL url) {
        return Util.getInputSource(url.toExternalForm());
    }

    /**
     * {@link CodeWriter} that produces progress messages
     * as Ant verbose messages.
     */
    private class AntProgressCodeWriter extends FilterCodeWriter {
        public AntProgressCodeWriter(CodeWriter output) {
            super(output);
        }

        @Override
        public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
            if (pkg == null || pkg.isUnnamed())
                log("generating " + fileName, Project.MSG_VERBOSE);
            else
                log("generating " +
                    pkg.name().replace('.', File.separatorChar) +
                    File.separatorChar + fileName, Project.MSG_VERBOSE);

            return super.openBinary(pkg, fileName);
        }
    }

    /**
     * {@link ErrorReceiver} that produces messages
     * as Ant messages.
     */
    private class ErrorReceiverImpl extends ErrorReceiver {

        public void warning(SAXParseException e) {
            print(Project.MSG_WARN, Messages.WARNING_MSG, e);
        }

        public void error(SAXParseException e) {
            print(Project.MSG_ERR, Messages.ERROR_MSG, e);
        }

        public void fatalError(SAXParseException e) {
            print(Project.MSG_ERR, Messages.ERROR_MSG, e);
        }

        public void info(SAXParseException e) {
            print(Project.MSG_VERBOSE, Messages.INFO_MSG, e);
        }

        private void print(int logLevel, String header, SAXParseException e) {
            log(Messages.format(header, e.getMessage()), logLevel);
            log(getLocationString(e), logLevel);
            log("", logLevel);
        }
    }
}
