XJC {#tools-xjc}
===

`xjc` Overview
--------------

JAXB RI also provides an Ant task to run the binding complier - see the
instructions for [???](#tools-xjc-ant-task).

Launching `xjc` {#section-7316528525821393}
---------------

The binding compiler can be launched using the appropriate `xjc` shell
script in the `bin` directory for your platform.

-   **Solaris/Linux**

    ``` {.cli}
    % /path/to/jaxb/bin/xjc.sh -help
    ```

-   **Windows**

    ``` {.cli}
    > c:\path\to\jaxb\bin\xjc.bat -help
    ```

### Execute the `jaxb-xjc.jar` JAR File {#section-445618689309685}

If all else fails, you should be able to execute the `jaxb-xjc.jar`
file:

-   **Solaris/Linux**

    ``` {.cli}
    % java -jar $JAXB_HOME/lib/jaxb-xjc.jar -help
    ```

-   **Windows**

    ``` {.cli}
    > java -jar %JAXB_HOME%\lib\jaxb-xjc.jar -help
    ```

This is equivalent of running `xjc.sh` or `xjc.bat`, and it allows you
to set the JVM parameters.

`xjc` Syntax
------------

xjc

OPTION

schema file/URL/dir/jar

-b

binding

::: {.informalexample}
    Usage: xjc [-options ...] <schema file/URL/dir/jar> ... [-b <bindinfo>] ...
    If dir is specified, all schema files in it will be compiled.
    If jar is specified, /META-INF/sun-jaxb.episode binding file will be compiled.
    Options:
      -nv                :  do not perform strict validation of the input schema(s)
      -extension         :  allow vendor extensions - do not strictly follow the
                            Compatibility Rules and App E.2 from the JAXB Spec
      -b <file/dir>      :  specify external bindings files (each <file> must have its own -b)
                            If a directory is given, **/*.xjb is searched
      -d <dir>           :  generated files will go into this directory
      -p <pkg>           :  specifies the target package
      -httpproxy <proxy> :  set HTTP/HTTPS proxy. Format is [user[:password]@]proxyHost:proxyPort
      -httpproxyfile <f> :  Works like -httpproxy but takes the argument in a file to protect password 
      -classpath <arg>   :  specify where to find user class files
      -catalog <file>    :  specify catalog files to resolve external entity references
                            support TR9401, XCatalog, and OASIS XML Catalog format.
      -readOnly          :  generated files will be in read-only mode
      -npa               :  suppress generation of package level annotations (**/package-info.java)
      -no-header         :  suppress generation of a file header with timestamp
      -target (2.0|2.1)  :  behave like XJC 2.0 or 2.1 and generate code that doesn't use any 2.2 features.
      -encoding <encoding> :  specify character encoding for generated source files
      -enableIntrospection :  enable correct generation of Boolean getters/setters to enable Bean Introspection apis 
      -disableXmlSecurity  :  disables XML security features when parsing XML documents 
      -contentForWildcard  :  generates content property for types with multiple xs:any derived elements 
      -xmlschema         :  treat input as W3C XML Schema (default)
      -relaxng           :  treat input as RELAX NG (experimental,unsupported)
      -relaxng-compact   :  treat input as RELAX NG compact syntax (experimental,unsupported)
      -dtd               :  treat input as XML DTD (experimental,unsupported)
      -wsdl              :  treat input as WSDL and compile schemas inside it (experimental,unsupported)
      -verbose           :  be extra verbose
      -quiet             :  suppress compiler output
      -help              :  display this help message
      -version           :  display version information
      -fullversion       :  display full version information

    Extensions:
      -Xinject-code      :  inject specified Java code fragments into the generated code
      -Xlocator          :  enable source location support for generated code
      -Xsync-methods     :  generate accessor methods with the 'synchronized' keyword
      -mark-generated    :  mark the generated code as @javax.annotation.Generated
      -episode           :  generate the episode file for separate compilation
      -Xpropertyaccessors :  Use XmlAccessType PROPERTY instead of FIELD for generated classes
:::

### Summary of Command Line Options {#switches}

**-nv**

:   By default, the XJC binding compiler performs strict validation of
    the source schema before processing it. Use this option to disable
    strict schema validation. This does not mean that the binding
    compiler will not perform any validation, it simply means that it
    will perform less-strict validation.

**-extension**

:   By default, the XJC binding compiler strictly enforces the rules
    outlined in the Compatibility chapter of the JAXB Specification.
    Appendix E.2 defines a set of W3C XML Schema features that are not
    completely supported by JAXB v1.0. In some cases, you may be allowed
    to use them in the \"-extension\" mode enabled by this switch. In
    the default (strict) mode, you are also limited to using only the
    binding customizations defined in the specification. By using the
    \"-extension\" switch, you will be allowed to use the
    [???](#jaxb-ri-extensions-overview).

**-b \<file\>**

:   Specify one or more external binding files to process. (Each binding
    file must have it\'s own `-b` switch.) The syntax of the external
    binding files is extremely flexible. You may have a single binding
    file that contains customizations for multiple schemas or you can
    break the customizations into multiple bindings files:

    ::: {.informalexample}
        xjc schema1.xsd schema2.xsd schema3.xsd -b bindings123.xjb
        xjc schema1.xsd schema2.xsd schema3.xsd -b bindings1.xjb -b bindings2.xjb -b bindings3.xjb
    :::

    In addition, the ordering of the schema files and binding files on
    the command line does not matter.

**-d \<dir\>**

:   By default, the XJC binding compiler will generate the Java content
    classes in the current directory. Use this option to specify an
    alternate output directory. The output directory must already exist,
    the XJC binding compiler will not create it for you.

**-encoding \<encoding\>**

:   Set the encoding name for generated sources, such as EUC-JP or
    UTF-8. If `-encoding` is not specified, the platform default
    encoding is used.

**-p \<pkg\>**

:   Specifying a target package via this command-line option overrides
    any binding customization for package name and the default package
    name algorithm defined in the specification.

**-httpproxy \<proxy\>**

:   Specify the HTTP/HTTPS proxy. The format is
    \[user\[:password\]@\]proxyHost\[:proxyPort\]. The old `-host` and
    `-port` are still supported by the RI for backwards compatibility,
    but they have been deprecated.

**-httpproxyfile \<f\>**

:   Same as the `-httpproxy
                            <proxy>` option, but it takes the \<proxy\>
    parameter in a file, so that you can protect the password (passing a
    password in the argument list is not safe.)

**-classpath \<arg\>**

:   Specify where to find client application class files used by the
    `<jxb:javaType>` and `<xjc:superClass>` customizations.

**-catalog \<file\>**

:   Specify catalog files to resolve external entity references.
    Supports TR9401, XCatalog, and OASIS XML Catalog format. Please read
    the [XML Entity and URI Resolvers](catalog.html) document or the
    `catalog-resolver` sample application.

**-readOnly**

:   By default, the XJC binding compiler does not write-protect the Java
    source files it generates. Use this option to force the XJC binding
    compiler to mark the generated Java sources read-only.

**-npa**

:   Supress the generation of package level annotations into
    \*\*/package-info.java. Using this switch causes the generated code
    to internalize those annotations into the other generated classes.

**-no-header**

:   Supress the generation of a file header comment that includes some
    note and timestamp. Using this makes the generated code more
    `diff`-friendly.

**-target (2.0\|2.1)**

:   Avoid generating code that relies on any JAXB 2.1\|2.2 features.
    This will allow the generated code to run with JAXB 2.0 runtime
    (such as JavaSE 6.)

**-xmlschema**

:   treat input schemas as W3C XML Schema (default). If you do not
    specify this switch, your input schemas will be treated as W3C XML
    Schema.

**-relaxng**

:   Treat input schemas as RELAX NG (experimental, unsupported). Support
    for RELAX NG schemas is provided as a
    [???](#jaxb-ri-extensions-overview).

**-relaxng-compact**

:   Treat input schemas as RELAX NG compact syntax(experimental,
    unsupported). Support for RELAX NG schemas is provided as a
    [???](#jaxb-ri-extensions-overview).

**-dtd**

:   Treat input schemas as XML DTD (experimental, unsupported). Support
    for RELAX NG schemas is provided as a
    [???](#jaxb-ri-extensions-overview).

**-wsdl**

:   Treat input as WSDL and compile schemas inside it
    (experimental,unsupported).

**-quiet**

:   Suppress compiler output, such as progress information and
    warnings..

**-verbose**

:   Be extra verbose, such as printing informational messages or
    displaying stack traces upon some errors..

**-help**

:   Display a brief summary of the compiler switches.

**-version**

:   Display the compiler version information.

**\<schema file/URL/dir\>**

:   Specify one or more schema files to compile. If you specify a
    directory, then xjc will scan it for all schema files and compile
    them.

**-Xlocator**

:   This feature causes the generated code to expose SAX Locator
    information about the source XML in the Java bean instances after
    unmarshalling.

**-Xsync-methods**

:   This feature causes all of the generated method signatures to
    include the synchronized keyword.

**-mark-generated**

:   This feature causes all of the generated code to have
    [`@Generated`](http://docs.oracle.com/javaee/5/api/javax/annotation/Generated.html)
    annotation.

**-episode \<FILE\>**

:   Generate an episode file from this compilation, so that other
    schemas that rely on this schema can be compiled later and rely on
    classes that are generated from this compilation. The generated
    episode file is really just a JAXB customization file (but with
    vendor extensions.)

**-Xinject-code**

**-Xpropertyaccessors\>**

:   Annotate the `@XmlAccessorType` of generated classes with
    XmlAccessType PROPERTY instead of FIELD

### Summary of Deprecated and Removed Command Line Options {#section-3919972974137325}

**-host & -port**

:   These options have been deprecated and replaced with the
    **-httpproxy** option. For backwards compatibility, we will continue
    to support these options, but they will no longer be documented and
    may be removed from future releases.

**-use-runtime**

:   Since the JAXB 2.0 specification has defined a portable runtime, it
    is no longer necessary for the JAXB RI to generate \*\*/impl/runtime
    packages. Therefore, this switch is obsolete and has been removed.

Compiler Restrictions {#restrictions}
---------------------

In general, it is safest to compile all related schemas as a single unit
with the same binding compiler switches.

Please keep the following list of restrictions in mind when running xjc.
Most of these issues only apply when compiling multiple schemas with
multiple invocations of xjc.

-   To compile multiple schemas at the same time, keep the following
    precedence rules for the target Java package name in mind:

    1.  The `-p` command line option takes the highest precedence.

    2.  `<jaxb:package>` customization

    3.  If `targetNamespace` is declared, apply `targetNamespace` -\>
        Java package name algorithm defined in the specification.

    4.  If no `targetNamespace` is declared, use a hardcoded package
        named \"generated\".

-   It is not legal to have more than one \< `jaxb:schemaBindings`\> per
    namespace, so it is impossible to have two schemas in the same
    target namespace compiled into different Java packages.

-   All schemas being compiled into the same Java package must be
    submitted to the XJC binding compiler at the same time - they cannot
    be compiled independently and work as expected.

-   Element substitution groups spread across multiple schema files must
    be compiled at the same time.

Generated Resource Files {#xjcresources}
------------------------

XJC produces a set of packages containing Java source files and also
`jaxb.properties` files, depending on the binding options you used for
compilation. When generated, `jaxb.properties` files must be kept with
the compiled source code and made available on the runtime classpath of
your client applications:
